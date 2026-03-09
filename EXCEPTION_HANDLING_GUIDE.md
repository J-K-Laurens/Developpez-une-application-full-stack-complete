# Guide des Réponses d'Erreur - API MDD

Ce document décrit les différents types de réponses d'erreur retournées par l'API et fournit des exemples pour chaque cas.

## Structure générale d'une réponse d'erreur

```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur non trouvé(e) avec id: 999",
  "path": "/api/users/999"
}
```

### Champs inclus:
- **timestamp**: Date/heure de l'erreur (ISO 8601)
- **status**: Code HTTP de l'erreur
- **error**: Types d'erreur (e.g., "Not Found", "Bad Request")
- **message**: Message explicatif de l'erreur
- **path**: Chemin de la requête qui a causé l'erreur
- **errorCode** (optionnel): Code métier spécifique
- **validationErrors** (optionnel): Détails des erreurs de validation

---

## Cas d'usage et exemples

### 1. Ressource non trouvée (404 Not Found)

**Endpoint**: `GET /api/users/999`

**Réponse**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur non trouvé(e) avec id: 999",
  "path": "/api/users/999"
}
```

**Code source dans le contrôleur**:
```java
@GetMapping("/{id}")
public ResponseEntity<UserDto.Response> getUserById(
    @PathVariable @Min(value = 1, message = "L'ID doit être supérieur à 0") Long id) {
    UserDto.Response user = userService.getUserById(id);
    if (user == null) {
        throw new ResourceNotFoundException("Utilisateur", "id", id);
    }
    return ResponseEntity.ok(user);
}
```

**Cas similaires**:
- `GET /api/articles/999` → Article non trouvé
- `GET /api/topics/999` → Topic non trouvé

---

### 2. Erreur de validation (400 Bad Request)

#### 2a. Validation des contraintes (Min, @NotNull, etc.)

**Endpoint**: `GET /api/users/-5`

**Réponse**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 400,
  "error": "Argument Validation Failed",
  "message": "Les paramètres fournis sont invalides",
  "path": "/api/users/-5",
  "validationErrors": {
    "id": "L'ID doit être supérieur à 0"
  }
}
```

#### 2b. Erreur de validation du corps de la requête (@Valid)

**Endpoint**: `POST /api/auth/register`

**Corps de la requête**:
```json
{
  "email": "invalid-email",
  "name": "",
  "password": "123"
}
```

**Réponse**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 400,
  "error": "Validation Failed",
  "message": "Erreur de validation: Les données fournies sont invalides",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "doit être une adresse e-mail valide",
    "name": "ne doit pas être vide",
    "password": "la taille doit être entre 8 et 100"
  }
}
```

---

### 3. Violation de règle métier (409 Conflict)

**Endpoint**: `POST /api/auth/register` (avec email existant)

**Corps de la requête**:
```json
{
  "email": "existing@example.com",
  "name": "John Doe",
  "password": "SecurePassword123!"
}
```

**Réponse**:
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

**Code source dans le contrôleur**:
```java
@PostMapping("/register")
public ResponseEntity<AuthDto.TokenResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
    try {
        userService.register(request.getEmail(), request.getName(), request.getPassword());
    } catch (org.springframework.web.server.ResponseStatusException e) {
        if (e.getStatus() == HttpStatus.CONFLICT) {
            throw new BusinessRuleException("Un compte existe déjà avec cet email", "DUPLICATE_USER");
        }
        throw e;
    }
    // ...
}
```

---

### 4. Authentification échouée (401 Unauthorized)

**Endpoint**: `POST /api/auth/login`

**Corps de la requête**:
```json
{
  "email": "user@example.com",
  "password": "WrongPassword123!"
}
```

**Réponse**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Email ou mot de passe incorrect",
  "path": "/api/auth/login"
}
```

---

### 5. Accès non autorisé (403 Forbidden)

**Endpoint**: `PUT /api/articles/1` (sans permission)

**Réponse**:
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous n'avez pas la permission de modifier cet article",
  "path": "/api/articles/1"
}
```

**Code source dans le service/contrôleur**:
```java
if (!currentUser.getId().equals(article.getUserId())) {
    throw new UnauthorizedException(
        "Vous n'avez pas la permission de modifier cet article",
        "article",
        "update"
    );
}
```

---

### 6. Erreur interne du serveur (500 Internal Server Error)

**Endpoint**: N'importe quel endpoint

**Réponse** (toute exception non gérée):
```json
{
  "timestamp": "2026-03-08T15:30:45.123456",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Une erreur interne s'est produite. Veuillez contacter le support.",
  "path": "/api/articles"
}
```

**Note**: Le service ne révèle pas les détails de l'erreur au client pour des raisons de sécurité. Les détails complets sont enregistrés dans les logs serveur.

---

## Tableau récapitulatif

| Exception | Code HTTP | Cas d'usage | Exemple |
|-----------|-----------|-----------|---------|
| `ResourceNotFoundException` | 404 | Ressource non trouvée | Utilisateur, Article, Topic inexistants |
| `ValidationException` | 400 | Validation métier échouée | Données invalides |
| `BusinessRuleException` | 409 | Violation de règle métier | Email déjà utilisé |
| `UnauthorizedException` | 403 | Accès non autorisé | Permission insuffisante |
| `MethodArgumentNotValidException` | 400 | Validation @Valid échouée | Requête mal formée |
| `BadCredentialsException` | 401 | Authentification échouée | Mot de passe incorrect |
| `Exception` (générique) | 500 | Erreur non gérée | Erreurs serveur inattendues |

---

## Messages d'erreur - Bonnes pratiques

### À faire ✅
- **Soyez spécifique**: "Utilisateur avec ID 999 non trouvé" au lieu de "Erreur"
- **Utilisez le contexte**: Incluez les identifiants/valeurs recherchées
- **Guidez l'utilisateur**: "Email ou mot de passe incorrect" vs "Authentification échouée"
- **Enregistrez les détails**: Loguez les erreurs sensibles côté serveur

### À éviter ❌
- Messages techniques exposés: "NullPointerException", "SQLException"
- Divulgation de structure base de données
- Révélation du système interne

---

## Intégration côté client Angular

Pour traiter les erreurs au format standardisé:

```typescript
// Dans votre intercepteur Error Interceptor
intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  return next.handle(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.error.validationErrors) {
        // Gestion des erreurs de validation
        console.error('Erreurs de validation:', error.error.validationErrors);
      } else {
        // Gestion du message d'erreur général
        console.error('Erreur:', error.error.message);
      }
      return throwError(error);
    })
  );
}
```

---

## Résumé des changements appliqués

1. ✅ **Création des exceptions personnalisées**:
   - `ResourceNotFoundException`
   - `ValidationException`
   - `BusinessRuleException`
   - `UnauthorizedException`

2. ✅ **DTO ErrorResponse** pour standardiser les réponses

3. ✅ **GlobalExceptionHandler** avec @ControllerAdvice pour centraliser la gestion

4. ✅ **Mise à jour des contrôleurs**:
   - Ajout de @Validated et @Min sur les paramètres
   - Remplacement de ResponseStatusException par les exceptions personnalisées
   - Ajout de commentaires JavaDoc

5. ✅ **Gestion de la validation**:
   - Support des erreurs de validation JSR303/JSR380
   - Inclusion des erreurs de champ dans les réponses

Cette approche offre une meilleure expérience utilisateur et facilite le débogage côté client.
