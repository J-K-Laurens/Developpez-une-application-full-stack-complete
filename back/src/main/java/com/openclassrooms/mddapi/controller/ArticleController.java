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
     * Récupère tous les articles auxquels l'utilisateur connecté est abonné.
     * @return List des articles filtrés
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    @GetMapping
    public ResponseEntity<List<ArticleDto.ListItem>> listAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", auth.getName()))
                .getId();
        return ResponseEntity.ok(articleService.findListByUserSubscriptions(userId));
    }

    /**
     * Récupère un article par son ID.
     * @param id L'ID de l'article
     * @return L'article demandé
     * @throws ResourceNotFoundException si l'article n'existe pas
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long id) {
        Article article = articleService.findById(id);
        if (article == null) {
            throw new ResourceNotFoundException("Article", "id", id);
        }
        return ResponseEntity.ok(article);
    }

    /**
     * Récupère les détails complets d'un article.
     * @param id L'ID de l'article
     * @return Les détails complets de l'article
     * @throws ResourceNotFoundException si l'article n'existe pas
     */
    @GetMapping("/{id}/full")
    public ResponseEntity<ArticleDto.Detail> getFull(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long id) {
        return ResponseEntity.ok(articleService.findFull(id));
    }

    /**
     * Crée un nouvel article.
     * @param article L'article à créer
     * @return L'article créé
     */
    @PostMapping
    public ResponseEntity<Article> create(@Valid @RequestBody Article article) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.create(article));
    }

    /**
     * Récupère tous les topics associés à un article.
     * @param articleId L'ID de l'article
     * @return Liste des topics
     * @throws ResourceNotFoundException si l'article n'existe pas
     */
    @GetMapping("/{articleId}/topics")
    public ResponseEntity<List<Topic>> getTopicsByArticleId(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long articleId) {
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
