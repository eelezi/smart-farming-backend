# Smart Farming Backend (Spring Boot + PostgreSQL)

## Branching Strategy

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready code. Protected – changes must go through a pull request from `dev`. |
| `dev`  | Active development branch. All feature branches should be created from and merged back into `dev`. |

Workflow: `feature/*` → `dev` → PR → `main`

## Requirements
- Java 17+
- Maven 3.9+
- Docker (for PostgreSQL)

## Start PostgreSQL
```bash
docker compose up -d
```

By default, Docker Compose auto-start is disabled.
Use the `local` profile to let Spring Boot auto-start `docker-compose.yml` services.

## Run application
```bash
mvn spring-boot:run
```

Run with local profile (auto-start Docker Compose):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

PowerShell equivalent:
```powershell
Set-Location "D:\Projects\SmartFarmingApp\smart-farming-backend"
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

