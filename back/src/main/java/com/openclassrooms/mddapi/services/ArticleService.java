package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.dto.ArticleDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicArticleRelationRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing article-related business logic.
 * Handles article retrieval, creation, and filtering by user subscriptions.
 */
@Service
public class ArticleService {

        private final ArticleRepository articleRepository;
        private final UserRepository userRepository;
        private final TopicRepository topicRepository;
        private final TopicArticleRelationRepository relationRepository;
        private final CommentRepository commentRepository;
        private final SubscriptionRepository subscriptionRepository;

    public ArticleService(
        ArticleRepository articleRepository,
        UserRepository userRepository,
        TopicRepository topicRepository,
        TopicArticleRelationRepository relationRepository,
        CommentRepository commentRepository,
        SubscriptionRepository subscriptionRepository
    ) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.relationRepository = relationRepository;
        this.commentRepository = commentRepository;
        this.subscriptionRepository = subscriptionRepository;
        }

    // ==================== SIMPLE METHODS ====================

    /**
     * Retrieves all articles from the database.
     *
     * @return list of all articles
     */
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    /**
     * Retrieves all articles with enriched information (DTO).
     * Includes the author's full name for each article.
     *
     * @return list of articles in ArticleDto.ListItem format
     */
    public List<ArticleDto.ListItem> findAllList() {
        return articleRepository.findAll().stream()
                .map(article -> {
                    String authorName = article.getUserId() != null
                            ? userRepository.findById(article.getUserId())
                                    .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                                    .orElse("Unknown author")
                            : "Unknown author";

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

    /**
     * Retrieves an article by its ID.
     * Throws a 404 exception if the article does not exist.
     *
     * @param id the article ID
     * @return the corresponding article
     * @throws ResourceNotFoundException if article is not found
     */
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
    }

    /**
     * Creates a new article.
     *
     * @param article the article to create
     * @return the created article saved in database
     */
    public Article create(Article article) {
        return articleRepository.save(article);
    }

    // ==================== COMPLEX METHODS ====================

    /**
     * Retrieves articles to which a user is subscribed.
     * Uses a Set for fast O(1) lookups of subscribed topics.
     * 
     * @param userId the user ID
     * @return list of filtered articles, sorted by date descending
     */
    public List<ArticleDto.ListItem> findListByUserSubscriptions(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        if (subscriptions.isEmpty()) {
            return List.of();
        }
        
        // Uses Set for O(1) lookups instead of O(n)
        Set<Long> topicIds = subscriptions.stream()
                .map(Subscription::getTopicId)
                .collect(Collectors.toSet());
        
        return articleRepository.findAll().stream()
                // Filter: does article belong to a subscribed topic?
                .filter(article -> relationRepository.findByArticleId(article.getId())
                        .stream()
                        .anyMatch(relation -> topicIds.contains(relation.getTopicId()))
                )
                // Sort by date descending
                .sorted((a1, a2) -> {
                    if (a1.getDate() != null && a2.getDate() != null) {
                        return a2.getDate().compareTo(a1.getDate());
                    }
                    return 0;
                })
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

    /**
     * Retrieves the complete details of an article including author, topic, and enriched comments.
     *
     * @param id the article ID
     * @return the article in enriched ArticleDto.Detail format
     */
    public ArticleDto.Detail findFull(Long id) {
        Article article = findById(id);

        String authorName = article.getUserId() != null
                ? userRepository.findById(article.getUserId())
                        .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                        .orElse("Unknown author")
                : "Unknown author";

        // Retrieves the first topic linked to this article
        String topicName = relationRepository.findByArticleId(id).stream()
                .findFirst()
                .flatMap(rel -> topicRepository.findById(rel.getTopicId()))
                .map(t -> t.getName())
                .orElse(null);

        List<ArticleDto.Detail.CommentItem> commentItems = 
                commentRepository.findByArticleId(id).stream()
                .map(comment -> {
                    String commentAuthor = comment.getUserId() != null
                            ? userRepository.findById(comment.getUserId())
                                    .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                                    .orElse("Anonymous")
                            : "Anonymous";
                    
                    return new ArticleDto.Detail.CommentItem(
                            comment.getId(),
                            commentAuthor,
                            comment.getContent(),
                            comment.getDate()
                    );
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
