package com.ristorante.manager.controller;

import java.util.List;
import java.util.Map;

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
    
    @PutMapping("/{id}")
    public Ruolo update(@PathVariable Long id, @RequestBody Ruolo ruolo) {
        return ruoloService.update(id, ruolo);
    }

    @PatchMapping("/{id}/stato")
    public Ruolo updateStato(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean attivo = payload.get("attivo");
        return ruoloService.updateStato(id, attivo);
    }
}
