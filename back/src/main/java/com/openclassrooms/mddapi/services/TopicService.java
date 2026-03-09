package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));
    }

    public List<Topic> findByArticleId(Long articleId) {
        return topicRepository.findByArticleId(articleId);
    }

    public Topic create(Topic topic) {
        return topicRepository.save(topic);
    }
}
