package com.ristorante.manager.repository;

import com.ristorante.manager.entity.MovimentoMagazzino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentoMagazzinoRepository
        extends JpaRepository<MovimentoMagazzino, Long> {

    List<MovimentoMagazzino> findByArticoloIdOrderByDataMovimentoDesc(Long articoloId);
    
    List<MovimentoMagazzino> findAllByOrderByDataMovimentoDesc();
}
