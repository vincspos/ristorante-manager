package com.ristorante.manager.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.ristorante.manager.entity.Ruolo;
import com.ristorante.manager.entity.Utente;
import com.ristorante.manager.repository.RuoloRepository;
import com.ristorante.manager.repository.UtenteRepository;

import com.ristorante.manager.dto.LoginRequest;
import com.ristorante.manager.dto.LoginResponse;

@Service
public class UtenteService {
	
	private final UtenteRepository utenteRepository;
    private final RuoloRepository ruoloRepository;

    public UtenteService(UtenteRepository utenteRepository, RuoloRepository ruoloRepository) {
        this.utenteRepository = utenteRepository;
        this.ruoloRepository = ruoloRepository;
    }

    public List<Utente> findAll() {
        return utenteRepository.findAll();
    }

    public Utente save(Utente utente) {
        if (utente.getRuolo() == null || utente.getRuolo().getId() == null) {
            throw new RuntimeException("Ruolo obbligatorio");
        }

        Ruolo ruolo = ruoloRepository.findById(utente.getRuolo().getId())
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));

        utente.setRuolo(ruolo);

        return utenteRepository.save(utente);
    }
    
    public Utente update(Utente utente) {

        Utente esistente = utenteRepository.findById(utente.getId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        esistente.setUsername(utente.getUsername());
        esistente.setNome(utente.getNome());
        esistente.setCognome(utente.getCognome());

        if (utente.getRuolo() != null && utente.getRuolo().getId() != null) {
            Ruolo ruolo = ruoloRepository.findById(utente.getRuolo().getId())
                    .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));
            esistente.setRuolo(ruolo);
        }

        return utenteRepository.save(esistente);
    }
    
    public Utente updateStato(Long id, Boolean attivo) {

        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        utente.setAttivo(attivo);

        return utenteRepository.save(utente);
    }
    
    public LoginResponse login(LoginRequest request) {
        Utente utente = utenteRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!utente.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        if (Boolean.FALSE.equals(utente.getAttivo())) {
            throw new RuntimeException("Utente disattivato");
        }

        LoginResponse response = new LoginResponse();
        response.setId(utente.getId());
        response.setUsername(utente.getUsername());
        response.setNome(utente.getNome());
        response.setCognome(utente.getCognome());
        response.setAttivo(utente.getAttivo());
        response.setRuolo(utente.getRuolo().getCodice());
        response.setHomePage(calcolaHomePage(utente.getRuolo().getCodice()));
        response.setModuli(calcolaModuli(utente.getRuolo().getCodice()));
        response.setMessaggio("Login effettuato con successo");

        return response;
    }
    
    private String calcolaHomePage(String ruolo) {
        return switch (ruolo) {
            case "ADMIN" -> "HOME_ADMIN";
            case "CASSA" -> "HOME_CASSA";
            case "SALA" -> "HOME_SALA";
            case "CUCINA" -> "HOME_CUCINA";
            case "PIZZERIA" -> "HOME_PIZZERIA";
            case "RIDER" -> "HOME_RIDER";
            default -> "HOME_DEFAULT";
        };
    }
    
    private List<String> calcolaModuli(String ruolo) {
        return switch (ruolo) {
            case "ADMIN" -> List.of(
                    "UTENTI",
                    "RUOLI",
                    "ARTICOLI",
                    "TAVOLI",
                    "ORDINI",
                    "ASPORTO",
                    "DOMICILIO",
                    "INCASSI",
                    "SPESE",
                    "REPORT"
            );
            case "CASSA" -> List.of(
                    "TAVOLI",
                    "ORDINI",
                    "ASPORTO",
                    "DOMICILIO",
                    "PAGAMENTI",
                    "INCASSI"
            );
            case "SALA" -> List.of(
                    "TAVOLI",
                    "ORDINI",
                    "RICHIESTA_CONTO"
            );
            case "CUCINA" -> List.of(
                    "COMANDE",
                    "RICHIESTE_CUCINA"
            );
            case "PIZZERIA" -> List.of(
                    "COMANDE_PIZZERIA",
                    "RICHIESTE_PIZZERIA"
            );
            case "RIDER" -> List.of(
                    "CONSEGNE",
                    "ORDINI_DOMICILIO",
                    "STATO_CONSEGNA"
            );
            default -> List.of("HOME");
        };
    }
}

