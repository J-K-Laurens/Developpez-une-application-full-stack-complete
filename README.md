# 🚀 MDD API - Démarrage Windows (Étape par Étape)

Application full-stack: **Angular** (frontend) + **Spring Boot** (backend) avec authentification JWT et logout sécurisé.

---

## 📋 Prérequis Windows

- ✅ **Java 11+** - Vérifier: `java -version`
- ✅ **Node.js 16+** - Vérifier: `node -v`
- ✅ **MySQL 8.0+** - Vérifier: `mysql -u root -p` puis `exit`
- ✅ **Docker** (pour Redis) - [Télécharger ici](https://www.docker.com/products/docker-desktop/)
- ✅ **Maven** - Inclus dans le repo (`mvnw.cmd`)

---

## 🚀 Lancer l'Application (5 étapes)

### 1️⃣ Installer Docker (une fois)

1. 📥 Télécharger: https://www.docker.com/products/docker-desktop/
2. ✅ Installer et redémarrer Windows
3. ✅ Vérifier: Ouvrir **PowerShell** et taper:

```powershell
docker --version
# Réponse attendue: Docker version 24.0.0 (ou plus)
```

---

### 2️⃣ Démarrer Redis avec Docker

Ouvrir **PowerShell** (n'importe où) et copier-coller:

```powershell
docker run -d -p 6379:6379 --name redis redis:latest
```

✅ Réponse: Un ID (exemple: `abc123def456`)

**Vérifier que ça marche:**

```powershell
docker exec redis redis-cli ping
# Réponse attendue: PONG
```

Si vous relancez le PC, relancer cette commande. C'est tout.

---

### 3️⃣ Démarrer MySQL

Si MySQL n'est pas déjà en cours d'exécution:

```powershell
net start MySQL80
# Réponse: The MySQL80 service is starting...
```

**Vérifier:**

```powershell
mysql -u root -p
# Taper le mot de passe, puis exit
```

---

### 4️⃣ Configurer l'Application

Ouvrir l'**Explorateur Windows**, aller dans `back/`:

**Créer un fichier `.env`:**

1. 📂 Dossier `back/`
2. 📋 Clic droit → Nouveau → Document texte
3. ✏️ Renommer en `.env` (bien: `.env` / mal: `fichier.txt.env`)
4. ✏️ Ouvrir avec Notepad et coller:

```
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2
```

5. 💾 Sauvegarder (Ctrl+S)

**Pourquoi `.env`?** C'est un fichier de configuration privé (pas commité en Git).

---

### 5️⃣ Lancer le Backend

Ouvrir **PowerShell** dans le dossier `back/`:

```powershell
# Vérifier que vous êtes dans le bon dossier
cd E:\_Projets\Projet_6\Developpez-une-application-full-stack-complete\back

# Démarrer le backend
$env:SPRING_PROFILES_ACTIVE = "dev"
mvn spring-boot:run
```

⏳ **Attendre 30-60 secondes...**

✅ Quand vous voyez: `Started MddApiApplication`

Le backend est prêt! 🎉

---

### 6️⃣ Lancer le Frontend

Ouvrir un **nouveau PowerShell** dans le dossier `front/`:

```powershell
cd E:\_Projets\Projet_6\Developpez-une-application-full-stack-complete\front

npm install
npm start
```

⏳ **Attendre 30 secondes...**

✅ Le navigateur ouvre automatiquement: http://localhost:4200

---

## ✅ C'est Prêt!

| Service | URL |
|---------|-----|
| **Frontend** | http://localhost:4200 |
| **Backend** | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **Redis** | localhost:6379 (interne) |

---

## 🧪 Tester le Logout

1. 🌐 Ouvrir http://localhost:4200
2. 🔑 **Login** avec `admin@meddit.fr` / `Mdp1234!`
3. 🚪 **Logout** (bouton en haut à droite)
4. ✅ Vérifier que vous êtes déconnecté

Le token JWT est automatiquement révoqué dans Redis ✅

---

## 🛑 Arrêter l'Application

```powershell
# Terminal 1 (Backend): Ctrl+C

# Terminal 2 (Frontend): Ctrl+C

# Arrêter Redis
docker stop redis

# Arrêter MySQL
net stop MySQL80
```

---

## 🐛 Troubleshooting Windows

### ❌ "mvn: command not found"

Utiliser `mvnw` à la place:

```powershell
# Au lieu de: mvn spring-boot:run
# Taper:
.\mvnw spring-boot:run
```

---

### ❌ "npm not found"

Node.js n'est pas installé:
- 📥 Télécharger: https://nodejs.org/
- ✅ Installer
- 🔄 Redémarrer PowerShell
- Vérifier: `node -v`

---

### ❌ "Can't connect to Redis"

```powershell
# Vérifier que Redis tourne
docker ps | findstr redis

# Si rien → Relancer Redis
docker run -d -p 6379:6379 --name redis redis:latest

# Si erreur "name already in use":
docker rm redis
docker run -d -p 6379:6379 --name redis redis:latest
```

---

### ❌ "Can't connect to MySQL"

```powershell
# Vérifier que MySQL tourne
net start MySQL80

# Vérifier la connexion
mysql -u root -p

# Si erreur de mot de passe, vérifier .env
```

---

### ❌ "Port 3306/6379 already in use"

Quelque chose utilise déjà ce port:

```powershell
# Trouver le processus
netstat -ano | findstr :6379

# Tuer le processus (ajuster PID)
taskkill /PID <numero> /F

# Relancer
docker run -d -p 6379:6379 --name redis redis:latest
```

---

### ❌ "npm install very slow or fails"

```powershell
cd front
npm cache clean --force
npm install
```

---

## 📂 Structure du Projet

```
├── back/                          # Backend Spring Boot
│   ├── src/main/java/
│   │   ├── controller/            # REST API
│   │   ├── service/              # Logique métier
│   │   ├── model/                # Entités JPA
│   │   ├── repository/           # Accès données
│   │   ├── dto/                  # Objets transfert
│   │   ├── security/             # JWT, authentification
│   │   └── configuration/        # Config Spring
│   ├── .env.example             # Copier en .env
│   ├── pom.xml
│   └── mvnw.cmd
│
├── front/                         # Frontend Angular 14
│   ├── src/
│   │   ├── app/
│   │   │   ├── features/        # Auth, Articles, Topics
│   │   │   ├── services/        # API calls
│   │   │   └── guards/          # Route guards
│   │   └── assets/
│   ├── package.json
│   └── angular.json
│
└── SECURITY.md                     # Guide sécurité & tokens
```

---

## 🔐 Sécurité

### Secrets Management

- ✅ JWT Secret en variable d'env (`.env`)
- ✅ DB password en variable d'env
- ✅ `.env` dans `.gitignore` (jamais commité)
- ✅ Production: variables d'env du serveur

**Ne jamais committer `.env` ou clés privées en Git!**

### Profils d'Application

- **dev** (`application-dev.properties`): Logs DEBUG, DDL update
- **prod** (`application-prod.properties`): Logs WARNING, DDL validate

Lancer en dev:
```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
```

---

## 🚀 Fonctionnalités

### Backend
- ✅ **Authentification JWT** - Login/Register
- ✅ **Token Blacklist** - Logout sécurisé (Redis)
- ✅ **Articles** - Créer, lire, commenter
- ✅ **Topics** - Catégories d'articles
- ✅ **Abonnements** - S'abonner aux topics
- ✅ **Swagger UI** - Documentation API interactive

### Frontend
- ✅ **Pages** - Home, Articles, Topics, Profile
- ✅ **Authentification** - Login/Register/Logout
- ✅ **Souscription** - S'abonner aux topics
- ✅ **Design Responsive** - Angular Material

---

## ✅ Checklist de Démarrage

- [ ] Docker Desktop installé et lancé
- [ ] `docker run -d -p 6379:6379 --name redis redis:latest` ✅
- [ ] `docker exec redis redis-cli ping` → `PONG`
- [ ] MySQL en cours d'exécution (`net start MySQL80`)
- [ ] `.env` créé dans `back/`
- [ ] `mvn spring-boot:run` → "Started MddApiApplication"
- [ ] `npm start` dans `front/` → http://localhost:4200
- [ ] Login/Logout fonctionnent

Vous êtes prêt! 🎉

---

## 🆘 Besoin d'Aide?

| Problème | Solution |
|----------|----------|
| Redis ne démarre | Voir "Troubleshooting" ci-dessus |
| Backend échoue | Vérifier MySQL + Redis en cours d'exécution |
| Frontend ne charge | Vérifier `npm start` dans le dossier `front/` |
| Port en utilisation | Voir "Port already in use" ci-dessus |

---

**Status**: ✅ Prêt à lancer!
**Durée**: ~5-10 minutes
**Difficultés**: Très facile (étapes simples)

Lancez-vous! 🚀
