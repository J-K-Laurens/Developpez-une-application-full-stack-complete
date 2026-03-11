# MDD API - Application Full-Stack

Une application full-stack complète avec Angular (frontend) et Spring Boot (backend).

## 🚀 Démarrage Rapide

### Prérequis
- **Node.js** 16+ (frontend)
- **Java 11+** (backend)
- **MySQL 8.0+** (base de données)
- **Maven 3.6+** (build backend)

### 1️⃣ Configuration de la Base de Données

Créez une base de données MySQL :
```sql
CREATE DATABASE mdd;
```

### 2️⃣ Configuration des Secrets

#### Option A : Variables d'Environnement (Recommandé ✅)

**PowerShell:**
```powershell
# Generate a secure JWT secret (minimum 32 characters)
# Using OpenSSL: openssl rand -hex 32
# Or online: https://www.uuidgenerator.net/

$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_secure_jwt_secret_at_least_32_characters"
```

**Bash/Linux:**
```bash
export DB_USERNAME="root"
export DB_PASSWORD="your_mysql_password"
export JWT_SECRET="your_secure_jwt_secret_at_least_32_characters"
```

#### Option B : Fichier .env (Développement Local)

1. Copier le fichier d'exemple:
```bash
cd back
cp .env.example .env
```

2. Éditer `back/.env` avec vos valeurs:
```
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_secure_jwt_secret_at_least_32_characters
```

⚠️ **IMPORTANT**: 
- **JAMAIS** committer le fichier `.env` en version control
- Chaque développeur doit créer son propre `.env`
- Le `.env` est listé dans `.gitignore`

#### Générer une Clé JWT Sécurisée

```bash
# Avec OpenSSL (recommandé)
openssl rand -hex 32

# Ou utiliser un générateur UUID
# https://www.uuidgenerator.net/
```

Exemple de clé sécurisée (32+ caractères):
```
a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2
```

### 3️⃣ Lancer le Backend

```bash
cd back

# Development (avec clé JWT de dev)
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run

# Production (REQUIRES environment variables)
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

✅ Le serveur démarre sur `http://localhost:8080`

### 4️⃣ Lancer le Frontend

```bash
cd front
npm install
ng serve
```

✅ L'app démarre sur `http://localhost:4200`

---

## 🔐 Sécurité des Secrets

### Development vs Production

| Aspect | Development | Production |
|--------|-------------|-----------|
| JWT Secret | Valeur par défaut de test | Variable d'environnement OBLIGATOIRE |
| DB Password | Peut être simple | Variable d'environnement sécurisée |
| DDL Auto | `update` (crée tables) | `validate` (vérifie seulement) |
| Show SQL | `true` | `false` |
| Swagger | Activé | Désactivé |
| Logging | DEBUG | INFO/WARN |

### Fichiers de Configuration

- `application.properties` - Configuration commune
- `application-dev.properties` - Configuration développement (git)
- `application-prod.properties` - Configuration production (git)

Pour activer un profil:
```bash
export SPRING_PROFILES_ACTIVE=dev   # ou prod
```

### Bonnes Pratiques 🛡️

1. ✅ **JAMAIS** de secrets en dur dans le code
2. ✅ **JAMAIS** de secrets dans Git
3. ✅ Utiliser des **variables d'environnement** en production
4. ✅ Générer une **clé JWT unique** pour chaque environnement
5. ✅ Utiliser `application-prod.properties` sans valeurs par défaut sensibles
6. ✅ Ajouter `.env` à `.gitignore`

---

## 📂 Structure du Projet

```
├── back/                    # Backend Spring Boot
│   ├── src/main/java/
│   │   ├── controller/      # REST API
│   │   ├── service/         # Logique métier
│   │   ├── model/           # Entités JPA
│   │   ├── repository/       # Accès données
│   │   ├── dto/             # Objets de transfert
│   │   ├── exception/       # Gestion erreurs
│   │   └── security/        # JWT, authentification
│   └── pom.xml              # Dépendances Maven
│
├── front/                   # Frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── features/    # Features (articles, topics, etc)
│   │   │   ├── features/auth/   # Authentification
│   │   │   ├── services/    # Services API
│   │   │   ├── interceptors/ # JWT intercept
│   │   │   └── guards/      # Route guards
│   │   └── assets/
│   └── package.json         # Dépendances npm
```

---

## 🔧 Fonctionnalités Principales

### Backend
- ✅ **Authentification JWT** - Login/Register avec tokens
- ✅ **Gestion Articles** - Créer, lire, filtrer
- ✅ **Gestion Topics** - Catégories d'articles
- ✅ **Abonnements** - Users s'abonnent à des topics
- ✅ **Commentaires** - Commentaires sur articles
- ✅ **Documentation OpenAPI/Swagger** - Swagger UI sur `/swagger-ui.html`

### Frontend
- ✅ **Pages** - Home, Articles, Topics, Profile
- ✅ **Authentification** - Login/Register
- ✅ **Souscription** - S'abonner/désabonner à des topics
- ✅ **Responsive** - Angular Material design

---

## 📡 API - Endpoints Principaux

### Authentification
```bash
POST   /api/auth/register        # Créer un compte
POST   /api/auth/login          # Se connecter
GET    /api/auth/me             # Info utilisateur actuel
```

### Users
```bash
GET    /api/users/{id}          # Récupérer un user
PUT    /api/users/me            # Modifier profil
```

### Articles
```bash
GET    /api/articles            # Articles abonnés (filtrés)
GET    /api/articles/{id}       # Détail d'un article
POST   /api/articles            # Créer un article
```

