package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.services.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Récupère tous les topics auxquels l'utilisateur connecté est abonné.
     * @return Liste des topics abonnés
     */
    @GetMapping
    public ResponseEntity<List<Topic>> getMySubscriptions() {
        String email = currentUserEmail();
        return ResponseEntity.ok(subscriptionService.getSubscribedTopics(email));
    }

    /**
     * Abonne l'utilisateur connecté à un topic.
     * @param topicId L'ID du topic
     * @return Vide avec statut 201 Created
     * @throws ResourceNotFoundException si le topic n'existe pas
     */
    @PostMapping("/{topicId}")
    public ResponseEntity<Void> subscribe(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long topicId) {
        String email = currentUserEmail();
        subscriptionService.subscribe(email, topicId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Désabonne l'utilisateur connecté d'un topic.
     * @param topicId L'ID du topic
     * @return Vide avec statut 204 No Content
     * @throws ResourceNotFoundException si le topic n'existe pas
     */
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long topicId) {
        String email = currentUserEmail();
        subscriptionService.unsubscribe(email, topicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère l'email de l'utilisateur actuellement authentifié.
     * @return L'email de l'utilisateur
     */
    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
