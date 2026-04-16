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
        
        if (utente.getUsername() == null || utente.getUsername().isBlank()
                || utente.getPassword() == null || utente.getPassword().isBlank()
                || utente.getNome() == null || utente.getNome().isBlank()
                || utente.getCognome() == null || utente.getCognome().isBlank()
                || utente.getRuolo() == null) {
            throw new RuntimeException("Compila tutti i campi obbligatori");
        }

        Ruolo ruolo = ruoloRepository.findById(utente.getRuolo().getId())
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));
        
        if (Boolean.FALSE.equals(ruolo.getAttivo())) {
            throw new RuntimeException("Non è possibile assegnare un ruolo disattivo");
        }

        utente.setRuolo(ruolo);
        
        String usernameNormalizzato = utente.getUsername().trim();
        String nomeNormalizzato = utente.getNome().trim();
        String cognomeNormalizzato = utente.getCognome().trim();
        String passwordNormalizzata = utente.getPassword().trim();
        
        if (usernameNormalizzato.length() < 3) {
            throw new RuntimeException("Lo username deve contenere almeno 3 caratteri");
        }

        if (usernameNormalizzato.contains(" ")) {
            throw new RuntimeException("Lo username non può contenere spazi");
        }

        if (passwordNormalizzata.length() < 6) {
            throw new RuntimeException("La password deve contenere almeno 6 caratteri");
        }
        
        if (utenteRepository.findByUsername(usernameNormalizzato).isPresent()) {
            throw new RuntimeException("Username già esistente");
        }
        
        utente.setUsername(usernameNormalizzato);
        utente.setNome(nomeNormalizzato);
        utente.setCognome(cognomeNormalizzato);
        utente.setPassword(passwordNormalizzata);

        return utenteRepository.save(utente);
    }
    
    public Utente update(Utente utente) {

        Utente esistente = utenteRepository.findById(utente.getId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (utente.getUsername() == null || utente.getUsername().isBlank()
                || utente.getNome() == null || utente.getNome().isBlank()
                || utente.getCognome() == null || utente.getCognome().isBlank()
                || utente.getRuolo() == null
                || utente.getRuolo().getId() == null) {
            throw new RuntimeException("Compila tutti i campi obbligatori");
        }

        String usernameNormalizzato = utente.getUsername().trim();
        String nomeNormalizzato = utente.getNome().trim();
        String cognomeNormalizzato = utente.getCognome().trim();

        if (usernameNormalizzato.length() < 3) {
            throw new RuntimeException("Lo username deve contenere almeno 3 caratteri");
        }

        if (usernameNormalizzato.contains(" ")) {
            throw new RuntimeException("Lo username non può contenere spazi");
        }

        utenteRepository.findByUsername(usernameNormalizzato)
                .ifPresent(trovato -> {
                    if (!trovato.getId().equals(esistente.getId())) {
                        throw new RuntimeException("Username già esistente");
                    }
                });

        Ruolo ruolo = ruoloRepository.findById(utente.getRuolo().getId())
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));
        
        if (Boolean.FALSE.equals(ruolo.getAttivo())) {
            throw new RuntimeException("Non è possibile assegnare un ruolo disattivo");
        }

        esistente.setUsername(usernameNormalizzato);
        esistente.setNome(nomeNormalizzato);
        esistente.setCognome(cognomeNormalizzato);
        esistente.setRuolo(ruolo);

        if (utente.getAttivo() != null) {
            esistente.setAttivo(utente.getAttivo());
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

