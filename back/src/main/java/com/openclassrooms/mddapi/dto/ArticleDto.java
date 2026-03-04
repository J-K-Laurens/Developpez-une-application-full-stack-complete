package com.openclassrooms.mddapi.dto;

import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleDto {

    @Data
    public static class Request {
        private String titre;
        private String content;
        private Long userId;
    }

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

    @Value
    public static class Detail {
        Long id;
        String titre;
        String content;
        LocalDateTime date;
        String authorName;
        String topicName;
        List<CommentItem> comments;

        @Value
        public static class CommentItem {
            Long id;
            String authorName;
            String content;
            LocalDateTime date;
        }
    }
}
