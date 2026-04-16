package com.ristorante.manager.controller;

import com.ristorante.manager.entity.CategoriaArticolo;
import com.ristorante.manager.service.CategoriaArticoloService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorie-articoli")
@CrossOrigin(origins = "*")
public class CategoriaArticoloController {

    private final CategoriaArticoloService categoriaArticoloService;

    public CategoriaArticoloController(CategoriaArticoloService categoriaArticoloService) {
        this.categoriaArticoloService = categoriaArticoloService;
    }

    @GetMapping
    public List<CategoriaArticolo> getAll() {
        return categoriaArticoloService.findAll();
    }

    @GetMapping("/attive")
    public List<CategoriaArticolo> getAttive() {
        return categoriaArticoloService.findAttive();
    }

    @PostMapping
    public CategoriaArticolo create(@RequestBody CategoriaArticolo categoriaArticolo) {
        return categoriaArticoloService.save(categoriaArticolo);
    }

    @PutMapping("/{id}")
    public CategoriaArticolo update(@PathVariable Long id, @RequestBody CategoriaArticolo categoriaArticolo) {
        return categoriaArticoloService.update(id, categoriaArticolo);
    }

    @PatchMapping("/{id}/stato")
    public CategoriaArticolo updateStato(@PathVariable Long id,
                                         @RequestBody Map<String, Boolean> payload) {
        Boolean attivo = payload.get("attivo");
        return categoriaArticoloService.updateStato(id, attivo);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoriaArticoloService.delete(id);
    }
}
