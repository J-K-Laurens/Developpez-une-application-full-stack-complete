package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query(value = "SELECT t.* FROM TOPICS t " +
                   "INNER JOIN `TOPIC_&_ARTICLE_RELATIONS` r ON t.id = r.topic_id " +
                   "WHERE r.article_id = :articleId", nativeQuery = true)
    List<Topic> findByArticleId(@Param("articleId") Long articleId);
}
