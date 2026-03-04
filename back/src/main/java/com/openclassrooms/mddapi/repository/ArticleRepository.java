package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = "SELECT DISTINCT a.* FROM ARTICLES a " +
                   "INNER JOIN `TOPIC_&_ARTICLE_RELATIONS` r ON a.id = r.article_id " +
                   "INNER JOIN `SUBSCRIPTIONS` s ON r.topic_id = s.topic_id " +
                   "WHERE s.user_id = :userId " +
                   "ORDER BY a.date DESC", nativeQuery = true)
    List<Article> findByUserSubscriptions(@Param("userId") Long userId);
}

