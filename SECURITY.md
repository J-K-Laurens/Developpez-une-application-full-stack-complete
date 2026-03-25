# 🔐 Guide de Sécurité - MDD API

## Gestion des Secrets et Variables d'Environnement

### ⚠️ RÈGLES ESSENTIELLES

1. **JAMAIS committer les secrets** 
   - `.env` files
   - Clés privées
   - Mots de passe

2. **JAMAIS afficher les secrets en clair**
   - Documentation
   - Logs
   - Code source

3. **TOUJOURS utiliser des variables d'environnement**
   - En développement: fichier `.env`
   - En production: variables d'environnement du serveur/cloud

---

## Fonctionnalité: JWT Token & Logout

### 🎯 Objectif

Quand un utilisateur se déconnecte (`POST /api/auth/logout`), son token JWT est supprimé côté client. Les tokens JWT expirent naturellement selon leur durée de validité configurée (par défaut 24h).

### 🏗️ Architecture

```
1. User calls POST /api/auth/logout
   ↓
2. Frontend removes JWT token from localStorage/sessionStorage
   ↓
3. On next request, no Authorization header is sent
   ↓
4. Backend rejects requests without valid token (401 Unauthorized)
   ↓
5. Token expires naturally after TTL (no server-side tracking needed)
```

### 📦 Dépendances

- **JWT**: Tokens auto-suffisants avec expiration intégrée
- **Client Storage**: localStorage/sessionStorage pour persister le token côté client

### 🚀 Gestion du Logout

**Flux Logout:**
```
User clicks "Logout" button
↓
Frontend calls POST /api/auth/logout (optional server notification)
↓
Token removed from client storage
↓
User redirected to login page
↓
Future requests have no Authorization header
↓
Backend returns 401 Unauthorized
```

**Expiration Naturelle:**
- Tokens JWT contiennent une date d'expiration (`exp` claim)
- Pas besoin de blacklist côté serveur
- Sécurité assurée par l'expiration automatique

---

## Variables d'Environnement Requises

### Développement Local

**Backend** (`back/.env`):
```
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_dev_secret_at_least_32_chars
```

### Production

**Variables d'Environnement du Serveur:**
```bash
export DB_HOST=prod-db.example.com
export DB_USERNAME=prod_user
export DB_PASSWORD=<secret>
export JWT_SECRET=<secret_sécurisé>
export SPRING_PROFILES_ACTIVE=prod
```

---

## Générer une Clé JWT Sécurisée

### Avec OpenSSL (Recommandé ✅)

```bash
# Générer une clé de 32 bytes en hexadécimal
openssl rand -hex 32

# Exemple de sortie:
# 7a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a
```

### Avec Python

```bash
python3 -c "import secrets; print(secrets.token_hex(32))"
```

### Avec Node.js

```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

---

## Architecture de Sécurité

### Backend Spring Boot

#### Profils d'Application

```
application.properties       → Configuration commune
application-dev.properties   → Config développement (valeurs par défaut de test)
application-prod.properties  → Config production (SANS valeurs par défaut)
```

#### Activation des Profils

```bash
# Développement
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run

# Production
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

### Différences Dev vs Prod

| Paramètre | Dev | Prod |
|-----------|-----|------|
| `jwt.secret` | Valeur par défaut | Variable d'env REQUISE |
| `show-sql` | `true` | `false` |
| `ddl-auto` | `update` | `validate` |
| Swagger | Activé | Désactivé |
| Logging | DEBUG | WARN |

---

## Checklist Sécurité

### Avant le Déploiement en Production

