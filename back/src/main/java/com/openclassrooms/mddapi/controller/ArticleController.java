package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.TopicArticleRelation;
import com.openclassrooms.mddapi.repository.TopicArticleRelationRepository;
import com.openclassrooms.mddapi.services.ArticleService;
import com.openclassrooms.mddapi.services.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final TopicService topicService;
    private final TopicArticleRelationRepository relationRepository;

    public ArticleController(ArticleService articleService, TopicService topicService,
                             TopicArticleRelationRepository relationRepository) {
        this.articleService = articleService;
        this.topicService = topicService;
        this.relationRepository = relationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Article>> listAll() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
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
