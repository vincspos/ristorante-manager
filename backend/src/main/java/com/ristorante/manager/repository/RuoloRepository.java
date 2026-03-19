package com.ristorante.manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ristorante.manager.entity.Ruolo;

public interface RuoloRepository extends JpaRepository<Ruolo, Long> {

    Optional<Ruolo> findByCodice(String codice);
    
}
