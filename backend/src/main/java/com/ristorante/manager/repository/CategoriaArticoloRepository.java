package com.ristorante.manager.repository;

import com.ristorante.manager.entity.CategoriaArticolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaArticoloRepository extends JpaRepository<CategoriaArticolo, Long> {

    Optional<CategoriaArticolo> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
