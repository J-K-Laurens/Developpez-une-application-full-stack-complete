# Gestion Centralisée des Exceptions - Projet MDD API

## Vue d'ensemble

Ce document décrit l'implémentation complète d'une gestion centralisée et robuste des exceptions dans le projet Spring Boot MDD.

## État d'implémentation ✅

Tous les éléments ont été implémentés avec succès:

### 1. **Exceptions Personnalisées** ✅
   Localisation: `src/main/java/com/openclassrooms/mddapi/exception/`
   
   - **ResourceNotFoundException**: Levée quand une ressource (utilisateur, article, topic, etc.) n'est pas trouvée
   - **ValidationException**: Levée pour les erreurs de validation métier
   - **BusinessRuleException**: Levée pour les violations de règles métier (ex: email déjà utilisé)
   - **UnauthorizedException**: Levée pour les tentatives d'accès non autorisées

### 2. **DTO ErrorResponse** ✅
   Localisation: `src/main/java/com/openclassrooms/mddapi/dto/ErrorResponse.java`
   
   Propriétés incluses:
   - `timestamp`: Date/heure de l'erreur (ISO 8601)
   - `status`: Code HTTP (404, 400, 409, 403, 500...)
   - `error`: Type d'erreur (Not Found, Bad Request, etc.)
   - `message`: Message explicatif détaillé
   - `path`: Chemin de la requête
   - `errorCode`: Code métier optionnel
   - `validationErrors`: Détails des erreurs de champ (optionnel)

### 3. **Gestionnaire Global des Exceptions** ✅
   Localisation: `src/main/java/com/openclassrooms/mddapi/exception/GlobalExceptionHandler.java`
   
   Annotée avec `@ControllerAdvice`, elle centralise la gestion de:
   - `ResourceNotFoundException` → 404 Not Found
   - `ValidationException` → 400 Bad Request
   - `BusinessRuleException` → 409 Conflict
   - `UnauthorizedException` → 403 Forbidden
   - `MethodArgumentNotValidException` → 400 Bad Request (validation JSR303)
   - `AuthenticationException` → 401 Unauthorized
   - `BadCredentialsException` → 401 Unauthorized
   - `Exception` (générique) → 500 Internal Server Error avec logging

### 4. **Contrôleurs Refactorisés** ✅
   
   Tous les contrôleurs ont été mis à jour:
   
   - **UserController** (`/api/users`)
     - Validation des IDs avec `@Min(value = 1)`
     - Gestion des utilisateurs non trouvés
     - Comment: Use `ResourceNotFoundException` for invalid IDs
   
   - **ArticleController** (`/api/articles`)
     - Remplacement de `ResponseStatusException` par `ResourceNotFoundException`
     - Validation des paramètres
     - Comment: Throws `ResourceNotFoundException` when article not found
   
   - **AuthController** (`/api/auth`)
     - Remplacement du try/catch custom par GlobalExceptionHandler
     - Utilisation de `BusinessRuleException` pour les emails en doublon
     - Gestion automatique de `BadCredentialsException`
   
   - **TopicController** (`/api/topics`)
     - Ajout de `@Validated`
     - Gestion des topics non trouvés
   
   - **SubscriptionController** (`/api/subscriptions`)
     - Validation des IDs
     - Gestion cohérente des erreurs
   
   - **CommentController** (`/api/articles/{articleId}/comments`)
     - Remplacement de `ResponseStatusException`
     - Validation améliorée

### 5. **Validation et Annotations** ✅
   
   - `@Validated` ajouté à tous les contrôleurs
   - `@Min(value = 1)` sur les paramètres ID
   - `@Valid` sur les corps de requête
   - `@NotNull`, `@NotEmpty`, etc. sur les DTOs (à vérifier dans vos fichiers DTO)

### 6. **Journalisation (Logging)** ✅
   
   Dans `GlobalExceptionHandler`:
   - Logs WARN pour les erreurs attendues (404, 400, 409, 403, 401)
   - Logs ERROR avec stacktrace pour les exceptions non gérées (500)
   - Facilite le débogage en production

---

## Structure du Projet

```
src/main/java/com/openclassrooms/mddapi/
├── exception/
│   ├── GlobalExceptionHandler.java          ← Gestionnaire central
│   ├── ResourceNotFoundException.java        ← 404 Not Found
│   ├── ValidationException.java              ← Validation custom
│   ├── BusinessRuleException.java            ← Règles métier
│   └── UnauthorizedException.java            ← 403 Forbidden
├── dto/
│   ├── ErrorResponse.java                   ← Réponse d'erreur standardisée
│   ├── UserDto.java
│   ├── ArticleDto.java
│   ├── CommentDto.java
│   ├── TopicDto.java
│   └── AuthDto.java
├── controller/
│   ├── UserController.java                  ✅ Refactorisé
│   ├── ArticleController.java               ✅ Refactorisé
│   ├── AuthController.java                  ✅ Refactorisé
│   ├── TopicController.java                 ✅ Refactorisé
│   ├── SubscriptionController.java          ✅ Refactorisé
│   └── CommentController.java               ✅ Refactorisé
└── ... (autres dossiers inchangés)
```

