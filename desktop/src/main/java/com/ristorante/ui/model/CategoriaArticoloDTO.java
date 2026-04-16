package com.ristorante.ui.model;

public class CategoriaArticoloDTO {

    private Long id;
    private String nome;
    private String colore;
    private boolean attivo;

    public CategoriaArticoloDTO(Long id, String nome, String colore, boolean attivo) {
        this.id = id;
        this.nome = nome;
        this.colore = colore;
        this.attivo = attivo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getColore() {
        return colore;
    }

    public boolean isAttivo() {
        return attivo;
    }
}