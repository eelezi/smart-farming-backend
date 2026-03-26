# Smart Farming Backend (Spring Boot + PostgreSQL)

## Requirements
- Java 17+
- Maven 3.9+
- Docker (for PostgreSQL)

## Start PostgreSQL
```bash
docker compose up -d
```

## Run application
```bash
mvn spring-boot:run
```

## Default API
- `GET /api/health`
- `POST /api/recommendations`

## Example request
```json
{
  "crop": "Tomato",
  "symptoms": "Yellow spots on leaves",
  "temperatureC": 23.5,
  "humidityPercent": 68.0
}
```
