package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.ClientRequest;
import org.arited.lawconnect.core.dtos.Response.ClientResponse;
import org.arited.lawconnect.core.entities.Client;
import org.arited.lawconnect.core.exceptions.DuplicateResourceException;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.mappers.ClientMapper;
import org.arited.lawconnect.core.repositories.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    // Called by auth-service via OpenFeign after account creation
    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        log.info("Creating client profile for userId={}", request.getUserId());

        // Guard: userId already linked to a profile
        if (clientRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateResourceException(
                "Un profil client existe déjà pour userId=" + request.getUserId()
            );
        }

        // Guard: email already taken
        if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(
                "Un compte avec l'email " + request.getEmail() + " existe déjà"
            );
        }

        // Map request → entity, then hash password
        Client client = clientMapper.toEntity(request);
        client.setPassword(passwordEncoder.encode(request.getPassword()));

        Client saved = clientRepository.save(client);
        log.info("Client profile created with id={}", saved.getUserId());

        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientByUserId(Long userId) {
        log.info("Fetching client by userId={}", userId);
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Client introuvable avec userId=" + userId
                ));
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClientResponse> getAllClients(int page, int size) {
        log.info("Fetching all clients — page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ClientResponse> result = clientRepository.findAll(pageable)
                .map(clientMapper::toResponse);
        return PageResponse.of(result);
    }

    @Override
    @Transactional
    public ClientResponse updateClient(Long id, ClientRequest request) {
        log.info("Updating client id={}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Client introuvable avec id=" + id
                ));

        // Only update email if it changed and not already taken
        if (!client.getEmail().equals(request.getEmail()) &&
             clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(
                "L'email " + request.getEmail() + " est déjà utilisé"
            );
        }

        client.setFullName(request.getPrenom());
        client.setEmail(request.getEmail());
        // Only re-hash if password was actually changed
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Client updated = clientRepository.save(client);
        log.info("Client id={} updated successfully", updated.getUserId());

        return clientMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        log.info("Deleting client id={}", id);
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Client introuvable avec id=" + id
            );
        }
        clientRepository.deleteById(id);
        log.info("Client id={} deleted", id);
    }
}