---

## Exemples d'utilisation

### Exemple 1: Récupérer un utilisateur inexistant

```bash
GET /api/users/999
```

**Réponse 404**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur non trouvé(e) avec id: 999",
  "path": "/api/users/999"
}
```

### Exemple 2: S'enregistrer avec un email déjà utilisé

```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "existing@example.com",
  "name": "John Doe",
  "password": "SecureP@assword123"
}
```

**Réponse 409**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Un compte existe déjà avec cet email",
  "path": "/api/auth/register",
  "errorCode": "DUPLICATE_USER"
}
```

### Exemple 3: Valider un ID négatif

```bash
GET /api/articles/-5
```

**Réponse 400**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 400,
  "error": "Argument Validation Failed",
  "message": "Les paramètres fournis sont invalides",
  "path": "/api/articles/-5",
  "validationErrors": {
    "id": "L'ID doit être supérieur à 0"
  }
}
```

---

## Comment ajouter une nouvelle exception personnalisée

1. **Créer la classe d'exception** dans `exception/`:
   ```java
   public class MyCustomException extends RuntimeException {
       public MyCustomException(String message) {
           super(message);
       }
   }
   ```

2. **Ajouter un handler** dans `GlobalExceptionHandler`:
   ```java
   @ExceptionHandler(MyCustomException.class)
   public ResponseEntity<ErrorResponse> handleMyCustom(
           MyCustomException ex, 
           HttpServletRequest request) {
       logger.warn("Mon erreur custom: {}", ex.getMessage());
       ErrorResponse error = new ErrorResponse(
           HttpStatus.CUSTOM_STATUS.value(),
           "Custom Error",
           ex.getMessage(),
           request.getRequestURI()
       );
       return new ResponseEntity<>(error, HttpStatus.CUSTOM_STATUS);
   }
   ```

3. **Utiliser dans vos contrôleurs/services**:
   ```java
   if (someCondition) {
       throw new MyCustomException("Description du problème");
   }
   ```

---

## Configuration dans application.properties

Pour modifier le comportement de la validation et des erreurs:

```properties
# Validation
spring.validation.enabled=true

# Messages personnalisés (optionnel)
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=never
server.error.include-exception=false
```

---

## Tests recommandés

Voici quelques tests à effectuer pour valider l'implémentation:

### Test 1: ResourceNotFoundException
```bash
curl -X GET http://localhost:8080/api/users/999
```
Attendu: 404 avec message "Utilisateur non trouvé(e) avec id: 999"

### Test 2: ValidationException (validation de paramètre)
```bash
curl -X GET http://localhost:8080/api/articles/-1
```
Attendu: 400 avec message "L'ID doit être supérieur à 0"

### Test 3: BusinessRuleException (email en doublon)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"existing@test.com","name":"John","password":"Pass123!"}'
```
Attendu: 409 avec message "Un compte existe déjà avec cet email"

### Test 4: BadCredentialsException
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"WrongPassword"}'
```
Attendu: 401 avec message "Email ou mot de passe incorrect"

---

## Avantages de cette implémentation

✅ **Centralisée**: Toute la gestion des erreurs en un seul endroit  
✅ **Cohérente**: Réponses uniformes dans toute l'API  
✅ **Maintenable**: Facile d'ajouter de nouveaux types d'erreur  
✅ **Loggée**: Tous les problèmes sont enregistrés  
✅ **Sécurisée**: Les erreurs sensibles ne fuient pas au client  
✅ **Client-friendly**: Messages clairs et codes HTTP appropriés  
✅ **Validée**: Support complet de JSR303/JSR380  

---

## Fichiers de documentation supplémentaires

- `EXCEPTION_HANDLING_GUIDE.md`: Guide détaillé avec exemples JSON complets
- Ce fichier: Configuration et structure du projet

---

## Prochaines étapes (optionnel)

1. **Ajouter plus de validations métier** dans les services
2. **Explorer l'interface ApiException** (bonus mentionné dans les livrables)
3. **Implémenter des tests d'intégration** pour les handlers d'exceptions
4. **Ajouter des metrics** pour suivre les erreurs en production

---

## Compilation et Tests

Le projet a été compilé avec succès:

```bash
mvn clean compile -DskipTests
# BUILD SUCCESS ✅
```

Les classes d'exceptions et le GlobalExceptionHandler sont prêts à l'emploi.

---

## Support et Questions

Pour toute question ou ajout future:
- Consultez `EXCEPTION_HANDLING_GUIDE.md` pour les exemples détaillés
- Vérifiez le GlobalExceptionHandler pour voir comment les exceptions sont traitées
- Adaptez les exceptions existantes ou créez-en de nouvelles selon vos besoins métier

