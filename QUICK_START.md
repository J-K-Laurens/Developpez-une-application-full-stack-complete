# ⚡ QUICK START - Démarrer en 5 minutes

## 🎯 Vue d'Ensemble Rapide

Vous avez une gestion centralisée des exceptions avec:
- ✅ 4 exceptions personnalisées
- ✅ 1 gestionnaire global (GlobalExceptionHandler)
- ✅ 1 DTO réponse standardisée
- ✅ 6 contrôleurs refactorisés
- ✅ Compilation réussie

---

## 🚀 Étape 1: Démarrer le Serveur (1 min)

```bash
cd back
mvn spring-boot:run
```

**Attendu:**
```
[INFO] Started MddApiApplication in 2.xxx seconds
Tomcat initialized with port(s): 8080
```

Serveur opérationnel sur `http://localhost:8080`

---

## 🧪 Étape 2: Tester les Exceptions (2 min)

### Test 1: Ressource non trouvée (404)
```bash
curl http://localhost:8080/api/users/999
```

**Réponse attendue**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur non trouvé(e) avec id: 999",
  "path": "/api/users/999"
}
```

### Test 2: Validation échouée (400)
```bash
curl http://localhost:8080/api/articles/-1
```

**Réponse attendue**:
```json
{
  "timestamp": "2026-03-08T20:45:30.123456",
  "status": 400,
  "error": "Argument Validation Failed",
  "message": "Les paramètres fournis sont invalides",
  "path": "/api/articles/-1",
  "validationErrors": {
    "id": "L'ID doit être supérieur à 0"
  }
}
```

### Test 3: Règle métier (409 - après enregistrement d'un utilisateur)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "existing@test.com",
    "name": "John",
    "password": "SecurePass123!"
  }'
```

**Réponse attendue (si email existe)**:
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

---

## 📚 Étape 3: Lire la Doc (2 min)

Lisez **UN SEUL** fichier pour comprendre:

### Option A: Vue synthétique (RECOMMANDÉ pour débuter)
```
README_EXCEPTIONS.md (3 min)
```

### Option B: Guide complet
```
EXCEPTION_HANDLING_GUIDE.md (5 min)
```

### Option C: Pour les développeurs avancés
```
ADVANCED_EXCEPTION_PATTERNS.md (10 min)
```

---

## ✅ Étape 4: Vérifier la Compilation (30 sec)

```bash
cd back
mvn clean compile -DskipTests
```

**Attendu**:
```
[INFO] BUILD SUCCESS
```

---

## 📊 Résumé de ce qui a changé

| Avant | Après |
|-------|-------|
| ResponseStatusException | ResourceNotFoundException |
| try/catch dans contrôleurs | GlobalExceptionHandler |
| Formats réponse différents | ErrorResponse único DTO |
| Pas de validation des IDs | @Min(1) sur tous les IDs |
| Exceptions non loggées | Toutes loggées avec SLF4J |

---

## 🔍 Structure des Fichiers Nouveaux

```
exception/
├── GlobalExceptionHandler.java     ← Gère TOUTES les exceptions
├── ResourceNotFoundException.java  ← 404 (ressource non trouvée)
├── ValidationException.java        ← 400 (validation métier)
├── BusinessRuleException.java      ← 409 (conflit métier)
└── UnauthorizedException.java      ← 403 (permission refusée)

dto/
└── ErrorResponse.java              ← Format réponse d'erreur
```

---

## 💡 Points Clés

1. **GlobalExceptionHandler**: Avec @ControllerAdvice intercepte TOUTES les exceptions
2. **ErrorResponse**: Réponse JSON standardisée avec timestamp, status, message, path
3. **@Validated**: Valide les paramètres (@Min, @Max, @NotNull, etc.)
4. **@Valid**: Valide le corps de la requête JSON
5. **Logging**: Tous les erreurs sont enregistrées avec SLF4J

---

## 🎓 3 Patterns d'Utilisation

### Pattern 1: Ressource non trouvée
```java
User user = repository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
```

### Pattern 2: Validation métier
```java
if (userRepository.existsByEmail(email)) {
    throw new BusinessRuleException("Email déjà utilisé", "DUPLICATE_EMAIL");
}
```

### Pattern 3: Vérifier les permissions
```java
if (!article.getUserId().equals(userId)) {
    throw new UnauthorizedException("Pas de permission", "article", "update");
}
```

---

## 🧪 Tests Recommandés (Checklist)

- [ ] `curl http://localhost:8080/api/users/999` → 404
- [ ] `curl http://localhost:8080/api/articles/-5` → 400
- [ ] `curl -X POST .../auth/register` (email existant) → 409
- [ ] `curl -X POST .../auth/login` (mauvais mot de passe) → 401
- [ ] Vérifier logs en console pour ERROR/WARN

---

## 📞 Besoin d'aide?

1. **Compilation échoue**: Lancer `mvn clean compile` à nouveau
2. **Exception non trouvée**: Vérifier l'import dans le contrôleur
3. **Format réponse différent**: Vérifier GlobalExceptionHandler
4. **Besoin d'une nouvelle exception**: Copier un fichier existant et adapter

---

## 🎉 C'est Tout!

Vous avez maintenant:
✅ Gestion centralisées des exceptions  
✅ Réponses standardisées  
✅ Code plus lisible  
✅ Erreurs loggées  
✅ Validation complète  

Pour davantage de détails, consultez les 4 guides de documentation dans le répertoire racine.

---

## 📖 Pour Aller Plus Loin

| Besoin | Fichier |
|--------|---------|
| Comprendre la structure | README_EXCEPTIONS.md |
| Voir des exemples JSON | EXCEPTION_HANDLING_GUIDE.md |
| Écrire des services | ADVANCED_EXCEPTION_PATTERNS.md |
| Tester avec cURL | TESTING_GUIDE.md |
| Vue d'ensemble complète | SYNTHESE_VISUELLE.md |

**Temps de lecture total pour maîtriser**: 30-45 min

---

**✨ Bonne chance! Vous êtes prêt à utiliser cette gestion des exceptions! ✨**

