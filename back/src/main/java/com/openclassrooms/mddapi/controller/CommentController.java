package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * REST Controller for comment-related endpoints.
 * Handles comment retrieval and creation on articles.
 */
@RestController
@RequestMapping("/api/articles")
@Validated
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    public CommentController(CommentService commentService, UserRepository userRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all comments for an article.
     * 
     * @param articleId the article ID
     * @return list of comments for the article
     * @throws ResourceNotFoundException if article not found
     */
    @GetMapping("/{articleId}/comments")
    public ResponseEntity<List<Comment>> getByArticleId(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long articleId) {
        return ResponseEntity.ok(commentService.findByArticleId(articleId));
    }

    /**
     * Creates a new comment on an article.
     * The current authenticated user is set as the comment author.
     * 
     * @param articleId the article ID
     * @param request the comment data
     * @return the created comment
     * @throws ResourceNotFoundException if article or user not found
     */
    @PostMapping("/{articleId}/comments")
    public ResponseEntity<Comment> create(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long articleId,
                                          @Valid @RequestBody CommentDto.Request request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", auth.getName()))
                .getId();

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(comment));
    }
}
