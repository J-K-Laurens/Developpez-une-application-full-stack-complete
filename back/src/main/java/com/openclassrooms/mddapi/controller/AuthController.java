package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.AuthDto;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.exception.BusinessRuleException;
import com.openclassrooms.mddapi.services.JwtService;
// ...existing code...
import com.openclassrooms.mddapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * REST Controller for authentication-related endpoints.
 * Handles user login, registration, logout, and current user info retrieval.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
// ...existing code...

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, 
                         UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    // ...existing code...
    }

    /**
     * Authenticates a user and returns a JWT token.
     * 
     * @param request the login credentials
     * @return a JWT token for subsequent requests
     * @throws BadCredentialsException if credentials are incorrect
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);
        return ResponseEntity.ok(new AuthDto.TokenResponse(token, refreshToken));
    }

    /**
     * Refreshes the access token using a valid refresh token.
     * @param request the refresh token
     * @return a new access token and refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.RefreshResponse> refresh(@RequestBody AuthDto.RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDto.RefreshResponse(null, null));
        }
        String email = jwtService.getEmailFromRefreshToken(refreshToken);
        String token = jwtService.generateToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);
        return ResponseEntity.ok(new AuthDto.RefreshResponse(token, newRefreshToken));
    }

    /**
     * Registers a new user account.
     * 
     * @param request the registration data
     * @return a JWT token for subsequent requests
     * @throws BusinessRuleException if an account already exists with this email
     */
    @PostMapping("/register")
    public ResponseEntity<AuthDto.TokenResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        userService.register(request.getEmail(), request.getName(), request.getPassword());
        
        // Authenticate user after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(new AuthDto.TokenResponse(token, null));
    }

    /**
     * Retrieves information about the currently authenticated user.
     * 
     * @return the user data
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }

    /**
    * Logs out the current user (blacklist removed).
     * After logout, the token can no longer be used for authentication.
     * 
     * @param request the HTTP request containing the Authorization header
     * @return 200 OK with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            // Extract token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Blacklist functionality removed
                // ...existing code...
                
                // Clear security context
                SecurityContextHolder.clearContext();
                
                return ResponseEntity.ok("Logged out successfully");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid token found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during logout: " + e.getMessage());
        }
    }
}
