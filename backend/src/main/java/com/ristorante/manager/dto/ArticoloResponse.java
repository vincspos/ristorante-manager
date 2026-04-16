package com.ristorante.manager.dto;

import java.math.BigDecimal;

public class ArticoloResponse {

    private Long id;
    private String codice;
    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private String categoria;
    private boolean attivo;

    public ArticoloResponse() {
    }

    public ArticoloResponse(Long id, String codice, String nome, String descrizione,
                            BigDecimal prezzo, String categoria, boolean attivo) {
        this.id = id;
        this.codice = codice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.attivo = attivo;
    }

    public Long getId() {
        return id;
    }

    public String getCodice() {
        return codice;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isAttivo() {
        return attivo;
    }
}