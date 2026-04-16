package com.ristorante.manager.service;

import com.ristorante.manager.entity.CategoriaArticolo;
import com.ristorante.manager.repository.CategoriaArticoloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaArticoloService {

    private final CategoriaArticoloRepository categoriaArticoloRepository;

    public CategoriaArticoloService(CategoriaArticoloRepository categoriaArticoloRepository) {
        this.categoriaArticoloRepository = categoriaArticoloRepository;
    }

    public List<CategoriaArticolo> findAll() {
        return categoriaArticoloRepository.findAll();
    }

    public List<CategoriaArticolo> findAttive() {
        return categoriaArticoloRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getAttivo()))
                .toList();
    }

    public CategoriaArticolo save(CategoriaArticolo categoriaArticolo) {
        validateCategoria(categoriaArticolo, null);

        categoriaArticolo.setNome(normalizeNome(categoriaArticolo.getNome()));
        categoriaArticolo.setColore(normalizeColore(categoriaArticolo.getColore()));

        if (categoriaArticolo.getAttivo() == null) {
            categoriaArticolo.setAttivo(true);
        }

        return categoriaArticoloRepository.save(categoriaArticolo);
    }

    public CategoriaArticolo update(Long id, CategoriaArticolo categoriaAggiornata) {
        CategoriaArticolo categoria = categoriaArticoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria articolo non trovata con id: " + id));

        validateCategoria(categoriaAggiornata, id);

        categoria.setNome(normalizeNome(categoriaAggiornata.getNome()));
        categoria.setColore(normalizeColore(categoriaAggiornata.getColore()));

        if (categoriaAggiornata.getAttivo() != null) {
            categoria.setAttivo(categoriaAggiornata.getAttivo());
        }

        return categoriaArticoloRepository.save(categoria);
    }

    public CategoriaArticolo updateStato(Long id, Boolean attivo) {
        CategoriaArticolo categoria = categoriaArticoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria articolo non trovata con id: " + id));

        categoria.setAttivo(attivo);
        return categoriaArticoloRepository.save(categoria);
    }

    public void delete(Long id) {
        CategoriaArticolo categoria = categoriaArticoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria articolo non trovata con id: " + id));

        categoriaArticoloRepository.delete(categoria);
    }

    private void validateCategoria(CategoriaArticolo categoria, Long currentId) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new RuntimeException("Il nome categoria è obbligatorio");
        }

        if (categoria.getColore() == null || categoria.getColore().isBlank()) {
            throw new RuntimeException("Il colore categoria è obbligatorio");
        }

        String nomeNormalizzato = normalizeNome(categoria.getNome());

        categoriaArticoloRepository.findByNomeIgnoreCase(nomeNormalizzato)
                .ifPresent(esistente -> {
                    if (currentId == null || !esistente.getId().equals(currentId)) {
                        throw new RuntimeException("Esiste già una categoria con questo nome");
                    }
                });

        String coloreNormalizzato = normalizeColore(categoria.getColore());
        if (!coloreNormalizzato.matches("^#[0-9A-F]{6}$")) {
            throw new RuntimeException("Il colore deve essere in formato esadecimale, ad esempio #DC2626");
        }
    }

    private String normalizeNome(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeColore(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}