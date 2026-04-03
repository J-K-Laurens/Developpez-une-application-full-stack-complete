# 📊 Architecture du Projet

## Communication Client-Serveur

### Flux de Communication (Séquence d'appels)

```mermaid
sequenceDiagram
    participant User as 👤 Utilisateur
    participant Browser as 🌐 Navigateur (Angular)
    participant JwtInt as JwtInterceptor
    participant ErrorInt as ErrorInterceptor
    participant Backend as 🔗 Backend (Spring)
    participant DB as 🗄️ Base de Données

    User->>Browser: 1️⃣ Login (email/password)
    Browser->>Backend: POST /api/auth/login
    Backend->>DB: Vérifier credentials
    Backend->>Browser: token + refreshToken
    Browser->>Browser: Stocker en localStorage

    User->>Browser: 2️⃣ Demande articles
    Browser->>JwtInt: GET /api/articles
    JwtInt->>JwtInt: Récupérer token du localStorage
    JwtInt->>JwtInt: Ajouter Authorization Header
    JwtInt->>Backend: GET /api/articles<br/>[Authorization: Bearer token]
    Backend->>Backend: Valider JWT
    Backend->>DB: Récupérer articles
    Backend->>Browser: 200 + articles
    Browser->>User: Afficher articles

    User->>Browser: 3️⃣ Faire une action<br/>(créer article, etc)
    Browser->>JwtInt: POST /api/articles
    JwtInt->>Backend: avec token valide
    Backend->>DB: Persister
    Backend->>Browser: 201 + article créé
    
    Note over Browser,Backend: 4️⃣ Cas: Token expiré
    User->>Browser: Après 24h
    Browser->>JwtInt: GET /api/articles
    JwtInt->>Backend: [Authorization: Bearer expired_token]
    Backend->>Browser: ❌ 401 Unauthorized
    JwtInt->>JwtInt: Détecter 401
    JwtInt->>Backend: POST /api/auth/refresh<br/>{refreshToken}
    Backend->>Backend: Valider refreshToken
    Backend->>Browser: ✅ Nouveau token
    JwtInt->>JwtInt: Stocker nouveau token
    JwtInt->>Browser: Réessayer requête originale
    Browser->>User: Succès transparent
```

### Architecture Requête-Réponse (Backend)

```mermaid
graph LR
    subgraph Backend["🔗 BACKEND"]
        Router["🌍 Router<br/>@GetMapping<br/>@PostMapping"]
        JwtFilter["🔐 JwtFilter"]
        Controllers["🎮 Controllers"]
        BusinessLogic["💼 Services"]
        Repositories["📚 Repositories"]
        Database["🗄️ PostgreSQL"]
        Response["⬅️ RESPONSE<br/>Status: 200/401<br/>Body: JSON"]
    end
    
    Router -->|routage| JwtFilter -->|authentification| Controllers -->|logique métier| BusinessLogic -->|persistance| Repositories -->|accès données| Database
    Database -->|récupération| Repositories -->|traitement| BusinessLogic -->|formatage| Controllers -->|réponse| Response
```

### Architecture Requête-Réponse (Version Diapo - Horizontal - Backend uniquement)

```mermaid
graph LR
    D[📡 HTTP Request<br/>Headers: Authorization<br/>Body: JSON] -->|routage| E[🌍 Router<br/>@GetMapping<br/>@PostMapping<br/>@DeleteMapping]
    E -->|authentification| F[🔐 JwtFilter<br/>Valide token]
    F -->|validation| G[🎮 Controllers<br/>ArticleController<br/>AuthController<br/>TopicController]
    G -->|logique métier| H[💼 Business Logic<br/>ArticleService<br/>SubscriptionService<br/>TopicService]
    H -->|accès données| I[📚 Repositories<br/>ArticleRepository<br/>UserRepository<br/>TopicRepository]
    I -->|persistance| J[🗄️ Base de Données<br/>PostgreSQL]
    
    J -->|récupération| I
    I -->|traitement| H
    H -->|formatage| G
    G -->|réponse| K[📡 HTTP Response<br/>Status: 200/401<br/>Body: JSON]
    
    style D fill:#e8f5e8
    style E fill:#fff8e1
    style F fill:#fce4ec
    style G fill:#f1f8e9
    style H fill:#e0f2f1
    style I fill:#f3e5f5
    style J fill:#ffebee
    style K fill:#e8f5e8
```

---

## 📡 Points clés de votre architecture

| Élément | Rôle |
|---------|------|
| **JwtInterceptor** | Ajoute le Bearer token à chaque requête |
| **ErrorInterceptor** | Gère les 401, déclenche refresh automatique |
| **AuthGuard** | Protège routes privées (**/articles**, **/profile**) |
| **UnauthGuard** | Protège routes publiques (**/login**, **/register**) |
| **JwtFilter (Spring)** | Valide token avant de router vers les contrôleurs |
