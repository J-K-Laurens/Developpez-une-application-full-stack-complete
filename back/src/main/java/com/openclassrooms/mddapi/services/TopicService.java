package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.TopicArticleRelation;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.TopicArticleRelationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing topic-related business logic.
 * Handles topic retrieval and creation.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicArticleRelationRepository relationRepository;

    public TopicService(TopicRepository topicRepository, TopicArticleRelationRepository relationRepository) {
        this.topicRepository = topicRepository;
        this.relationRepository = relationRepository;
    }

    /**
     * Retrieves all topics.
     * 
     * @return list of all topics
     */
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    /**
     * Retrieves a topic by its ID.
     * 
     * @param id the topic ID
     * @return the topic
     * @throws ResourceNotFoundException if topic not found
     */
    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));
    }

    /**
     * Retrieves all topics associated with an article.
     * Uses TopicArticleRelation to find linked topics via standard JPA methods.
     * Eliminates duplicate topic IDs using a Set for efficiency.
     * 
     * @param articleId the article ID
     * @return list of topics for the article
     */
    public List<Topic> findByArticleId(Long articleId) {
        // Retrieve all topic-article relationships for this article
        List<TopicArticleRelation> relations = relationRepository.findByArticleId(articleId);
        
        // Extract topic IDs using Set to eliminate duplicates and improve lookup performance
        Set<Long> topicIds = relations.stream()
                .map(TopicArticleRelation::getTopicId)
                .collect(Collectors.toSet());
        
        return topicRepository.findAllById(topicIds);
    }

    /**
     * Creates a new topic.
     * 
     * @param topic the topic to create
     * @return the created topic
     */
    public Topic create(Topic topic) {
        return topicRepository.save(topic);
    }
}
