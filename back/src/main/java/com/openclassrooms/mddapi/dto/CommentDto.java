package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CommentDto {

    @Data
    public static class Request {
        @NotBlank
        private String content;
    }

    @Value
    public static class Response {
        Long id;
        String content;
        LocalDateTime date;
        Long userId;
        Long articleId;
    }
}
