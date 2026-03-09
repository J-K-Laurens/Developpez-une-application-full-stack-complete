# Guide Avancé - Exceptions dans les Services

Ce fichier fournit des exemples avancés de comment utiliser et lever les exceptions personnalisées dans vos services.

## Architecture recommandée

```
Controller (reçoit les exceptions du service)
    ↓ (appelle)
Service (lève les exceptions)
    ↓ (appelle)
Repository (peut ne pas lever d'exception personnalisée)
```

---

## Patterns et Exemples

### Pattern 1: Validation dans le Service

#### Avant (sans gestion d'exception centralisée):
```java
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserDto.Response getUserById(Long id) {
        return userRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Utilisateur non trouvé"
            ));
    }
}
```

#### Après (avec gestion d'exception centralisée):
```java
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserDto.Response getUserById(Long id) {
        return userRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
    }
}
```

**Avantage**: GlobalExceptionHandler se charge de formatter la réponse 404.

---

### Pattern 2: Validation Métier dans le Service

```java
@Service
public class AuthService {
    private final UserRepository userRepository;
    
    /**
     * Enregistre un nouvel utilisateur.
     * @throws BusinessRuleException si l'email existe déjà
     */
    public void register(String email, String name, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException(
                "Un compte existe déjà avec cet email",
                "DUPLICATE_EMAIL"
            );
        }
        // Créer l'utilisateur...
    }
    
    /**
     * Met à jour un utilisateur.
     * @throws ValidationException si les données sont invalides
     */
    public UserDto.Response updateUser(Long userId, UserDto.Request request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("name", "Le nom ne peut pas être vide");
            throw new ValidationException("Données utilisateur invalides", errors);
        }
        // Mettre à jour l'utilisateur...
    }
}
```

**Avantage**: Le contrôleur reste simple et GlobalExceptionHandler gère le formatage.

---

### Pattern 3: Gestion des Permissions

```java
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    
    /**
     * Met à jour un article.
     * @throws ResourceNotFoundException si l'article n'existe pas
     * @throws UnauthorizedException si l'utilisateur n'est pas propriétaire
     */
    public ArticleDto.Detail updateArticle(Long articleId, Long userId, 
                                           ArticleDto.UpdateRequest request) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        
        if (!article.getUserId().equals(userId)) {
            throw new UnauthorizedException(
                "Vous n'avez pas la permission de modifier cet article",
                "article",
                "update"
            );
        }
        
        // Mettre à jour l'article...
        return convertToDto(article);
    }
}
```

**Response 403** si non-propriétaire:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous n'avez pas la permission de modifier cet article",
  "path": "/api/articles/1"
}
```

---

### Pattern 4: Chaînage et Cascade d'Exceptions

```java
@Service
public class SubscriptionService {
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final SubscriptionRepository subscriptionRepository;
    
    /**
     * Abonne un utilisateur à un topic.
     * @throws ResourceNotFoundException si l'utilisateur ou le topic n'existe pas
     * @throws BusinessRuleException si déjà abonné
     */
    public void subscribe(String email, Long topicId) {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
        
        // Vérifier que le topic existe
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
        
        // Vérifier que l'utilisateur n'est pas déjà abonné
        if (subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topicId)) {
            throw new BusinessRuleException(
                "Vous êtes déjà abonné à ce topic",
                "ALREADY_SUBSCRIBED"
            );
        }
        
        // Créer l'abonnement...
    }
}
```

---

### Pattern 5: Exceptions avec Contexte Détaillé

```java
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    
    /**
     * Supprime un commentaire après vérification des permissions.
     */
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Commentaire",
                "id",
                commentId
            ));
        
        // Vérifier les permissions
        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException(
                "Vous ne pouvez supprimer que vos propres commentaires",
                "comment",
                "delete"
            );
        }
        
        // Vérifier que l'article existe encore
        articleRepository.findById(comment.getArticleId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Article parent",
                "id",
                comment.getArticleId()
            ));
        
        // Supprimer le commentaire...
    }
}
```

---

## Intégration Complète: Exemple Bout en Bout

### Contrôleur
```java
@RestController
@RequestMapping("/api/articles")
@Validated
public class ArticleController {
    private final ArticleService articleService;
    
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto.Detail> updateArticle(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ArticleDto.UpdateRequest request,
            Authentication auth) {
        
        Long userId = getUserIdFromAuth(auth);
        ArticleDto.Detail updated = articleService.updateArticle(id, userId, request);
        return ResponseEntity.ok(updated);
    }
    
