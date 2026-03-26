package com.ristorante.ui.model;

public class RuoloDTO {
	
	private Long id;
	private String codice;
	private String descrizione;
	
	public RuoloDTO(Long id, String codice, String descrizione) {
        this.id = id;
        this.codice = codice;
        this.descrizione = descrizione;
    }

    public Long getId() {
        return id;
    }

    public String getCodice() {
        return codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public String toString() {
        return codice;
    }

}
