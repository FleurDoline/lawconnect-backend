package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AdminCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AdminUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AdminResponse;
import org.arited.lawconnect.core.entities.Admin;
import org.arited.lawconnect.core.enums.AccesEnum;
import org.arited.lawconnect.core.enums.AuthProvider;
import org.arited.lawconnect.core.enums.RoleEnum;
import org.arited.lawconnect.core.exceptions.DuplicateResourceException;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.mappers.AdminMapper;
import org.arited.lawconnect.core.repositories.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest request) {
        log.info("Creating admin account for email={}", request.getEmail());

        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "Un compte admin avec l'email " + request.getEmail() + " existe déjà"
            );
        }
        Admin admin = adminMapper.toEntity(request);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(RoleEnum.ADMIN);
        admin.setProvider(AuthProvider.LOCAL);
        admin.setActive(true);

        Admin saved = adminRepository.save(admin);
        log.info("Admin created with userId={}", saved.getUserId());
        return adminMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getAdminById(Long id) {
        log.info("Fetching admin by id={}", id);
        return adminMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getAdminByUserId(Long userId) {
        log.info("Fetching admin by userId={}", userId);
        Admin admin = adminRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Admin introuvable avec userId=" + userId
            ));
        return adminMapper.toResponse(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminResponse> getAllAdmins(int page, int size) {
        log.info("Fetching all admins — page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminResponse> result = adminRepository.findAll(pageable)
            .map(adminMapper::toResponse);
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAdminsByNiveauAcces(AccesEnum niveauAcces) {
        log.info("Fetching admins by niveauAcces={}", niveauAcces);
        return adminRepository.findByNiveauAcces(niveauAcces).stream()
            .map(adminMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(Long id, AdminUpdateRequest request) {
        log.info("Updating admin id={}", id);
        Admin admin = findOrThrow(id);
        adminMapper.updateEntity(request, admin);
        Admin updated = adminRepository.save(admin);
        return adminMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        log.info("Deleting admin id={}", id);
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin introuvable avec id=" + id);
        }
        adminRepository.deleteById(id);
        log.info("Admin id={} deleted", id);
    }

    private Admin findOrThrow(Long id) {
        return adminRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Admin introuvable avec id=" + id
            ));
    }
}
