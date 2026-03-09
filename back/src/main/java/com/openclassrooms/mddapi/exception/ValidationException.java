package com.openclassrooms.mddapi.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception levée lors d'erreurs de validation métier.
 * Correspond généralement à un statut HTTP 400.
 */
public class ValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    private Map<String, String> validationErrors = new HashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.validationErrors = errors;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void addError(String field, String message) {
        this.validationErrors.put(field, message);
    }
}
