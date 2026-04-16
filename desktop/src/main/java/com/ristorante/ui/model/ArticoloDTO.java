package com.ristorante.ui.model;

import java.math.BigDecimal;

public class ArticoloDTO {

    private Long id;
    private String codice;
    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private String categoria;
    private boolean attivo;
    
    public ArticoloDTO(Long id, String codice, String nome,
            String descrizione, BigDecimal prezzo,
            String categoria, boolean attivo) {
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }
}
