package com.ristorante.manager.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "utenti")
public class Utente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
    private String password;
	
	@Column(nullable = false, unique = true)
    private String username;

	@ManyToOne
	@JoinColumn(name = "ruolo_id", nullable = false)
	private Ruolo ruolo;

	@Column(nullable = false)
    private String nome;

	@Column(nullable = false)
    private String cognome;

	@Column(nullable = false)
    private Boolean attivo = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Boolean getAttivo() {
		return attivo;
	}

	public void setAttivo(Boolean attivo) {
		this.attivo = attivo;
	}

}
