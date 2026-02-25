package com.openclassrooms.mddapi.dto;

import com.openclassrooms.mddapi.model.User;

import java.time.LocalDateTime;

public class UserResponse {

    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Boolean admin;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UserResponse(Long id, String email, String firstName, String lastName,
                        Boolean admin, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.admin = admin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Boolean getAdmin() { return admin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAdmin(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
