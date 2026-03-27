package com.ristorante.manager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ristorante.manager.entity.Utente;
import com.ristorante.manager.service.UtenteService;
import com.ristorante.manager.dto.ErrorResponse;
import com.ristorante.manager.dto.LoginRequest;
import com.ristorante.manager.dto.LoginResponse;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {
	
	private final UtenteService utenteService;

	 public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping
    public List<Utente> getAll() {
        return utenteService.findAll();
    }

    @PostMapping
    public Utente create(@RequestBody Utente utente) {
        return utenteService.save(utente);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = utenteService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenziali non valide"));
        }
    }
    
    @PutMapping("/{id}")
    public Utente update(@PathVariable Long id, @RequestBody Utente utente) {
        utente.setId(id);
        return utenteService.update(utente);
    }
    
    @PatchMapping("/{id}/stato")
    public Utente updateStato(@PathVariable Long id, @RequestBody Utente utente) {
        return utenteService.updateStato(id, utente.getAttivo());
    }
}
