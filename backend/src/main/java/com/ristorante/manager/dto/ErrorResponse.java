package com.ristorante.manager.dto;

public class ErrorResponse {
	
	private String errore;

    public ErrorResponse(String errore) {
        this.errore = errore;
    }

    public String getErrore() {
        return errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }

}
