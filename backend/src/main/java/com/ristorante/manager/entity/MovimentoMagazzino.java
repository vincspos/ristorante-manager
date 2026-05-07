package com.ristorante.manager.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimenti_magazzino")
public class MovimentoMagazzino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "articolo_id")
    private Articolo articolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentoMagazzino tipo;

    @Column(nullable = false)
    private Integer quantita;

    @Column(length = 255)
    private String note;

    @Column(nullable = false)
    private LocalDateTime dataMovimento;
    
    @Column(name = "utente_id")
    private Long utenteId;

    @Column(name = "utente_username", length = 80)
    private String utenteUsername;

    @PrePersist
    public void prePersist() {
        dataMovimento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Articolo getArticolo() {
        return articolo;
    }

    public void setArticolo(Articolo articolo) {
        this.articolo = articolo;
    }

    public TipoMovimentoMagazzino getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoMagazzino tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getDataMovimento() {
        return dataMovimento;
    }

	public Long getUtenteId() {
		return utenteId;
	}

	public void setUtenteId(Long utenteId) {
		this.utenteId = utenteId;
	}

	public String getUtenteUsername() {
		return utenteUsername;
	}

	public void setUtenteUsername(String utenteUsername) {
		this.utenteUsername = utenteUsername;
	}
}
