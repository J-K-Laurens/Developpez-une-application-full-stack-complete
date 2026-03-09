# Guide de Test - Gestion Centralisée des Exceptions

Ce fichier fournit des commandes cURL complètes et des réponses attendues pour tester tous les cas d'erreur.

---

## Configuration initiale

### URL de base
```
http://localhost:8080
```

### Démarrer le serveur
```bash
# Dans le répertoire back/
mvn spring-boot:run

# Ou via Maven Wrapper
./mvnw spring-boot:run
```

---

## 1. TESTS RESOURCENOTFOUNDEXCEPTION (404)

### 1.1 Utilisateur non trouvé

```bash
curl -X GET http://localhost:8080/api/users/999 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

**Réponse attendue (404)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur non trouvé(e) avec id: 999",
  "path": "/api/users/999"
}
```

### 1.2 Article non trouvé

```bash
curl -X GET http://localhost:8080/api/articles/999 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

**Réponse attendue (404)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Article non trouvé(e) avec id: 999",
  "path": "/api/articles/999"
}
```

### 1.3 Topic non trouvé

```bash
curl -X GET http://localhost:8080/api/topics/999 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Réponse attendue (404)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Topic non trouvé(e) avec id: 999",
  "path": "/api/topics/999"
}
```

---

## 2. TESTS VALIDATIONEXCEPTION (400 - Validation)

### 2.1 ID négatif (violation @Min)

```bash
curl -X GET http://localhost:8080/api/users/-5 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Réponse attendue (400)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 400,
  "error": "Argument Validation Failed",
  "message": "Les paramètres fournis sont invalides",
  "path": "/api/users/-5",
  "validationErrors": {
    "id": "L'ID doit être supérieur à 0"
  }
}
```

### 2.2 ID zéro (violation @Min)

```bash
curl -X GET http://localhost:8080/api/articles/0 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Réponse attendue (400)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 400,
  "error": "Argument Validation Failed",
  "message": "Les paramètres fournis sont invalides",
  "path": "/api/articles/0",
  "validationErrors": {
    "id": "L'ID doit être supérieur à 0"
  }
}
```

### 2.3 Validation du corps de requête (inscription)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid-email",
    "name": "",
    "password": "123"
  }'
```

**Réponse attendue (400)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
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

### 2.4 Validation du corps de requête (mise à jour utilisateur)

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "not-an-email",
    "name": null
  }'
```

**Réponse attendue (400)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 400,
  "error": "Validation Failed",
  "message": "Erreur de validation: Les données fournies sont invalides",
  "path": "/api/users/me",
  "validationErrors": {
    "email": "doit être une adresse e-mail valide",
    "name": "ne doit pas être vide"
  }
}
```

---

## 3. TESTS BUSINESSRULEEXCEPTION (409)

### 3.1 Email déjà utilisé (enregistrement)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "existing@example.com",
    "name": "John Doe",
    "password": "SecurePassword123!"
  }'
```

**Réponse attendue (409)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Un compte existe déjà avec cet email",
  "path": "/api/auth/register",
  "errorCode": "DUPLICATE_USER"
}
```

### 3.2 Double abonnement au même topic

```bash
# Supposons que l'utilisateur est déjà abonné au topic 1
curl -X POST http://localhost:8080/api/subscriptions/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Réponse attendue (409 - si implémenté)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Vous êtes déjà abonné à ce topic",
  "path": "/api/subscriptions/1",
  "errorCode": "ALREADY_SUBSCRIBED"
}
```

---

## 4. TESTS AUTHENTICATIONEXCEPTION (401)

### 4.1 Authentification échouée (mot de passe incorrect)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "WrongPassword123!"
  }'
```

**Réponse attendue (401)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Email ou mot de passe incorrect",
  "path": "/api/auth/login"
}
```

### 4.2 Utilisateur inexistant (login)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "SomePassword123!"
  }'
```

**Réponse attendue (401)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Email ou mot de passe incorrect",
  "path": "/api/auth/login"
}
```

### 4.3 Token JWT expiré ou invalide

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer INVALID_TOKEN"
```

**Réponse attendue (401)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Identifiants invalides ou session expirée",
  "path": "/api/users/me"
}
```

---

## 5. TESTS UNAUTHORIZEDEXCEPTION (403 - À implémenter)

### 5.1 Modification d'article sans permission

```bash
# Supposons:
# - Article 1 appartient à l'utilisateur 5
# - Le token appartient à l'utilisateur 2

curl -X PUT http://localhost:8080/api/articles/1 \
  -H "Authorization: Bearer USER_2_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Titre modifié",
    "content": "Contenu modifié"
  }'
```

**Réponse attendue (403 - si implémenté)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous n'avez pas la permission de modifier cet article",
  "path": "/api/articles/1"
}
```

### 5.2 Suppression de commentaire d'autrui

```bash
# Supposons:
# - Commentaire 1 a été écrit par l'utilisateur 3
# - Le token appartient à l'utilisateur 4

curl -X DELETE http://localhost:8080/api/articles/1/comments/1 \
  -H "Authorization: Bearer USER_4_TOKEN"
```

**Réponse attendue (403)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous ne pouvez supprimer que vos propres commentaires",
  "path": "/api/articles/1/comments/1"
}
```

---

## 6. TESTS EXCEPTION GÉNÉRIQUE (500)

### 6.1 Erreur interne du serveur (non gérée)

Cette erreur se produit quand une exception non prévue est levée.

**Exemple**: Erreur de base de données, NullPointerException, etc.

**Réponse attendue (500)**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Une erreur interne s'est produite. Veuillez contacter le support.",
  "path": "/api/articles"
}
```

