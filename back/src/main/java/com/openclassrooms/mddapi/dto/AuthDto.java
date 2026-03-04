package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class AuthDto {

    @Data
    public static class LoginRequest {
        private String email;
        @NotBlank
        private String password;
    }

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

    @Value
    public static class TokenResponse {
        String token;
    }

    @Value
    public static class ErrorResponse {
        String message;
    }
}
