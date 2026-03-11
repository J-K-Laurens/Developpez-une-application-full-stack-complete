package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing comment-related business logic.
 * Handles comment retrieval and creation.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Retrieves all comments for a specific article.
     * 
     * @param articleId the article ID
     * @return list of comments for the article
     */
    public List<Comment> findByArticleId(Long articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    /**
     * Creates a new comment.
     * 
     * @param comment the comment to create
     * @return the created comment
     */
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }
}
