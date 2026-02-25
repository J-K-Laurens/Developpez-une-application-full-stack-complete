package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{articleId}/comments")
    public ResponseEntity<List<Comment>> getByArticleId(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentService.findByArticleId(articleId));
    }

    @PostMapping("/{articleId}/comments")
    public ResponseEntity<Comment> create(@PathVariable Long articleId, @RequestBody Comment comment) {
        comment.setArticleId(articleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(comment));
    }
}
