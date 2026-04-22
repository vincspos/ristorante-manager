package com.ristorante.manager.repository;

import com.ristorante.manager.entity.Articolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticoloRepository extends JpaRepository<Articolo, Long> {

    boolean existsByCodiceIgnoreCase(String codice);

    Optional<Articolo> findByCodiceIgnoreCase(String codice);
    
    @Query("SELECT MAX(a.id) FROM Articolo a")
    Long findMaxId();
}
