package com.ristorante.manager.controller;

import com.ristorante.manager.dto.ArticoloRequest;
import com.ristorante.manager.dto.ArticoloResponse;
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
    public List<ArticoloResponse> getAll() {
        return articoloService.findAll();
    }

    @PostMapping
    public ArticoloResponse create(@RequestBody ArticoloRequest request) {
        return articoloService.save(request);
    }

    @PutMapping("/{id}")
    public ArticoloResponse update(@PathVariable Long id, @RequestBody ArticoloRequest request) {
        return articoloService.update(id, request);
    }

    @PatchMapping("/{id}/stato")
    public ArticoloResponse updateStato(@PathVariable Long id,
                                        @RequestBody Map<String, Boolean> payload) {
        Boolean attivo = payload.get("attivo");
        return articoloService.updateStato(id, attivo);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        articoloService.delete(id);
    }
}