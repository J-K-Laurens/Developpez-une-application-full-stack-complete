package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

public class TopicDto {

    @Data
    public static class Request {
        private String name;
        private String description;
    }

    @Value
    public static class Response {
        Long id;
        String name;
        String description;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }
}
