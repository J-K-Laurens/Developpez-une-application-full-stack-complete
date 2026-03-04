package com.openclassrooms.mddapi.dto;

import com.openclassrooms.mddapi.model.User;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UserDto {

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
