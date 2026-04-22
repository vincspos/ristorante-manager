package com.ristorante.manager.service;

import com.ristorante.manager.entity.Articolo;
import com.ristorante.manager.repository.ArticoloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticoloService {

    private final ArticoloRepository articoloRepository;

    public ArticoloService(ArticoloRepository articoloRepository) {
        this.articoloRepository = articoloRepository;
    }

    public List<Articolo> findAll() {
        return articoloRepository.findAll();
    }

    public Articolo save(Articolo articolo) {

        validateArticolo(articolo, null);

        articolo.setCodice(generaCodice());
        articolo.setNome(articolo.getNome().trim());
        articolo.setCategoria(articolo.getCategoria().trim());

        return articoloRepository.save(articolo);
    }

    public Articolo update(Long id, Articolo articoloAggiornato) {

        Articolo articolo = articoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Articolo non trovato con id: " + id));

        validateArticolo(articoloAggiornato, id);

        articolo.setNome(articoloAggiornato.getNome().trim());
        articolo.setDescrizione(articoloAggiornato.getDescrizione());
        articolo.setPrezzo(articoloAggiornato.getPrezzo());
        articolo.setCategoria(articoloAggiornato.getCategoria().trim());

        if (articoloAggiornato.getAttivo() != null) {
            articolo.setAttivo(articoloAggiornato.getAttivo());
        }

        return articoloRepository.save(articolo);
    }

    public Articolo updateStato(Long id, Boolean attivo) {

        Articolo articolo = articoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Articolo non trovato con id: " + id));

        articolo.setAttivo(attivo);

        return articoloRepository.save(articolo);
    }

    // ================= VALIDAZIONE =================

    private void validateArticolo(Articolo articolo, Long currentId) {

        if (articolo.getNome() == null || articolo.getNome().isBlank()
                || articolo.getPrezzo() == null
                || articolo.getCategoria() == null || articolo.getCategoria().isBlank()) {
            throw new RuntimeException("Compila tutti i campi obbligatori");
        }

        if (articolo.getPrezzo().doubleValue() < 0) {
            throw new RuntimeException("Il prezzo non può essere negativo");
        }

        String codiceNormalizzato = normalize(articolo.getCodice());

        articoloRepository.findByCodiceIgnoreCase(codiceNormalizzato)
                .ifPresent(esistente -> {
                    if (currentId == null || !esistente.getId().equals(currentId)) {
                        throw new RuntimeException("Codice articolo già esistente");
                    }
                });
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
    
    private String generaCodice() {
        long count = articoloRepository.count() + 1;
        return String.format("ART-%03d", count);
    }
}