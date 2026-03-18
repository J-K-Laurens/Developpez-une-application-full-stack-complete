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
 * Global exception handler for the API.
 * Centralizes error handling and standardizes error responses.
 */
@ControllerAdvice // Handles exceptions at the application level
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException (404 Not Found).
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ValidationException (400 Bad Request).
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, 
            HttpServletRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
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
     * Handles BusinessRuleException (409 Conflict).
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 409 status
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
            BusinessRuleException ex, 
            HttpServletRequest request) {
        logger.warn("Business rule violation: {} [{}]", ex.getMessage(), ex.getErrorCode());
        
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
     * Handles UnauthorizedException (403 Forbidden).
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 403 status
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, 
            HttpServletRequest request) {
        logger.warn("Unauthorized access: {} [Resource: {}, Action: {}]", 
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
     * Handles method argument validation errors (400 Bad Request).
     * Used for @Valid and JSR303/JSR380 validation constraints.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        logger.warn("Argument validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Argument Validation Failed",
                "The provided parameters are invalid",
                request.getRequestURI(),
                errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles authentication errors.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 401 status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, 
            HttpServletRequest request) {
        logger.warn("Authentication error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                "Invalid credentials or expired session",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles unhandled exceptions (500 Internal Server Error).
     * Used as a last resort.
     * 
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        logger.error("Unhandled exception", ex);
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An internal error occurred. Please contact support.",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
