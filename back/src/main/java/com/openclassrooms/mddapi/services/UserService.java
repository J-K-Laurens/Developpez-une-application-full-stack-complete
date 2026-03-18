package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.exception.BusinessRuleException;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.exception.ValidationException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing user-related business logic.
 * Handles user retrieval, registration, and profile updates.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id the user ID
     * @return the user response DTO
     * @throws ResourceNotFoundException if user not found
     */
    public UserDto.Response getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto.Response::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Retrieves a user by their email address.
     * 
     * @param email the user email
     * @return the user response DTO
     * @throws ResourceNotFoundException if user not found
     */
    public UserDto.Response getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDto.Response::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Updates a user's profile information.
     * Validates email uniqueness and password strength.
     * 
     * @param currentEmail the user's current email
     * @param request the update request with new user data
     * @return the updated user response DTO
     * @throws ResourceNotFoundException if user not found
     * @throws BusinessRuleException if email already exists
     * @throws ValidationException if password is invalid
     */
    public UserDto.Response updateUser(String currentEmail, UserDto.Request request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentEmail));

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(currentEmail) && userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessRuleException("Un compte existe déjà avec cet email", "EMAIL_ALREADY_EXISTS");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!PasswordValidator.isValid(request.getPassword())) {
                throw new ValidationException(PasswordValidator.getErrorMessage(request.getPassword()));
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserDto.Response.fromEntity(userRepository.save(user));
    }

    /**
     * Registers a new user account.
     * Validates email uniqueness and password strength.
     * 
     * @param email the user's email address
     * @param name the user's first name
     * @param rawPassword the user's raw password (will be encoded)
     * @throws BusinessRuleException if email already exists
     * @throws ValidationException if password is invalid
     */
    public void register(String email, String name, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("Un compte existe déjà avec cet email", "EMAIL_ALREADY_EXISTS");
        }
        if (!PasswordValidator.isValid(rawPassword)) {
            throw new ValidationException(PasswordValidator.getErrorMessage(rawPassword));
        }
        
        User user = new User();
        user.setEmail(email);
        user.setFirstName(name);
        user.setLastName(null);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setAdmin(false);
        userRepository.save(user);
    }
}

