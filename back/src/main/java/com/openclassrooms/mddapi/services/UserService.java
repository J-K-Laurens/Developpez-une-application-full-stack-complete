package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.validation.PasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto.Response getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto.Response::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Utilisateur avec l'id " + id + " introuvable"));
    }

    public UserDto.Response getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDto.Response::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Utilisateur avec l'email " + email + " introuvable"));
    }

    public UserDto.Response updateUser(String currentEmail, UserDto.Request request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(currentEmail) && userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte existe déjà avec cet email");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!PasswordValidator.isValid(request.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        PasswordValidator.getErrorMessage(request.getPassword()));
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserDto.Response.fromEntity(userRepository.save(user));
    }

    public void register(String email, String name, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte existe déjà avec cet email");
        }
        if (!PasswordValidator.isValid(rawPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    PasswordValidator.getErrorMessage(rawPassword));
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

