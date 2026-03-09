package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'API.
 * Centralise la gestion des erreurs et standardise les réponses.
 */
@ControllerAdvice // Permet de gérer les exceptions à l'échelle de l'application
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les ResourceNotFoundException (404 Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {
        logger.warn("Ressource non trouvée: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les ValidationException (400 Bad Request)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, 
            HttpServletRequest request) {
        logger.warn("Erreur de validation: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getValidationErrors()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les BusinessRuleException (409 Conflict)
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
            BusinessRuleException ex, 
            HttpServletRequest request) {
        logger.warn("Violation de règle métier: {} [{}]", ex.getMessage(), ex.getErrorCode());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Business Rule Violation",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Gère les UnauthorizedException (403 Forbidden)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, 
            HttpServletRequest request) {
        logger.warn("Accès non autorisé: {} [Resource: {}, Action: {}]", 
                ex.getMessage(), ex.getResource(), ex.getAction());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Gère les erreurs de validation des arguments de méthode (400 Bad Request)
     * Utilisé pour @Valid et les contraintes de validation JSR303/JSR380
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        logger.warn("Erreur de validation d'argument: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Argument Validation Failed",
                "Les paramètres fournis sont invalides",
                request.getRequestURI(),
                errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les erreurs d'authentification
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, 
            HttpServletRequest request) {
        logger.warn("Erreur d'authentification: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                "Identifiants invalides ou session expirée",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gère les exceptions non gérées (500 Internal Server Error)
     * À utiliser en dernier recours
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        logger.error("Exception non gérée", ex);
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Une erreur interne s'est produite. Veuillez contacter le support.",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
