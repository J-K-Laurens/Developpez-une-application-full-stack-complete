package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Data Transfer Object for Authentication operations.
 * Contains request and response classes for login and registration.
 */
public class AuthDto {

    /**
     * Request DTO for user login.
     */
    @Data
    public static class LoginRequest {
        private String email;
        @NotBlank
        private String password;
    }

    /**
     * Request DTO for user registration.
     */
    @Data
    public static class RegisterRequest {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String name;
        @NotBlank
        private String password;
    }

    /**
     * Response DTO containing JWT token.
     */
    @Value
    public static class TokenResponse {
        String token;
        String refreshToken;
    }

    @Data
    public static class RefreshRequest {
        private String refreshToken;
    }

    @Value
    public static class RefreshResponse {
        String token;
        String refreshToken;
    }

    @Value
    public static class ErrorResponse {
        String message;
    }
}
