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

@RestController
@RequestMapping("/api/topics")
@Validated
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Récupère tous les topics disponibles.
     * @return Liste de tous les topics
     */
    @GetMapping
    public ResponseEntity<List<Topic>> listAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    /**
     * Récupère un topic par son ID.
     * @param id L'ID du topic
     * @return Le topic demandé
     * @throws ResourceNotFoundException si le topic n'existe pas
     */
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getById(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long id) {
        Topic topic = topicService.findById(id);
        if (topic == null) {
            throw new ResourceNotFoundException("Topic", "id", id);
        }
        return ResponseEntity.ok(topic);
    }

    /**
     * Crée un nouveau topic.
     * @param topic Les données du topic à créer
     * @return Le topic créé
     */
    @PostMapping
    public ResponseEntity<Topic> create(@Valid @RequestBody Topic topic) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.create(topic));
    }
}
