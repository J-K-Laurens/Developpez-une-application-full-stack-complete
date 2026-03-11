package com.openclassrooms.mddapi.exception;

/**
 * Exception thrown when a business rule is violated.
 * Typically corresponds to HTTP status 409 (Conflict).
 */
public class BusinessRuleException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    private String errorCode;

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessRuleException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
