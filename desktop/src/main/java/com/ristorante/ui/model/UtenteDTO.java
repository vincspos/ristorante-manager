package com.ristorante.ui.model;

public class UtenteDTO {

    private Long id;
    private String username;
    private String nome;
    private String cognome;
    private String ruolo;

    public UtenteDTO(Long id, String username, String nome, String cognome, String ruolo) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getRuolo() { return ruolo; }
}
