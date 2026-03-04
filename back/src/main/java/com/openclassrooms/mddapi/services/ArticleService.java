package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.ArticleDto;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.TopicArticleRelationRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final TopicArticleRelationRepository relationRepository;
    private final CommentRepository commentRepository;

    public ArticleService(ArticleRepository articleRepository,
                          UserRepository userRepository,
                          TopicRepository topicRepository,
                          TopicArticleRelationRepository relationRepository,
                          CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.relationRepository = relationRepository;
        this.commentRepository = commentRepository;
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<ArticleDto.ListItem> findAllList() {
        return articleRepository.findAll().stream()
                .map(article -> {
                    String authorName = article.getUserId() != null
                            ? userRepository.findById(article.getUserId())
                                    .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                                    .orElse("Auteur inconnu")
                            : "Auteur inconnu";
                    return new ArticleDto.ListItem(
                            article.getId(),
                            article.getTitre(),
                            article.getContent(),
                            article.getDate(),
                            authorName,
                            article.getUserId(),
                            article.getCreatedAt(),
                            article.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<ArticleDto.ListItem> findListByUserSubscriptions(Long userId) {
        return articleRepository.findByUserSubscriptions(userId).stream()
                .map(article -> {
                    String authorName = article.getUserId() != null
                            ? userRepository.findById(article.getUserId())
                                    .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                                    .orElse("Auteur inconnu")
                            : "Auteur inconnu";
                    return new ArticleDto.ListItem(
                            article.getId(),
                            article.getTitre(),
                            article.getContent(),
                            article.getDate(),
                            authorName,
                            article.getUserId(),
                            article.getCreatedAt(),
                            article.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Article avec l'id " + id + " introuvable"));
    }

    public Article create(Article article) {
        return articleRepository.save(article);
    }

    public ArticleDto.Detail findFull(Long id) {
        Article article = findById(id);

        String authorName = article.getUserId() != null
                ? userRepository.findById(article.getUserId())
                        .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                        .orElse("Auteur inconnu")
                : "Auteur inconnu";

        String topicName = relationRepository.findByArticleId(id).stream()
                .findFirst()
                .flatMap(rel -> topicRepository.findById(rel.getTopicId()))
                .map(t -> t.getName())
                .orElse(null);

        List<ArticleDto.Detail.CommentItem> commentItems = commentRepository.findByArticleId(id).stream()
                .map(c -> {
                    String commentAuthor = c.getUserId() != null
                            ? userRepository.findById(c.getUserId())
                                    .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                                    .orElse("Anonyme")
                            : "Anonyme";
                    return new ArticleDto.Detail.CommentItem(c.getId(), commentAuthor, c.getContent(), c.getDate());
                })
                .collect(Collectors.toList());

        return new ArticleDto.Detail(
                article.getId(),
                article.getTitre(),
                article.getContent(),
                article.getDate(),
                authorName,
                topicName,
                commentItems
        );
    }
}
