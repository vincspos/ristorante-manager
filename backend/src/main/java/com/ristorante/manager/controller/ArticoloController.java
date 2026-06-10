package com.ristorante.manager.controller;

import com.ristorante.manager.dto.ArticoloRequest;
import com.ristorante.manager.dto.ArticoloResponse;
import com.ristorante.manager.dto.MovimentoMagazzinoResponse;
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
    
    @PatchMapping("/{id}/quantita")
    public ArticoloResponse updateQuantita(@PathVariable Long id,
                                           @RequestBody Map<String, Object> payload) {
        Integer delta = (Integer) payload.get("delta");

        Long utenteId = payload.get("utenteId") != null
                ? Long.valueOf(payload.get("utenteId").toString())
                : null;

        String utenteUsername = payload.get("utenteUsername") != null
                ? payload.get("utenteUsername").toString()
                : null;

        return articoloService.updateQuantita(id, delta, utenteId, utenteUsername);
    }
    
    @GetMapping("/{id}/movimenti")
    public List<MovimentoMagazzinoResponse> getMovimentiArticolo(@PathVariable Long id) {
        return articoloService.findMovimentiByArticolo(id);
    }
}