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
     * Récupère tous les commentaires d'un article.
     * @param articleId L'ID de l'article
     * @return Liste des commentaires de l'article
     * @throws ResourceNotFoundException si l'article n'existe pas
     */
    @GetMapping("/{articleId}/comments")
    public ResponseEntity<List<Comment>> getByArticleId(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long articleId) {
        return ResponseEntity.ok(commentService.findByArticleId(articleId));
    }

    /**
     * Crée un nouveau commentaire sur un article.
     * @param articleId L'ID de l'article
     * @param request Les données du commentaire
     * @return Le commentaire créé
     * @throws ResourceNotFoundException si l'article ou l'utilisateur n'existe pas
     */
    @PostMapping("/{articleId}/comments")
    public ResponseEntity<Comment> create(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long articleId,
                                          @Valid @RequestBody CommentDto.Request request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", auth.getName()))
                .getId();

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(comment));
    }
}
