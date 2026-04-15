package com.ristorante.ui.model;

public class RuoloDTO {
	
	private Long id;
	private String codice;
	private String descrizione;
	private boolean attivo;
	
	public RuoloDTO(Long id, String codice, String descrizione, boolean attivo) {
        this.id = id;
        this.codice = codice;
        this.descrizione = descrizione;
        this.attivo = attivo;
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

    
    public boolean isAttivo() {
        return attivo;
    }
    
    @Override
    public String toString() {
        return codice;
    }

}
