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

}