**Logs serveur** (console/logs):
```
[ERROR] Exception non gérée
java.lang.NullPointerException: ...
    at com.openclassrooms.mddapi.services.ArticleService.findById(ArticleService.java:45)
    ...
```

---

## 7. TESTS CAS DE SUCCÈS (200/201)

### 7.1 Enregistrement réussi

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "name": "Jane Doe",
    "password": "SecurePassword123!"
  }'
```

**Réponse attendue (200)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuZXd1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjQ2NzY1NTMwfQ.2vvTR_X2Z0AQE1gHQ2uZ3vZ1Z0AQE1gHQ2uZ3vZ1"
}
```

### 7.2 Connexion réussie

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!"
  }'
```

**Réponse attendue (200)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjQ2NzY1NTMwfQ.2vvTR_X2Z0AQE1gHQ2uZ3vZ1Z0AQE1gHQ2uZ3vZ1"
}
```

### 7.3 Récupération d'utilisateur réussie

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Réponse attendue (200)**:
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2026-02-15T10:30:00",
  "updatedAt": "2026-03-08T15:45:30"
}
```

### 7.4 Création d'article réussie

```bash
curl -X POST http://localhost:8080/api/articles \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Mon Article",
    "content": "Contenu de mon article",
    "featured": true
  }'
```

**Réponse attendue (201)**:
```json
{
  "id": 42,
  "title": "Mon Article",
  "content": "Contenu de mon article",
  "featured": true,
  "userId": 1,
  "createdAt": "2026-03-08T20:45:30"
}
```

---

## Checklist de Test

Utilisez cette checklist pour valider l'implémentation complète:

- [ ] **404 Tests**
  - [ ] GET /api/users/999 → 404
  - [ ] GET /api/articles/999 → 404
  - [ ] GET /api/topics/999 → 404

- [ ] **400 Validation Tests**
  - [ ] GET /api/users/-5 → 400 (Min violation)
  - [ ] GET /api/articles/0 → 400 (Min violation)
  - [ ] POST /api/auth/register avec email invalide → 400

- [ ] **409 Business Rule Tests**
  - [ ] POST /api/auth/register avec email existant → 409
  - [ ] POST /api/subscriptions/1 (déjà abonné) → 409

- [ ] **401 Authentication Tests**
  - [ ] POST /api/auth/login avec mauvais mot de passe → 401
  - [ ] GET /api/users/me sans token → 401
  - [ ] GET /api/users/me avec token expiré → 401

- [ ] **403 Authorization Tests (si implémenté)**
  - [ ] PUT /api/articles/1 (sans permission) → 403
  - [ ] DELETE /api/articles/1/comments/1 (commentaire d'autrui) → 403

- [ ] **200/201 Success Tests**
  - [ ] POST /api/auth/register (nouveau user) → 200 + token
  - [ ] POST /api/auth/login → 200 + token
  - [ ] GET /api/users/1 → 200 + user data
  - [ ] POST /api/articles → 201 + article data

---

## Outils recommandés pour tester

### Postman
1. Importer/créer une collection avec les requêtes
2. Organiser par dossiers (success, 4xx, 5xx)
3. Ajouter des tests pour vérifier les codes HTTP
4. Générer un rapport de tests

### cURL (Command Line)
```bash
# Créer un fichier test.sh avec tous les tests:
#!/bin/bash

echo "Test 1: Utilisateur non trouvé"
curl -X GET http://localhost:8080/api/users/999 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  | jq '.'

echo "Test 2: Validation échouée"
curl -X GET http://localhost:8080/api/users/-5 \
  | jq '.'

# ... etc
```

### REST Client (VS Code Extension)
```
@host = http://localhost:8080
@token = YOUR_JWT_TOKEN

### Test 1: Utilisateur non trouvé
GET {{host}}/api/users/999
Authorization: Bearer {{token}}

### Test 2: Validation échouée
GET {{host}}/api/users/-5

### Test 3: Email déjà utilisé
POST {{host}}/api/auth/register
Content-Type: application/json

{
  "email": "existing@example.com",
  "name": "John",
  "password": "Pass123!"
}
```

---

## Troubleshooting

### Problème: Toutes les requêtes retournent 500
**Cause**: Le GlobalExceptionHandler n'est pas démarré ou il y a une erreur de compilation  
**Solution**:
```bash
# Vérifier la compilation
mvn clean compile

# Vérifier que GlobalExceptionHandler est dans le classpath avec @ControllerAdvice
```

### Problème: ValidationException n'est pas levée
**Cause**: @Validated ou @Valid manquant sur les paramètres/corps  
**Solution**: Vérifier que tous les contrôleurs importent:
```java
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Min;
```

### Problème: ResourceNotFoundException n'est pas capturée
**Cause**: Service qui lève une exception différente  
**Solution**: Vérifier que le service lève bien `ResourceNotFoundException`

---

## Logs à survey en cas d'erreur

Dans la console/logs serveur, cherchez:

```
// ExceptionHandlers appelé
[WARN] Ressource non trouvée: Utilisateur non trouvé(e) avec id: 999

// Validation
[WARN] Erreur de validation: ...

// Erreurs non gérées
[ERROR] Exception non gérée
java.lang.NullPointerException...
```

---

## Conclusion

Cette suite de tests couvre tous les cas d'exception et permet de valider que la gestion centralisée fonctionne correctement.

Pour plus d'informations, consultez `EXCEPTION_HANDLING_GUIDE.md` et `ADVANCED_EXCEPTION_PATTERNS.md`.

