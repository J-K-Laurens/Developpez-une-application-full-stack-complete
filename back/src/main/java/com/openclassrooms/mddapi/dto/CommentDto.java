package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Comment entities.
 * Contains request and response classes for comment-related API operations.
 */
public class CommentDto {

    /**
     * Request DTO for creating comments.
     */
    @Data
    public static class Request {
        @NotBlank
        private String content;
    }

    /**
     * Response DTO for comment information.
     */
    @Value
    public static class Response {
        Long id;
        String content;
        LocalDateTime date;
        Long userId;
        Long articleId;
    }
}
