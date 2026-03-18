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

/**
 * REST Controller for subscription-related endpoints.
 * Handles topic subscriptions for authenticated users.
 */
@RestController
@RequestMapping("/api/subscriptions")
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Retrieves all topics the authenticated user is subscribed to.
     * 
     * @return list of subscribed topics
     */
    @GetMapping
    public ResponseEntity<List<Topic>> getMySubscriptions() {
        String email = currentUserEmail();
        return ResponseEntity.ok(subscriptionService.getSubscribedTopics(email));
    }

    /**
     * Subscribes the authenticated user to a topic.
     * 
     * @param topicId the topic ID
     * @return empty response with status 201 Created
     * @throws ResourceNotFoundException if topic not found
     */
    @PostMapping("/{topicId}")
    public ResponseEntity<Void> subscribe(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long topicId) {
        String email = currentUserEmail();
        subscriptionService.subscribe(email, topicId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Unsubscribes the authenticated user from a topic.
     * 
     * @param topicId the topic ID
     * @return empty response with status 204 No Content
     * @throws ResourceNotFoundException if subscription not found
     */
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long topicId) {
        String email = currentUserEmail();
        subscriptionService.unsubscribe(email, topicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the email of the currently authenticated user.
     * 
     * @return the user's email
     */
    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
