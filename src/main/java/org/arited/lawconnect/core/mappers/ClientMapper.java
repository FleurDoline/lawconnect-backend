package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Request.ClientRequest;
import org.arited.lawconnect.core.dtos.Response.ClientResponse;
import org.arited.lawconnect.core.entities.Client;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "role", constant = "CLIENT")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "password", ignore = true)
    Client toEntity(ClientRequest request);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "isActive", source = "active")
    ClientResponse toResponse(Client client);
}