- [ ] Générer une clé JWT unique et sécurisée
- [ ] Configurer DB_USERNAME et DB_PASSWORD sécurisés
- [ ] Vérifier que JWT_SECRET est dans les variables d'env
- [ ] Activar le profil `prod`: `SPRING_PROFILES_ACTIVE=prod`
- [ ] Désactiver Swagger: `springdoc.swagger-ui.enabled=false`
- [ ] Éviter les logs SQL: `spring.jpa.show-sql=false`
- [ ] DDL validation seulement: `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Vérifier HTTPS forcé
- [ ] Vérifier CORS configuré correctement
- [ ] Vérifier Rate Limiting activé
- [ ] Logs centralisés (ELK, CloudWatch, etc.)
- [ ] Tester le logout: Token supprimé côté client

### En Développement Local

- [ ] Copier `.env.example` vers `.env`
- [ ] Remplir les valeurs de test
- [ ] Importer le profil `dev`: `export SPRING_PROFILES_ACTIVE=dev`
- [ ] Vérifier que `.env` est ignoré par Git
- [ ] Tester le logout en Postman/Insomnia:
  ```bash
  # Login
  POST http://localhost:8080/api/auth/login
  {"email": "test@example.com", "password": "password"}
  Response: {"token": "eyJhbGc..."}
  
  # Use token
  GET http://localhost:8080/api/auth/me
  Header: Authorization: Bearer eyJhbGc...
  Response: {"id": 1, "email": "test@example.com"}
  
  # Logout
  POST http://localhost:8080/api/auth/logout
  Response: "Logged out successfully"
  
  # Try using token again (will fail after expiration)
  GET http://localhost:8080/api/auth/me
  Header: Authorization: Bearer eyJhbGc...
  Response: 401 Unauthorized (token expired)
  ```

---

## Outils et Ressources

### Génération de Secrets

- 🔐 [OpenSSL](https://www.openssl.org/) - Outil CLI standard
- 🔐 [UUID Generator](https://www.uuidgenerator.net/) - En ligne
- 🔐 [Random.org](https://www.random.org/) - Nombres aléatoires

### Gestion des Secrets en Production

- 🏗️ **AWS Secrets Manager** - Pour AWS
- 🏗️ **Azure Key Vault** - Pour Azure
- 🏗️ **HashiCorp Vault** - Universel, on-premise
- 🏗️ **Docker Secrets** - Pour Docker/Kubernetes
- 🏗️ **Heroku Config Vars** - Pour Heroku

### Validation de Sécurité

- 🔍 **OWASP Top 10** - Checklist sécurité web
- 🔍 **Spring Security** - Documentation
- 🔍 **JWT Best Practices** - https://tools.ietf.org/html/rfc8725

---

## Troubleshooting

### ❌ "jwt.secret not configured"

**Cause:** Variable d'environnement JWT_SECRET manquante

**Solution:**
```bash
# Vérifier que la variable est définie
echo $JWT_SECRET

# Définir la variable
export JWT_SECRET="votre_clé_here"

# Redémarrer l'application
mvn spring-boot:run
```

### ❌ "Property not found"

**Cause:** Spring profile non activé

**Solution:**
```bash
# Vérifier le profil
echo $SPRING_PROFILES_ACTIVE

# Activer le profil dev
export SPRING_PROFILES_ACTIVE=dev
```

### ✅ Vérifier les Configuration

```bash
# Afficher toutes les properties chargées
curl http://localhost:8080/actuator/env | jq

# Vérifier que le JWT secret est chargé
curl http://localhost:8080/actuator/env | jq '.propertySources[] | select(.name | contains("jwt"))'
```

### ✅ Tester Logout

```bash
# Tester l'API de logout
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <votre_token>"

# Le token est supprimé côté client, expiration naturelle côté serveur
```

---

## Ressources Additionnelles

- 📖 [Spring Security Documentation](https://spring.io/projects/spring-security)
- 📖 [12 Factor App - Config](https://12factor.net/config)
- 📖 [OWASP Cheat Sheet](https://cheatsheetseries.owasp.org/)
- 📖 [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Dernière mise à jour:** 11/03/2026


---

## Générer une Clé JWT Sécurisée

### Avec OpenSSL (Recommandé ✅)

```bash
# Générer une clé de 32 bytes en hexadécimal
openssl rand -hex 32

