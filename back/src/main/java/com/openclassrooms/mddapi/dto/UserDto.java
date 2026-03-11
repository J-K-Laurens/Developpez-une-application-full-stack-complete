package com.openclassrooms.mddapi.dto;

import com.openclassrooms.mddapi.model.User;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entities.
 * Contains request and response classes for user-related API operations.
 */
public class UserDto {

    /**
     * Request DTO for user profile updates.
     */
    @Data
    public static class Request {
        @Email
        private String email;
        @Size(max = 40)
        private String firstName;
        @Size(max = 40)
        private String lastName;
        private String password;
    }

    /**
     * Response DTO for user information.
     */
    @Value
    public static class Response {
        Long id;
        String email;
        String firstName;
        String lastName;
        Boolean admin;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        public static Response fromEntity(User user) {
            return new Response(
                    user.getId(), user.getEmail(), user.getFirstName(),
                    user.getLastName(), user.getAdmin(),
                    user.getCreatedAt(), user.getUpdatedAt()
            );
        }
    }
}
