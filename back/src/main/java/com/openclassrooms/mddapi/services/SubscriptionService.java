package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.exception.BusinessRuleException;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user topic subscriptions.
 * Handles subscription creation, deletion, and subscription retrieval.
 */
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               TopicRepository topicRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all topics a user is subscribed to.
     * 
     * @param email the user's email address
     * @return list of subscribed topics
     */
    public List<Topic> getSubscribedTopics(String email) {
        Long userId = getUserIdByEmail(email);
        return subscriptionRepository.findByUserId(userId).stream()
                .map(sub -> topicRepository.findById(sub.getTopicId())
                        .orElse(null))
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    /**
     * Subscribes a user to a specific topic.
     * 
     * @param email the user's email address
     * @param topicId the topic ID to subscribe to
     * @throws ResourceNotFoundException if topic not found or user not found
     * @throws BusinessRuleException if user is already subscribed to the topic
     */
    public void subscribe(String email, Long topicId) {
        Long userId = getUserIdByEmail(email);
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Topic", "id", topicId);
        }
        if (subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
            throw new BusinessRuleException("Already subscribed to this topic", "ALREADY_SUBSCRIBED");
        }
        Subscription sub = new Subscription();
        sub.setUserId(userId);
        sub.setTopicId(topicId);
        subscriptionRepository.save(sub);
    }

    /**
     * Unsubscribes a user from a specific topic.
     * 
     * @param email the user's email address
     * @param topicId the topic ID to unsubscribe from
     * @throws ResourceNotFoundException if subscription not found
     */
    @Transactional
    public void unsubscribe(String email, Long topicId) {
        Long userId = getUserIdByEmail(email);
        if (!subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
            throw new ResourceNotFoundException("Subscription", "userId", userId);
        }
        subscriptionRepository.deleteByUserIdAndTopicId(userId, topicId);
    }

    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();
    }
}
