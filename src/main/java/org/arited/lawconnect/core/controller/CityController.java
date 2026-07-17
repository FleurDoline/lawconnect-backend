package org.arited.lawconnect.core.controller;

import org.arited.lawconnect.core.dtos.Response.CityResponse;
import org.arited.lawconnect.core.services.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<CityResponse>> search(@RequestParam("q") String query) {
        return ResponseEntity.ok(cityService.searchCities(query));
    }
}