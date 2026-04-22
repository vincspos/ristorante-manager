package com.ristorante.manager.exception;

public class ResourceNotFoundException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3634280485589147290L;

	public ResourceNotFoundException(String message) {
        super(message);
    }

}