### Topics
```bash
GET    /api/topics              # Liste tous les topics
GET    /api/topics/{id}         # Détail d'un topic
```

### Abonnements
```bash
GET    /api/subscriptions       # Topics abonnés
POST   /api/subscriptions/{id}  # S'abonner
DELETE /api/subscriptions/{id}  # Se désabonner
```

---

## ⚠️ Gestion des Erreurs

L'API retourne des réponses d'erreur **structurées et cohérentes**.

### ✅ Exemple: Réponse Normal (200)
```bash
curl http://localhost:8080/api/users/1
```
```json
{
  "id": 1,
  "email": "user@test.com",
  "firstName": "John",
  "lastName": "Doe",
  "admin": false
}
```

### ❌ Exemple 1: Ressource Non Trouvée (404)

```bash
curl http://localhost:8080/api/users/999
```

**Réponse:**
```json
{
  "timestamp": "2026-03-11T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/users/999"
}
```

**Raison:** L'utilisateur avec l'ID 999 n'existe pas.

---

### ❌ Exemple 2: Validation Échouée (400)

```bash
curl http://localhost:8080/api/users/-5
```

**Réponse:**
```json
{
  "timestamp": "2026-03-11T10:00:00",
  "status": 400,
  "error": "Argument Validation Failed",
  "message": "The provided parameters are invalid",
  "path": "/api/users/-5",
  "validationErrors": {
    "id": "ID must be greater than 0"
  }
}
```

**Raison:** L'ID doit être positif (validation Spring).

---

### ❌ Exemple 3: Règle Métier Violée (409)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "existing@test.com",
    "name": "John",
    "password": "SecurePass123!"
  }'
```

**Réponse** (si email existe déjà):
```json
{
  "timestamp": "2026-03-11T10:00:00",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "An account already exists with this email",
  "path": "/api/auth/register",
  "errorCode": "EMAIL_ALREADY_EXISTS"
}
```

**Raison:** Un utilisateur avec cet email existe déjà.

---

### ❌ Exemple 4: Erreur d'Authentification (401)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@test.com", "password": "bad"}'
```

**Réponse:**
```json
{
  "timestamp": "2026-03-11T10:00:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Invalid credentials or expired session",
  "path": "/api/auth/login"
}
```

**Raison:** Email ou mot de passe incorrect.

---

### ❌ Exemple 5: Erreur Serveur (500)

```bash
# Erreur inattendue côté serveur
```

**Réponse:**
```json
{
  "timestamp": "2026-03-11T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An internal error occurred. Please contact support.",
  "path": "/api/articles"
}
```

**Raison:** Problem imprévu sur le serveur. Vérifiez les logs.

---

## 📊 Résumé des Codes d'Erreur

| Code | Nom | Cause | Solution |
|------|-----|-------|----------|
| **404** | Not Found | Ressource inexistante | Vérifiez l'ID |
| **400** | Bad Request | Données invalides | Vérifiez les paramètres |
| **409** | Conflict | Règle métier violée | Ex: email déjà utilisé |
| **401** | Unauthorized | Auth manquante/invalide | Connectez-vous avec JWT |
| **500** | Server Error | Erreur serveur | Vérifiez les logs |

---

## 🧪 Tester l'API

### Avec cURL
```bash
# Créer un utilisateur
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","name":"Test","password":"SecurePass123!"}'

# Récupérer tous les topics
curl http://localhost:8080/api/topics
```

### Avec Postman ou Swagger
Accédez à http://localhost:8080/swagger-ui.html pour une interface interactive.

---

## 🔐 Authentification JWT

1. **Login** → Recevez un `token`
2. **Envoyez le token** dans chaque requête :
   ```
   Authorization: Bearer YOUR_TOKEN_HERE
   ```
3. **Token valide 24h** (configurable)

**Exemple:**
```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5..."
```

---

## 📚 Documentation Complète

Pour des explications avancées sur :
- Exception patterns avancés
- Tests unitaires
- Déploiement en production
- Architecture SOLID

Voir les dossiers `docs/` ou les fichiers `.md` archivés.

---

## ⚙️ Configuration

### Backend (application.properties)

```properties
server.port=8080                        # Port serveur
spring.datasource.url=jdbc:mysql://...  # Base de données
spring.jpa.hibernate.ddl-auto=update     # Auto-créer tables
jwt.expiration-ms=86400000               # Token valide 24h
```

### Frontend (environment.ts)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'  // URL du backend
};
```

---

## 🐛 Dépannage

### Backend ne démarre pas?
```bash
# Check Java version
java -version

# Check MySQL est lancé
# Vérifiez les variables d'environnement
$env:DB_USERNAME
$env:DB_PASSWORD
```

### Frontend ne se connecte pas?
```bash
# Vérifiez que le backend démarre sur le port 8080
curl http://localhost:8080/swagger-ui.html

# Vérifiez le proxy.conf.json du frontend
```

### Base de données vide?
```sql
-- Connectez-vous à MySQL et vérifiez
USE mdd;
SHOW TABLES;
```

---

## 🎯 Prochaines Étapes

1. ✅ Clone/Fork le repo
2. ✅ Configure les variables d'environnement
3. ✅ Démarre le backend
4. ✅ Démarre le frontend
5. ✅ Test l'API sur Swagger UI
6. ✅ Modifie le code et développe!

---

## 📞 Support

Pour les erreurs, vérifiez:
1. **Logs backend** - Console Maven
2. **Logs frontend** - Console navigateur (F12)
3. **Swagger UI** - http://localhost:8080/swagger-ui.html
4. **Fichiers de config** - `application.properties`, `environment.ts`

---

**Bonne chance! 🚀**
