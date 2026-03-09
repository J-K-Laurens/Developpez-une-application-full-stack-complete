package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Article.
 * Utilise uniquement les méthodes simples de Spring Data JPA.
 * La logique de filtrage complexe est gérée au niveau du Service.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * Récupère tous les articles.
     */
    @Override
    List<Article> findAll();
    
    /**
     * Récupère un article par son ID.
     */
    @Override
    Optional<Article> findById(Long id);
    
    /**
     * Récupère les articles créés par un utilisateur.
     */
    List<Article> findByUserId(Long userId);
}

