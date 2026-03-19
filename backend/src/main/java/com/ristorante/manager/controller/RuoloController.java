package com.ristorante.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ristorante.manager.entity.Ruolo;
import com.ristorante.manager.service.RuoloService;

@RestController
@RequestMapping("/api/ruoli")
public class RuoloController {

    private final RuoloService ruoloService;

    public RuoloController(RuoloService ruoloService) {
        this.ruoloService = ruoloService;
    }

    @GetMapping
    public List<Ruolo> getAll() {
        return ruoloService.findAll();
    }

    @PostMapping
    public Ruolo create(@RequestBody Ruolo ruolo) {
        return ruoloService.save(ruolo);
    }
}
