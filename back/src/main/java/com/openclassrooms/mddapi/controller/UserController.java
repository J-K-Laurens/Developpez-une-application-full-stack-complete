package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère un utilisateur par son ID.
     * @param id L'ID de l'utilisateur (doit être >= 1)
     * @return Les détails de l'utilisateur
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long id) {
        UserDto.Response user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", id);
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Met à jour les informations de l'utilisateur connecté.
     * @param request Les nouvelles données de l'utilisateur
     * @return Les données mises à jour
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto.Response> updateCurrentUser(@Valid @RequestBody UserDto.Request request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.updateUser(auth.getName(), request));
    }
}
