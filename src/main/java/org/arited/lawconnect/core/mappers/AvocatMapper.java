package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Request.AvocatCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AvocatUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AvocatResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatSummaryResponse;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AvocatMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "role", constant = "AVOCAT")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "noteMoyenne", ignore = true)
    @Mapping(target = "progression", ignore = true)
    @Mapping(target = "validAt", ignore = true)
    @Mapping(target = "validBy", ignore = true)
    @Mapping(target = "abonnements", ignore = true)
    @Mapping(target = "lienAgenda", ignore = true)
    @Mapping(target = "specialites", ignore = true) 
    @Mapping(target = "carteProfessionnel", ignore = true)
    @Mapping(target = "diplome", ignore = true)
    @Mapping(target = "pieceIdentite", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "tarif", ignore = true)
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "adresseCabinet", ignore = true)
    @Mapping(target = "ville", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "authorities", ignore = true)

    Avocat toEntity(AvocatCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "noteMoyenne", ignore = true)
    @Mapping(target = "progression", ignore = true)
    @Mapping(target = "validAt", ignore = true)
    @Mapping(target = "validBy", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "abonnements", ignore = true)
    @Mapping(target = "specialites", ignore = true)

    void updateEntity(AvocatUpdateRequest request, @MappingTarget Avocat avocat);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "specialites", source = "specialites", qualifiedByName = "specialitesToNoms")
    AvocatResponse toResponse(Avocat avocat);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "specialites", source = "specialites", qualifiedByName = "specialitesToNoms")
    AvocatSummaryResponse toSummaryResponse(Avocat avocat);

    @Named("specialitesToNoms")
    default List<String> specialitesToNoms(Set<SpecialiteDroit> specialites) {
        if (specialites == null) return List.of();
        return specialites.stream()
                .map(SpecialiteDroit::getNom)
                .collect(Collectors.toList());
    }
}