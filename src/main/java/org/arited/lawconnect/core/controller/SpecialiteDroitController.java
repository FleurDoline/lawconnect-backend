package org.arited.lawconnect.core.controller;

import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.arited.lawconnect.core.repositories.SpecialiteDroitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/specialites")
@RequiredArgsConstructor
public class SpecialiteDroitController {

    private final SpecialiteDroitRepository specialiteDroitRepository;

    @GetMapping
    public List<SpecialiteDroit> search(@RequestParam(required = false, defaultValue = "") String query) {
        if (query.isBlank()) {
            return specialiteDroitRepository.findByNomContainingIgnoreCaseOrderByNomAsc("");
        }
        return specialiteDroitRepository.findByNomContainingIgnoreCaseOrderByNomAsc(query);
    }
}