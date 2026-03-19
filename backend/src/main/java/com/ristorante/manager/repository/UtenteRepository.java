package com.ristorante.manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ristorante.manager.entity.Utente;


public interface UtenteRepository extends JpaRepository<Utente, Long> {
	
	Optional<Utente> findByUsername(String username);

}
