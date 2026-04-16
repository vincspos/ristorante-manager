package com.ristorante.manager.controller;

import com.ristorante.manager.entity.Articolo;
import com.ristorante.manager.service.ArticoloService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articoli")
@CrossOrigin(origins = "*")
public class ArticoloController {

    private final ArticoloService articoloService;

    public ArticoloController(ArticoloService articoloService) {
        this.articoloService = articoloService;
    }

    @GetMapping
    public List<Articolo> getAll() {
        return articoloService.findAll();
    }

    @PostMapping
    public Articolo create(@RequestBody Articolo articolo) {
        return articoloService.save(articolo);
    }

    @PutMapping("/{id}")
    public Articolo update(@PathVariable Long id, @RequestBody Articolo articolo) {
        return articoloService.update(id, articolo);
    }

    @PatchMapping("/{id}/stato")
    public Articolo updateStato(@PathVariable Long id,
                               @RequestBody Map<String, Boolean> payload) {

        Boolean attivo = payload.get("attivo");
        return articoloService.updateStato(id, attivo);
    }
}