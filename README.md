# DevHub 🚀

A Reddit-inspired community platform built for developers — where you can create communities, share posts, vote, and discuss ideas.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.2-blue?style=flat-square&logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-326CE5?style=flat-square&logo=kubernetes)

---

## Features

- **User Auth** — Register, Login, Logout with Spring Security + BCrypt
- **Communities** — Create and browse developer communities
- **Posts** — Create posts inside communities with rich content
- **Voting** — Upvote / Downvote posts with Karma system
- **Comments** — Nested threaded comments on posts
- **Swagger UI** — API docs at `/swagger-ui.html`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.4 |
| Security | Spring Security 6 |
| ORM | Spring Data JPA + Hibernate |
| Templating | Thymeleaf |
| Database | MySQL 8.2 |
| Build | Maven |
| Containerization | Docker + Docker Compose |
| Orchestration | Kubernetes (Minikube / EKS) |
| API Docs | SpringDoc OpenAPI (Swagger) |

---

## Project Structure

```
DevHub/
├── src/
│   └── main/
│       ├── java/com/devhub/
│       │   ├── controller/        # AuthController, PostController, CommunityController
│       │   ├── entity/            # User, Post, Community, Comment, Vote
│       │   ├── repository/        # JPA Repositories
│       │   ├── service/           # Business Logic (interfaces + impl)
│       │   ├── dto/               # Data Transfer Objects
│       │   ├── config/            # SecurityConfig
│       │   ├── exception/         # GlobalExceptionHandler
│       │   └── util/              # AppConstants
│       └── resources/
│           ├── templates/         # Thymeleaf HTML templates
│           └── application.properties
├── k8s/
│   ├── deployment.yaml            # App Deployment + Service
│   ├── mysql-deployment.yaml      # MySQL Deployment + Service
│   ├── configmap.yaml             # Non-sensitive config
│   └── secret.yaml                # Sensitive credentials
├── Dockerfile                     # Multi-stage build
├── docker-compose.yml             # Local development setup
└── pom.xml
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker + Docker Compose
- MySQL 8.x (if running locally without Docker)

### Option 1 — Docker Compose (Recommended)

```bash
# Clone the repo
git clone https://github.com/kartavynirwel-code/DevHub.git
cd DevHub

# Create application.properties (see Configuration section)
# Then start everything
docker compose up --build
```

App will be available at `http://localhost:8080`

### Option 2 — Run Locally

```bash
# Start MySQL separately, then:
mvn clean package -DskipTests
java -jar target/devhub-0.0.1-SNAPSHOT.jar
```

---

## Configuration

Create `src/main/resources/application.properties`:

```properties
# Server
server.port=8080
spring.application.name=DevHub

# DataSource
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:devhub_db}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USER:devhub_user}
spring.datasource.password=${DB_PASSWORD:devhub_pass}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Thymeleaf
spring.thymeleaf.cache=false
```

> **Note:** `application.properties` is in `.gitignore` — never commit secrets to Git.

---

## Docker

### Build Image

```bash
docker build -t kartavyanirwel/devhub-app:latest .
```

### Push to Docker Hub

```bash
docker push kartavyanirwel/devhub-app:latest
```

### Docker Compose Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `db` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `devhub_db` | Database name |
| `DB_USER` | `devhub_user` | Database user |
| `DB_PASSWORD` | `devhub_pass` | Database password |

---

## Kubernetes Deployment

### Prerequisites

- Minikube or EKS cluster running
- `kubectl` configured

### Deploy

```bash
# 1. Create secrets (run once)
kubectl create secret generic devhub-secrets \
  --from-literal=DB_USER=devhub_user \
  --from-literal=DB_PASSWORD=devhub_pass \
  --from-literal=MYSQL_ROOT_PASSWORD=root_secure_password \
  --from-literal=DB_NAME=devhub_db

# 2. Apply all manifests
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/deployment.yaml

# 3. Check status
kubectl get pods
kubectl get services
```

### Access on Minikube

```bash
minikube service devhub-service --url
```

---

## API Docs

Once the app is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Database Schema

```
users           — id, username, email, password, bio, karma_points, created_at
communities     — id, name, description, created_by (FK users)
posts           — id, title, content, upvotes, downvotes, score, author_id, community_id
comments        — id, content, author_id, post_id, parent_id (nested)
votes           — id, vote_type (UPVOTE/DOWNVOTE), user_id, post_id
community_members — user_id, community_id (join table)
```

---

## CI/CD Pipeline

This project uses a full DevOps pipeline:

```
GitHub Push
    ↓
Jenkins (CI) — Build → Test → Docker Build → Push to Docker Hub
    ↓
ArgoCD (CD) — Pull from Docker Hub → Deploy to Kubernetes
```

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

This project is open source and available under the [MIT License](LICENSE).