# Exemple de sortie:
# 7a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a
```

### Avec Python

```bash
python3 -c "import secrets; print(secrets.token_hex(32))"
```

### Avec Node.js

```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

---

## Architecture de Sécurité

### Backend Spring Boot

#### Profils d'Application

```
application.properties       → Configuration commune
application-dev.properties   → Config développement (valeurs par défaut de test)
application-prod.properties  → Config production (SANS valeurs par défaut)
```

#### Activation des Profils

```bash
# Développement
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run

# Production
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

### Différences Dev vs Prod

| Paramètre | Dev | Prod |
|-----------|-----|------|
| `jwt.secret` | Valeur par défaut | Variable d'env REQUISE |
| `show-sql` | `true` | `false` |
| `ddl-auto` | `update` | `validate` |
| Swagger | Activé | Désactivé |
| Logging | DEBUG | WARN |

---

## Checklist Sécurité

### Avant le Déploiement en Production

- [ ] Générer une clé JWT unique et sécurisée
- [ ] Configurer DB_USERNAME et DB_PASSWORD sécurisés
- [ ] Vérifier que JWT_SECRET est dans les variables d'env
- [ ] Activer le profil `prod`: `SPRING_PROFILES_ACTIVE=prod`
- [ ] Désactiver Swagger: `springdoc.swagger-ui.enabled=false`
- [ ] Éviter les logs SQL: `spring.jpa.show-sql=false`
- [ ] DDL validation seulement: `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Vérifier HTTPS forcé
- [ ] Vérifier CORS configuré correctement
- [ ] Vérifier Rate Limiting activé
- [ ] Logs centralisés (ELK, CloudWatch, etc.)

### En Développement Local

- [ ] Copier `.env.example` vers `.env`
- [ ] Remplir les valeurs de test
- [ ] Importer le profil `dev`
- [ ] Vérifier que `.env` est ignoré par Git

---

## Outils et Ressources

### Génération de Secrets

- 🔐 [OpenSSL](https://www.openssl.org/) - Outil CLI standard
- 🔐 [UUID Generator](https://www.uuidgenerator.net/) - En ligne
- 🔐 [Random.org](https://www.random.org/) - Nombres aléatoires

### Gestion des Secrets en Production

- 🏗️ **AWS Secrets Manager** - Pour AWS
- 🏗️ **Azure Key Vault** - Pour Azure
- 🏗️ **HashiCorp Vault** - Universel, on-premise
- 🏗️ **Docker Secrets** - Pour Docker/Kubernetes
- 🏗️ **Heroku Config Vars** - Pour Heroku

### Validation de Sécurité

- 🔍 **OWASP Top 10** - Checklist sécurité web
- 🔍 **Spring Security** - Documentation
- 🔍 **JWT Best Practices** - https://tools.ietf.org/html/rfc8725

---

## Troubleshooting

### ❌ "jwt.secret not configured"

**Cause:** Variable d'environnement JWT_SECRET manquante

**Solution:**
```bash
# Vérifier que la variable est définie
echo $JWT_SECRET

# Définir la variable
export JWT_SECRET="votre_clé_here"

# Redémarrer l'application
mvn spring-boot:run
```

### ❌ "Property not found"

**Cause:** Spring profile non activé

**Solution:**
```bash
# Vérifier le profil
echo $SPRING_PROFILES_ACTIVE

# Activer le profil dev
export SPRING_PROFILES_ACTIVE=dev
```

### ✅ Vérifier les Configuration

```bash
# Afficher toutes les properties chargées
curl http://localhost:8080/actuator/env | jq

# Vérifier que le JWT secret est chargé
curl http://localhost:8080/actuator/env | jq '.propertySources[] | select(.name | contains("jwt"))'
```

---

## Ressources Additionnelles

- 📖 [Spring Security Documentation](https://spring.io/projects/spring-security)
- 📖 [12 Factor App - Config](https://12factor.net/config)
- 📖 [OWASP Cheat Sheet](https://cheatsheetseries.owasp.org/)
- 📖 [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Dernière mise à jour:** 11/03/2026
