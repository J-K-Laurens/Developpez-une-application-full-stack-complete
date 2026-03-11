package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the Article entity.
 * Uses only simple Spring Data JPA methods.
 * Complex filtering logic is handled at the Service level.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * Retrieves all articles.
     */
    @Override
    List<Article> findAll();
    
    /**
     * Retrieves an article by its ID.
     */
    @Override
    Optional<Article> findById(Long id);
    
    /**
     * Retrieves articles created by a specific user.
     * 
     * @param userId the user ID
     * @return list of articles created by the user
     */
    List<Article> findByUserId(Long userId);
}

