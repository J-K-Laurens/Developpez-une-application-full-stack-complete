package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing topic-related business logic.
 * Handles topic retrieval and creation.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
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
     * 
     * @param articleId the article ID
     * @return list of topics for the article
     */
    public List<Topic> findByArticleId(Long articleId) {
        return topicRepository.findByArticleId(articleId);
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
