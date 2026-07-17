package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Response.CityResponse;
import org.arited.lawconnect.core.repositories.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityResponse> searchCities(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }
        return cityRepository.findByCityNameStartingWith(prefix.trim())
                .stream()
                .map(c -> new CityResponse(c.getId(), c.getCityName(), c.getCityRegion()))
                .collect(Collectors.toList());
    }
}