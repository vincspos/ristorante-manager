package com.ristorante.manager.dto;

import java.math.BigDecimal;

public class ArticoloRequest {

    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private Long categoriaId;
    private Integer iva;
    private Boolean attivo;
    private Integer quantitaDisponibile;
    private Integer sogliaWarning;
    private Boolean gestioneMagazzino;
    private Long utenteId;
    private String utenteUsername;

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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getIva() {
        return iva;
    }

    public void setIva(Integer iva) {
        this.iva = iva;
    }

    public Boolean getAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }
    public Integer getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(Integer quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public Integer getSogliaWarning() {
        return sogliaWarning;
    }

    public void setSogliaWarning(Integer sogliaWarning) {
        this.sogliaWarning = sogliaWarning;
    }
    
    public Boolean getGestioneMagazzino() {
        return gestioneMagazzino;
    }

    public void setGestioneMagazzino(Boolean gestioneMagazzino) {
        this.gestioneMagazzino = gestioneMagazzino;
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