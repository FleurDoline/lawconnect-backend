package org.arited.lawconnect.core.controller;
import org.arited.lawconnect.core.entities.EtCity;
import org.arited.lawconnect.core.repositories.EtCityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api/villes")
public class VilleController {

    @Autowired
    private EtCityRepository repository;

    @GetMapping
    public List<EtCity> getAll() {
        return repository.findByActiveTrueOrderByCityNameAsc();
    }
}