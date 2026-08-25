package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Response.RessourceJuridiqueResponse;
import org.arited.lawconnect.core.entities.RessourceJuridique;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RessourceJuridiqueMapper {

    @Mapping(target = "specialiteNom", source = "specialite.nom")
    @Mapping(target = "specialiteId", source = "specialite.id")
    RessourceJuridiqueResponse toResponse(RessourceJuridique ressource);
}