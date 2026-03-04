package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.services.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getMySubscriptions() {
        String email = currentUserEmail();
        return ResponseEntity.ok(subscriptionService.getSubscribedTopics(email));
    }

    @PostMapping("/{topicId}")
    public ResponseEntity<Void> subscribe(@PathVariable Long topicId) {
        String email = currentUserEmail();
        subscriptionService.subscribe(email, topicId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long topicId) {
        String email = currentUserEmail();
        subscriptionService.unsubscribe(email, topicId);
        return ResponseEntity.noContent().build();
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
