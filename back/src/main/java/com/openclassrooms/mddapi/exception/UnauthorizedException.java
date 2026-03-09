package com.openclassrooms.mddapi.exception;

/**
 * Exception levée lors d'une tentative d'accès non autorisée.
 * Correspond à un statut HTTP 403 (Forbidden).
 */
public class UnauthorizedException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    private String resource;
    private String action;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, String resource, String action) {
        super(message);
        this.resource = resource;
        this.action = action;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }
}
