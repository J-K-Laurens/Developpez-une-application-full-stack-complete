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

/**
 * REST Controller for user-related endpoints.
 * Handles user profile retrieval and updates.
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id the user ID (must be &gt;= 1)
     * @return the user details
     * @throws ResourceNotFoundException if user not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        UserDto.Response user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Updates the information of the currently authenticated user.
     * 
     * @param request the new user data
     * @return the updated user data
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto.Response> updateCurrentUser(@Valid @RequestBody UserDto.Request request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.updateUser(auth.getName(), request));
    }
}
