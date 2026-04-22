package com.ristorante.manager.exception;

public class BadRequestException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4516409975466787718L;

	public BadRequestException(String message) {
        super(message);
    }

}
