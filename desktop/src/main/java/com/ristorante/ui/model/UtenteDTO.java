package com.ristorante.ui.model;

public class UtenteDTO {

    private Long id;
    private String username;
    private String nome;
    private String cognome;
    private String ruolo;
    private Long ruoloId;
    private boolean attivo;

    public UtenteDTO(Long id, String username, String nome, String cognome, String ruolo, Long ruoloId, boolean attivo) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
        this.ruoloId = ruoloId;
        this.attivo = attivo;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getRuolo() { return ruolo; }
    public Long getRuoloId() {  return ruoloId; }
    public boolean isAttivo() { return attivo; }
}
