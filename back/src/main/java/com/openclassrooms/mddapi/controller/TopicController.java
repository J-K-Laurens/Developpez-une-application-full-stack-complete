package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.services.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * REST Controller for topic-related endpoints.
 * Handles topic retrieval and creation.
 */
@RestController
@RequestMapping("/api/topics")
@Validated
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Retrieves all available topics.
     * 
     * @return list of all topics
     */
    @GetMapping
    public ResponseEntity<List<Topic>> listAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    /**
     * Retrieves a topic by its ID.
     * 
     * @param id the topic ID
     * @return the requested topic
     * @throws ResourceNotFoundException if topic not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getById(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        Topic topic = topicService.findById(id);
        if (topic == null) {
            throw new ResourceNotFoundException("Topic", "id", id);
        }
        return ResponseEntity.ok(topic);
    }

    /**
     * Creates a new topic.
     * 
     * @param topic the topic data to create
     * @return the created topic
     */
    @PostMapping
    public ResponseEntity<Topic> create(@Valid @RequestBody Topic topic) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.create(topic));
    }
}
