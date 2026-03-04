package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.ArticleDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
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

    @GetMapping
    public ResponseEntity<List<ArticleDto.ListItem>> listAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"))
                .getId();
        return ResponseEntity.ok(articleService.findListByUserSubscriptions(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<ArticleDto.Detail> getFull(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findFull(id));
    }

    @PostMapping
    public ResponseEntity<Article> create(@RequestBody Article article) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.create(article));
    }

    @GetMapping("/{articleId}/topics")
    public ResponseEntity<List<Topic>> getTopicsByArticleId(@PathVariable Long articleId) {
        return ResponseEntity.ok(topicService.findByArticleId(articleId));
    }

    @PostMapping("/{articleId}/topics/{topicId}")
    public ResponseEntity<TopicArticleRelation> addTopicToArticle(@PathVariable Long articleId,
                                                                   @PathVariable Long topicId) {
        TopicArticleRelation relation = new TopicArticleRelation();
        relation.setArticleId(articleId);
        relation.setTopicId(topicId);
        return ResponseEntity.status(HttpStatus.CREATED).body(relationRepository.save(relation));
    }
}
