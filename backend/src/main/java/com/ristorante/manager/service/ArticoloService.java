package com.ristorante.manager.service;

import com.ristorante.manager.dto.ArticoloRequest;
import com.ristorante.manager.dto.ArticoloResponse;
import com.ristorante.manager.dto.MovimentoMagazzinoResponse;
import com.ristorante.manager.entity.Articolo;
import com.ristorante.manager.entity.CategoriaArticolo;
import com.ristorante.manager.entity.MovimentoMagazzino;
import com.ristorante.manager.entity.TipoMovimentoMagazzino;
import com.ristorante.manager.exception.BadRequestException;
import com.ristorante.manager.exception.ResourceNotFoundException;
import com.ristorante.manager.repository.ArticoloRepository;
import com.ristorante.manager.repository.CategoriaArticoloRepository;
import com.ristorante.manager.repository.MovimentoMagazzinoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticoloService {

    private final ArticoloRepository articoloRepository;
    private final CategoriaArticoloRepository categoriaArticoloRepository;
    private final MovimentoMagazzinoRepository movimentoMagazzinoRepository;

    public ArticoloService(
            ArticoloRepository articoloRepository,
            CategoriaArticoloRepository categoriaArticoloRepository,
            MovimentoMagazzinoRepository movimentoMagazzinoRepository
    ) {
        this.articoloRepository = articoloRepository;
        this.categoriaArticoloRepository = categoriaArticoloRepository;
        this.movimentoMagazzinoRepository = movimentoMagazzinoRepository;
    }

    public List<ArticoloResponse> findAll() {
        return articoloRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ArticoloResponse save(ArticoloRequest request) {
        validateArticolo(request);

        CategoriaArticolo categoria = loadCategoria(request.getCategoriaId());
        
        boolean gestioneMagazzino = Boolean.TRUE.equals(request.getGestioneMagazzino());

        Articolo articolo = new Articolo();
        articolo.setCodice(generaCodice());
        articolo.setNome(request.getNome().trim());
        articolo.setDescrizione(normalizeNullable(request.getDescrizione()));
        articolo.setPrezzo(request.getPrezzo());
        articolo.setCategoria(categoria);
        articolo.setIva(request.getIva());
        articolo.setAttivo(request.getAttivo() != null ? request.getAttivo() : true);
        articolo.setGestioneMagazzino(gestioneMagazzino);
        articolo.setQuantitaDisponibile(request.getQuantitaDisponibile() != null ? request.getQuantitaDisponibile() : 0);
        articolo.setSogliaWarning(request.getSogliaWarning() != null ? request.getSogliaWarning() : 0);

        return toResponse(articoloRepository.save(articolo));
    }

    public ArticoloResponse update(Long id, ArticoloRequest request) {
        Articolo articolo = articoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Articolo non trovato con id: " + id));

        validateArticolo(request);
        
        boolean gestioneMagazzino = Boolean.TRUE.equals(request.getGestioneMagazzino());
        
        Integer vecchiaQuantita = articolo.getQuantitaDisponibile() != null
                ? articolo.getQuantitaDisponibile()
                : 0;

        Integer nuovaQuantitaRichiesta = request.getQuantitaDisponibile() != null
                ? request.getQuantitaDisponibile()
                : 0;

        int deltaMagazzino = nuovaQuantitaRichiesta - vecchiaQuantita;

        CategoriaArticolo categoria = loadCategoria(request.getCategoriaId());

        articolo.setNome(request.getNome().trim());
        articolo.setDescrizione(normalizeNullable(request.getDescrizione()));
        articolo.setPrezzo(request.getPrezzo());
        articolo.setCategoria(categoria);
        articolo.setIva(request.getIva());
        articolo.setGestioneMagazzino(gestioneMagazzino);
        articolo.setQuantitaDisponibile(request.getQuantitaDisponibile());
        articolo.setSogliaWarning(request.getSogliaWarning());
        
        if (gestioneMagazzino && deltaMagazzino != 0) {
            MovimentoMagazzino movimento = new MovimentoMagazzino();
            movimento.setArticolo(articolo);
            movimento.setTipo(deltaMagazzino > 0
                    ? TipoMovimentoMagazzino.CARICO
                    : TipoMovimentoMagazzino.SCARICO_MANUALE);
            movimento.setQuantita(Math.abs(deltaMagazzino));
            movimento.setNote("Aggiornamento quantità da scheda articolo");

            movimentoMagazzinoRepository.save(movimento);
        }

        if (request.getAttivo() != null) {
            articolo.setAttivo(request.getAttivo());
        }

        return toResponse(articoloRepository.save(articolo));
    }

    public ArticoloResponse updateStato(Long id, Boolean attivo) {
        Articolo articolo = articoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Articolo non trovato con id: " + id));

        articolo.setAttivo(attivo);
        return toResponse(articoloRepository.save(articolo));
    }
    
    public void delete(Long id) {
        Articolo articolo = articoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Articolo non trovato con id: " + id));

        articoloRepository.delete(articolo);
    }
    
    public ArticoloResponse updateQuantita(Long id, Integer delta, Long utenteId, String utenteUsername) {
        Articolo articolo = articoloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Articolo non trovato con id: " + id));

        if (!Boolean.TRUE.equals(articolo.getGestioneMagazzino())) {
            throw new BadRequestException("Magazzino non gestito per questo articolo");
        }

        if (delta == null) {
            throw new BadRequestException("Quantità non valida");
        }

        int nuovaQuantita = articolo.getQuantitaDisponibile() + delta;

        if (nuovaQuantita < 0) {
            throw new BadRequestException("La quantità non può scendere sotto zero");
        }

        articolo.setQuantitaDisponibile(nuovaQuantita);
        
        MovimentoMagazzino movimento = new MovimentoMagazzino();

        movimento.setArticolo(articolo);

        movimento.setTipo(
                delta >= 0
                        ? TipoMovimentoMagazzino.CARICO
                		: TipoMovimentoMagazzino.SCARICO_MANUALE
        );

        movimento.setQuantita(Math.abs(delta));

        movimento.setNote("Aggiornamento rapido quantità");
        
        movimento.setUtenteId(utenteId);
        movimento.setUtenteUsername(utenteUsername);

        movimentoMagazzinoRepository.save(movimento);

        return toResponse(articoloRepository.save(articolo));
    }
    
    public List<MovimentoMagazzinoResponse> findMovimentiByArticolo(Long articoloId) {
        articoloRepository.findById(articoloId)
                .orElseThrow(() -> new ResourceNotFoundException("Articolo non trovato con id: " + articoloId));

        return movimentoMagazzinoRepository.findByArticoloIdOrderByDataMovimentoDesc(articoloId)
                .stream()
                .map(m -> new MovimentoMagazzinoResponse(
                        m.getId(),
                        m.getArticolo().getNome(),
                        m.getTipo().name(),
                        m.getQuantita(),
                        m.getNote(),
                        m.getDataMovimento(),
                        m.getUtenteId(),
                        m.getUtenteUsername()
                ))
                .toList();
    }

    private void validateArticolo(ArticoloRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()
                || request.getPrezzo() == null
                || request.getCategoriaId() == null
                || request.getIva() == null) {
            throw new BadRequestException("Compila tutti i campi obbligatori");
        }

        if (request.getPrezzo().doubleValue() < 0) {
            throw new BadRequestException("Il prezzo non può essere negativo");
        }
        
        boolean gestioneMagazzino = Boolean.TRUE.equals(request.getGestioneMagazzino());

        
        if (gestioneMagazzino) {
            if (request.getQuantitaDisponibile() == null || request.getSogliaWarning() == null) {
                throw new BadRequestException("Compila i dati di magazzino");
            }

            if (request.getQuantitaDisponibile() < 0) {
                throw new BadRequestException("La quantità disponibile non può essere negativa");
            }

            if (request.getSogliaWarning() < 0) {
                throw new BadRequestException("La soglia warning non può essere negativa");
            }
        }
    }

    private CategoriaArticolo loadCategoria(Long categoriaId) {
        return categoriaArticoloRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata con id: " + categoriaId));
    }

    private ArticoloResponse toResponse(Articolo articolo) {
        Long categoriaId = articolo.getCategoria() != null ? articolo.getCategoria().getId() : null;
        String categoriaNome = articolo.getCategoria() != null ? articolo.getCategoria().getNome() : null;

        return new ArticoloResponse(
                articolo.getId(),
                articolo.getCodice(),
                articolo.getNome(),
                articolo.getDescrizione(),
                articolo.getPrezzo(),
                categoriaId,
                categoriaNome,
                Boolean.TRUE.equals(articolo.getAttivo()),
                articolo.getIva(),
                articolo.getQuantitaDisponibile(),
                articolo.getSogliaWarning(),
                articolo.getStatoMagazzino(),
                Boolean.TRUE.equals(articolo.getGestioneMagazzino())
        );
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String generaCodice() {
		Long maxId = articoloRepository.findMaxId();
	    long next = (maxId == null ? 1 : maxId + 1);
	    return String.format("ART-%03d", next);
    }
}