    private Long getUserIdFromAuth(Authentication auth) {
        // Implémenter selon votre logique...
        return 1L;
    }
}
```

### Service
```java
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    
    public ArticleDto.Detail updateArticle(Long articleId, Long userId, 
                                           ArticleDto.UpdateRequest request) {
        // 1. Vérifier que l'article existe (404)
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        
        // 2. Vérifier les permissions (403)
        if (!article.getUserId().equals(userId)) {
            throw new UnauthorizedException(
                "Vous n'avez pas la permission de modifier cet article",
                "article",
                "update"
            );
        }
        
        // 3. Valider les données (400)
        validateUpdateRequest(request);
        
        // 4. Mettre à jour
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        articleRepository.save(article);
        
        return convertToDto(article);
    }
    
    private void validateUpdateRequest(ArticleDto.UpdateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("title", "Le titre est requis");
            throw new ValidationException("Données invalides", errors);
        }
    }
}
```

### GlobalExceptionHandler
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {
        // 404
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, 
            HttpServletRequest request) {
        // 403
        ErrorResponse error = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, 
            HttpServletRequest request) {
        // 400 avec détails des erreurs
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            ex.getMessage(),
            request.getRequestURI(),
            ex.getValidationErrors()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
```

### Scénarios de Test

**Scénario 1**: Article inexistant
```bash
PUT /api/articles/999 HTTP/1.1
```
→ **404** "Article non trouvé(e) avec id: 999"

**Scénario 2**: Utilisateur sans permission
```bash
PUT /api/articles/1 HTTP/1.1
(userId=2, article.userId=1)
```
→ **403** "Vous n'avez pas la permission de modifier cet article"

**Scénario 3**: Titre vide
```bash
PUT /api/articles/1 HTTP/1.1
{ "title": "", "content": "..." }
```
→ **400** avec validationErrors: { "title": "Le titre est requis" }

---

## Bonnes Pratiques

### ✅ À faire

1. **Lever l'exception au plus bas niveau possible**
   ```java
   // Bon: Lever dans le service
   public User getUser(Long id) {
       return userRepository.findById(id)
           .orElseThrow(() -> new ResourceNotFoundException(...));
   }
   ```

2. **Inclure le contexte dans le message**
   ```java
   // Bon
   throw new ResourceNotFoundException("Utilisateur", "email", email);
   
   // Moins bon
   throw new ResourceNotFoundException("Utilisateur non trouvé");
   ```

3. **Utiliser le code d'erreur pour les cas métier**
   ```java
   throw new BusinessRuleException(
       "Email déjà utilisé",
       "DUPLICATE_EMAIL"  // ← Code métier
   );
   ```

4. **Valider tôt, échouer vite**
   ```java
   public void register(String email, String name, String password) {
       // Vérifier les contraines AVANT les opérations coûteuses
       if (email == null || !emailValid(email)) {
           throw new ValidationException("Email invalide");
       }
       if (userRepository.existsByEmail(email)) {
           throw new BusinessRuleException("Email déjà utilisé", "DUPLICATE");
       }
   }
   ```

### ❌ À éviter

1. **Laisser les exceptions remonter sans les traiter**
   ```java
   // Mauvais: NPE ou autres erreurs non gérées
   public User getUser(Long id) {
       return userRepository.findById(id).get(); // Peut lever NoSuchElementException
   }
   ```

2. **Exceptons trop génériques**
   ```java
   // Mauvais
   throw new Exception("Erreur");
   
   // Bon
   throw new ResourceNotFoundException("Utilisateur", "id", id);
   ```

3. **Mélanger exceptions checked et unchecked**
   ```java
   // Mauvais: RuntimeExceptions + exceptions checked
   // Bon: Utiliser que des RuntimeException pour plus de flexibilité
   ```

4. **Capturer et ignorer les exceptions**
   ```java
   // Très mauvais
   try {
       userRepository.findById(id).orElseThrow(...);
   } catch (Exception e) {
       // Ignoré silencieusement!
   }
   ```

---

## Tester les Exceptions

### Exemple de Test Unitaire

```java
@SpringBootTest
public class ArticleServiceTest {
    
    @Autowired
    private ArticleService articleService;
    
    @MockBean
    private ArticleRepository articleRepository;
    
    @Test
    public void testUpdateArticleNotFound() {
        // Arrange
        when(articleRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            articleService.updateArticle(999L, 1L, new ArticleDto.UpdateRequest());
        });
    }
    
    @Test
    public void testUpdateArticleUnauthorized() {
        // Arrange
        Article article = new Article();
        article.setId(1L);
        article.setUserId(10L); // Propriétaire différent
        
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        
        // Act & Assert
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            articleService.updateArticle(1L, 2L, new ArticleDto.UpdateRequest());
        });
        
        assertEquals("Vous n'avez pas la permission de modifier cet article", ex.getMessage());
    }
}
```

---

## Résumé

| Type d'Exception | Où la lever | Code HTTP | Utilisation |
|---|---|---|---|
| `ResourceNotFoundException` | Service quand `findById().isEmpty()` | 404 | Ressource non trouvée |
| `ValidationException` | Service lors de validation métier | 400 | Données invalides |
| `BusinessRuleException` | Service lors de conflit métier | 409 | Email en doublon, déjà abonné |
| `UnauthorizedException` | Service lors de check permission | 403 | Utilisateur sans permission |

Toutes ces exceptions remontent au contrôleur qui les laisse passer à **GlobalExceptionHandler**, qui les formate en réponse JSON standardisée.

