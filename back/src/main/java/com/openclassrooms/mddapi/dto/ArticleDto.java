package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Article entities.
 * Contains request and response classes for article-related API operations.
 */
public class ArticleDto {

    /**
     * Request DTO for creating or updating articles.
     */
    @Data
    public static class Request {
        private String titre;
        private String content;
        private Long userId;
    }

    /**
     * Response DTO for listing articles.
     */
    @Value
    public static class ListItem {
        Long id;
        String titre;
        String content;
        LocalDateTime date;
        String author;
        Long userId;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }

    /**
     * Response DTO for detailed article information.
     */
    @Value
    public static class Detail {
        Long id;
        String titre;
        String content;
        LocalDateTime date;
        String authorName;
        String topicName;
        List<CommentItem> comments;

        /**
         * Comment item in article detail.
         */
        @Value
        public static class CommentItem {
            Long id;
            String authorName;
            String content;
            LocalDateTime date;
        }
    }
}
