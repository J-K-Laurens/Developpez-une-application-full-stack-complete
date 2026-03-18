package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.ArticleDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.TopicArticleRelation;
import com.openclassrooms.mddapi.repository.TopicArticleRelationRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.services.ArticleService;
import com.openclassrooms.mddapi.services.TopicService;
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
 * REST Controller for article-related endpoints.
 * Handles article retrieval, creation, and topic associations.
 */
@RestController
@RequestMapping("/api/articles")
@Validated
public class ArticleController {

    private final ArticleService articleService;
    private final TopicService topicService;
    private final TopicArticleRelationRepository relationRepository;
    private final UserRepository userRepository;

    public ArticleController(ArticleService articleService, TopicService topicService,
                             TopicArticleRelationRepository relationRepository,
                             UserRepository userRepository) {
        this.articleService = articleService;
        this.topicService = topicService;
        this.relationRepository = relationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all articles to which the authenticated user is subscribed.
     * 
     * @return list of filtered articles
     * @throws ResourceNotFoundException if user not found
     */
    @GetMapping
    public ResponseEntity<List<ArticleDto.ListItem>> listAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", auth.getName()))
                .getId();
        return ResponseEntity.ok(articleService.findListByUserSubscriptions(userId));
    }

    /**
     * Retrieves an article by its ID.
     * 
     * @param id the article ID
     * @return the requested article
     * @throws ResourceNotFoundException if article not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        Article article = articleService.findById(id);
        if (article == null) {
            throw new ResourceNotFoundException("Article", "id", id);
        }
        return ResponseEntity.ok(article);
    }

    /**
     * Retrieves the complete details of an article.
     * 
     * @param id the article ID
     * @return the complete article details
     * @throws ResourceNotFoundException if article not found
     */
    @GetMapping("/{id}/full")
    public ResponseEntity<ArticleDto.Detail> getFull(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        return ResponseEntity.ok(articleService.findFull(id));
    }

    /**
     * Creates a new article.
     * 
     * @param article the article to create
     * @return the created article
     */
    @PostMapping
    public ResponseEntity<Article> create(@Valid @RequestBody Article article) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.create(article));
    }

    /**
     * Retrieves all topics associated with an article.
     * 
     * @param articleId the article ID
     * @return list of topics
     * @throws ResourceNotFoundException if article not found
     */
    @GetMapping("/{articleId}/topics")
    public ResponseEntity<List<Topic>> getTopicsByArticleId(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long articleId) {
        return ResponseEntity.ok(topicService.findByArticleId(articleId));
    }

    /**
     * Associe un topic à un article.
     * @param articleId L'ID de l'article
     * @param topicId L'ID du topic
     * @return La relation créée
     * @throws ResourceNotFoundException si l'article ou le topic n'existe pas
     */
    @PostMapping("/{articleId}/topics/{topicId}")
    public ResponseEntity<TopicArticleRelation> addTopicToArticle(
            @PathVariable @Min(value = 1, message = "L'ID article doit être supérieur à 0") Long articleId,
            @PathVariable @Min(value = 1, message = "L'ID topic doit être supérieur à 0") Long topicId) {
        TopicArticleRelation relation = new TopicArticleRelation();
        relation.setArticleId(articleId);
        relation.setTopicId(topicId);
        return ResponseEntity.status(HttpStatus.CREATED).body(relationRepository.save(relation));
    }
}
