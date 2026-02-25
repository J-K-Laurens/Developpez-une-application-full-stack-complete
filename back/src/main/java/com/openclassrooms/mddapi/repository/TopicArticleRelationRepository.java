package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.TopicArticleRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicArticleRelationRepository extends JpaRepository<TopicArticleRelation, Long> {
}
