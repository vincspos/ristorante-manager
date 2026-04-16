package com.ristorante.manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categorie_articolo")
public class CategoriaArticolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 20)
    private String colore;

    @Column(nullable = false)
    private Boolean attivo = true;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    
    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }
}
