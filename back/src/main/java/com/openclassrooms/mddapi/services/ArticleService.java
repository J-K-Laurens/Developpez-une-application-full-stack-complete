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

    // ==================== MÉTHODES SIMPLES ====================

    /**
     * Récupère TOUS les articles de la base de données.
     *
     * @return Liste de tous les articles
     */
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    /**
     * Récupère TOUS les articles avec les informations enrichies (DTO).
     * Inclut le nom complet de l'auteur de chaque article.
     *
     * @return Liste des articles au format ArticleDto.ListItem
     */
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

    /**
     * Récupère un article par son ID.
     * Lève une exception 404 si l'article n'existe pas.
     *
     * @param id L'ID de l'article
     * @return L'article correspondant
     * @throws ResourceNotFoundException 404 si l'article n'est pas trouvé
     */
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
    }

    /**
     * Crée un nouvel article.
     *
     * @param article L'article à créer
     * @return L'article créé et sauvegardé en base
     */
    public Article create(Article article) {
        return articleRepository.save(article);
    }

    // ==================== MÉTHODES COMPLEXES ====================

    /**
     * Récupère les articles auxquels un utilisateur est abonné.
     * Utilise un Set pour les recherches rapides O(1) des topics abonnés.
     * 
     * @param userId L'ID de l'utilisateur
     * @return Liste des articles filtrés, triés par date décroissante
     */
    public List<ArticleDto.ListItem> findListByUserSubscriptions(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        if (subscriptions.isEmpty()) {
            return List.of();
        }
        
        // Utilise Set pour recherches O(1) au lieu de O(n)
        Set<Long> topicIds = subscriptions.stream()
                .map(Subscription::getTopicId)
                .collect(Collectors.toSet());
        
        return articleRepository.findAll().stream()
                // Filtre: article appartient à un topic abonné?
                .filter(article -> relationRepository.findByArticleId(article.getId())
                        .stream()
                        .anyMatch(relation -> topicIds.contains(relation.getTopicId()))
                )
                // Trie par date décroissante
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
     * Récupère les détails complets d'un article avec auteur, topic et commentaires enrichis.
     *
     * @param id L'ID de l'article
     * @return L'article au format ArticleDto.Detail enrichi
     * @throws ResponseStatusException 404 si l'article n'existe pas
     */
    public ArticleDto.Detail findFull(Long id) {
        Article article = findById(id);

        String authorName = article.getUserId() != null
                ? userRepository.findById(article.getUserId())
                        .map(u -> u.getFirstName() != null ? u.getFirstName() : u.getEmail())
                        .orElse("Auteur inconnu")
                : "Auteur inconnu";

        // Récupère le premier topic lié à cet article
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
                                    .orElse("Anonyme")
                            : "Anonyme";
                    
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
