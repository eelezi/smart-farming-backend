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

## Generate PDF report
Endpoint: `POST /generate-pdf`

The endpoint accepts farm statistics in JSON and returns a printable PDF file (`application/pdf`) with an AI-generated health summary.

Example request body:
```json
{
  "farmName": "North Valley Farm",
  "reportDate": "2026-03-28",
  "preparedBy": "Operations Team",
  "stats": [
	{
	  "fieldName": "Field-01",
	  "cropName": "Wheat",
	  "acreage": 18.5,
	  "yieldPerAcre": 4.8,
	  "soilMoisturePct": 52.1,
	  "pestIncidents": 2,
	  "diseaseRiskScore": 35.5
	}
  ]
}
```

Example curl command:
```bash
curl -X POST http://localhost:8080/generate-pdf \
  -H "Content-Type: application/json" \
  -d @request.json \
  --output farming-report.pdf
```

## Run tests
```bash
mvn test
```

