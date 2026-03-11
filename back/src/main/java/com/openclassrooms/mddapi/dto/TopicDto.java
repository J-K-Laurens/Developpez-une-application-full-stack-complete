package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Topic entities.
 * Contains request and response classes for topic-related API operations.
 */
public class TopicDto {

    /**
     * Request DTO for creating or updating topics.
     */
    @Data
    public static class Request {
        private String name;
        private String description;
    }

    /**
     * Response DTO for topic information.
     */
    @Value
    public static class Response {
        Long id;
        String name;
        String description;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }
}
