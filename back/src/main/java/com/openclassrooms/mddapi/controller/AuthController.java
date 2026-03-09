package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.AuthDto;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.exception.BusinessRuleException;
import com.openclassrooms.mddapi.services.JwtService;
import com.openclassrooms.mddapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     * @param request Les identifiants de connexion
     * @return Un token JWT pour les requêtes ultérieures
     * @throws BadCredentialsException si les identifiants sont incorrects
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(new AuthDto.TokenResponse(token));
    }

    /**
     * Enregistre un nouvel utilisateur.
     * @param request Les données d'inscription
     * @return Un token JWT pour les requêtes ultérieures
     * @throws BusinessRuleException si un compte existe déjà avec cet email
     */
    @PostMapping("/register")
    public ResponseEntity<AuthDto.TokenResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        userService.register(request.getEmail(), request.getName(), request.getPassword());
        
        // Authentifier l'utilisateur après l'enregistrement
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(new AuthDto.TokenResponse(token));
    }

    /**
     * Récupère les informations de l'utilisateur actuellement connecté.
     * @return Les données de l'utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }
}
