package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.ClientRequest;
import org.arited.lawconnect.core.dtos.Response.ClientResponse;

public interface ClientService {
    ClientResponse createClient(ClientRequest request);
    ClientResponse getClientByUserId(Long userId);
    PageResponse<ClientResponse> getAllClients(int page, int size);
    ClientResponse updateClient(Long id, ClientRequest request);
    void deleteClient(Long id);
}