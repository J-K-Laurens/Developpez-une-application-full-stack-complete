package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.TopicArticleRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the TopicArticleRelation entity.
 */
@Repository
public interface TopicArticleRelationRepository extends JpaRepository<TopicArticleRelation, Long> {

    List<TopicArticleRelation> findByArticleId(Long articleId);
}
