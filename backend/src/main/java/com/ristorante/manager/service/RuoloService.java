package com.ristorante.manager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ristorante.manager.entity.Ruolo;
import com.ristorante.manager.repository.RuoloRepository;

@Service
public class RuoloService {
	
	private final RuoloRepository ruoloRepository;
	
	public RuoloService(RuoloRepository ruoloRepository) {
        this.ruoloRepository = ruoloRepository;
    }

    public List<Ruolo> findAll() {
        return ruoloRepository.findAll();
    }

    public Ruolo save(Ruolo ruolo) {
        return ruoloRepository.save(ruolo);
    }
    
    public Ruolo update(Long id, Ruolo ruoloAggiornato) {
        Ruolo ruolo = ruoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato con id: " + id));

        ruolo.setCodice(ruoloAggiornato.getCodice());
        ruolo.setDescrizione(ruoloAggiornato.getDescrizione());

        if (ruoloAggiornato.getAttivo() != null) {
            ruolo.setAttivo(ruoloAggiornato.getAttivo());
        }

        return ruoloRepository.save(ruolo);
    }

    public Ruolo updateStato(Long id, Boolean attivo) {
        Ruolo ruolo = ruoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato con id: " + id));
        
        if ("ADMIN".equalsIgnoreCase(ruolo.getCodice()) && Boolean.FALSE.equals(attivo)) {
            throw new RuntimeException("Il ruolo ADMIN non può essere disattivato");
        }

        ruolo.setAttivo(attivo);
        return ruoloRepository.save(ruolo);
    }

}
