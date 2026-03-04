package com.openclassrooms.mddapi.services;

import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<Topic> getSubscribedTopics(String email) {
        Long userId = getUserIdByEmail(email);
        return subscriptionRepository.findByUserId(userId).stream()
                .map(sub -> topicRepository.findById(sub.getTopicId())
                        .orElse(null))
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    public void subscribe(String email, Long topicId) {
        Long userId = getUserIdByEmail(email);
        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thème introuvable");
        }
        if (subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Déjà abonné à ce thème");
        }
        Subscription sub = new Subscription();
        sub.setUserId(userId);
        sub.setTopicId(topicId);
        subscriptionRepository.save(sub);
    }

    @Transactional
    public void unsubscribe(String email, Long topicId) {
        Long userId = getUserIdByEmail(email);
        if (!subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Abonnement introuvable");
        }
        subscriptionRepository.deleteByUserIdAndTopicId(userId, topicId);
    }

    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"))
                .getId();
    }
}
