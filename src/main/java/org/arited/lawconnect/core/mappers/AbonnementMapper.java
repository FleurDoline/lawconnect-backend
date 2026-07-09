package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Request.AbonnementRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementResponse;
import org.arited.lawconnect.core.entities.Abonnement;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AbonnementMapper {

    /**
     * Convertit un AbonnementRequest en entité Abonnement
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "avocat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "prochainRenouvellement", source = "prochainRenouvellement")
    Abonnement toEntity(AbonnementRequest request);

    /**
     * Met à jour une entité existante avec les données du request
     * Seuls les champs non-nuls sont mis à jour
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "avocat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(AbonnementRequest request, @MappingTarget Abonnement abonnement);

    /**
     * Convertit une entité Abonnement en AbonnementResponse
     */
    @Mapping(target = "avocatId", source = "avocat.userId")
    @Mapping(target = "avocatFullName", source = "avocat.fullName")
    @Mapping(target = "avocatEmail", source = "avocat.email")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    AbonnementResponse toResponse(Abonnement abonnement);
}