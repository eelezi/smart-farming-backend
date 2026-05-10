# Planting Information API Documentation

Base URL: `/entries`

All endpoints under this controller handle creating, reading, updating, and deleting planting entries. Authentication is implemented using **JWT (JSON Web Token)** via a `Bearer` token passed in the `Authorization` header.

---

## Error Response Structure

All business logic errors thrown as `IllegalArgumentException` are handled by the global exception handler and return the following structure with `400 Bad Request`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "<error message>",
  "path": "<request path>"
}
```

JWT-related errors return `401 Unauthorized`:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "<error message>",
  "path": "<request path>"
}
```

---

## Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/entries` | Get all entries for the authenticated user | Yes |
| GET | `/entries/{id}` | Get a single entry by ID | Yes |
| GET | `/api/plantings/{id}/ai-tips` | Generate AI-based cultivation tips for an entry | Yes |
| POST | `/entries` | Create a new planting entry | Yes |
| PUT | `/entries/{id}` | Update an existing planting entry | Yes |
| DELETE | `/entries/{id}` | Delete a planting entry | Yes |

---

## GET `/entries`

Returns all planting entries belonging to the authenticated user.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

### Response

**Status:** `200 OK`

```json
[
  {
    "plantingId": 1,
    "area": 2.5,
    "plantingDate": "2024-03-15",
    "userId": 1,
    "cropId": 3,
    "cropName": "Wheat",
    "soilTypeId": 2,
    "soilTypeName": "Loamy",
    "latitude": 41.0082,
    "longitude": 28.9784,
    "locationName": "North Field",
    "irrigationType": "DRIP",
    "currentStatus": "HEALTHY",
    "expectedHarvestDate": "2024-07-15",
    "notes": "Fertilized on day 10"
  }
]
```

### Error Responses

**Status:** `400 Bad Request` — Authenticated user not found

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "User not found with ID: 1",
  "path": "/entries"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

---

## GET `/entries/{id}`

Returns a single planting entry by its ID. The entry must belong to the authenticated user.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | The ID of the planting entry |

### Response

**Status:** `200 OK`

```json
{
  "plantingId": 1,
  "area": 2.5,
  "plantingDate": "2024-03-15",
  "userId": 1,
  "cropId": 3,
  "cropName": "Wheat",
  "soilTypeId": 2,
  "soilTypeName": "Loamy",
  "latitude": 41.0082,
  "longitude": 28.9784,
  "locationName": "North Field",
  "irrigationType": "DRIP",
  "currentStatus": "HEALTHY",
  "expectedHarvestDate": "2024-07-15",
  "notes": "Fertilized on day 10"
}
```

### Error Responses

**Status:** `400 Bad Request` — Entry not found or does not belong to the authenticated user

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Entry not found with ID: 1",
  "path": "/entries/1"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

---

## POST `/entries`

Creates a new planting entry for the authenticated user.

### Request

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Body:**

```json
{
  "area": 2.5,
  "plantingDate": "2024-03-15",
  "cropId": 3,
  "soilTypeId": 2,
  "latitude": 41.0082,
  "longitude": 28.9784,
  "locationName": "North Field",
  "irrigationType": "DRIP",
  "currentStatus": "HEALTHY",
  "expectedHarvestDate": "2024-07-15",
  "notes": "Fertilized on day 10"
}
```

**Field Validation:**

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| area | Double | Yes | Must be positive |
| plantingDate | LocalDate | Yes | Format: `YYYY-MM-DD` |
| cropId | Long | Yes | Must be positive, must exist |
| soilTypeId | Long | Yes | Must be positive, must exist |
| latitude | Double | No | Between -90.0 and 90.0 |
| longitude | Double | No | Between -180.0 and 180.0 |
| locationName | String | No | Max 255 characters |
| irrigationType | IrrigationType | No | Enum value |
| currentStatus | CurrentStatus | No | Enum value — defaults to `HEALTHY` |
| expectedHarvestDate | LocalDate | No | Format: `YYYY-MM-DD` |
| notes | String | No | Max 1000 characters |

### Response

**Status:** `201 Created`

```json
{
  "plantingId": 1,
  "area": 2.5,
  "plantingDate": "2024-03-15",
  "userId": 1,
  "cropId": 3,
  "cropName": "Wheat",
  "soilTypeId": 2,
  "soilTypeName": "Loamy",
  "latitude": 41.0082,
  "longitude": 28.9784,
  "locationName": "North Field",
  "irrigationType": "DRIP",
  "currentStatus": "HEALTHY",
  "expectedHarvestDate": "2024-07-15",
  "notes": "Fertilized on day 10"
}
```

### Error Responses

**Status:** `400 Bad Request` — Crop or soil type not found

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Crop not found with ID: 3",
  "path": "/entries"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

---

## PUT `/entries/{id}`

Updates an existing planting entry. The entry must belong to the authenticated user.

### Request

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | The ID of the planting entry to update |

**Body:**

```json
{
  "area": 3.0,
  "plantingDate": "2024-03-15",
  "cropId": 3,
  "soilTypeId": 2,
  "latitude": 41.0082,
  "longitude": 28.9784,
  "locationName": "North Field",
  "irrigationType": "DRIP",
  "currentStatus": "GROWING",
  "expectedHarvestDate": "2024-07-15",
  "notes": "Updated after irrigation check"
}
```

**Field Validation:**

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| area | Double | Yes | Must be positive |
| plantingDate | LocalDate | Yes | Format: `YYYY-MM-DD` |
| cropId | Long | Yes | Must be positive, must exist |
| soilTypeId | Long | Yes | Must be positive, must exist |
| latitude | Double | No | Between -90.0 and 90.0 |
| longitude | Double | No | Between -180.0 and 180.0 |
| locationName | String | No | Max 255 characters |
| irrigationType | IrrigationType | No | Enum value |
| currentStatus | CurrentStatus | Yes | Enum value |
| expectedHarvestDate | LocalDate | No | Format: `YYYY-MM-DD` |
| notes | String | No | Max 1000 characters |

### Response

**Status:** `200 OK` — Returns the updated `PlantingInformationResponse`

### Error Responses

**Status:** `404 Not Found` — Entry not found or does not belong to the authenticated user

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Entry not found with ID: 1",
  "path": "/entries/1"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

---

## DELETE `/entries/{id}`

Deletes a planting entry. The entry must belong to the authenticated user.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | The ID of the planting entry to delete |

### Response

**Status:** `204 No Content`

### Error Responses

**Status:** `404 Not Found` — Entry not found or does not belong to the authenticated user

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Entry not found with ID: 1",
  "path": "/entries/1"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

---

## GET `/api/plantings/{id}/ai-tips`

Generates AI-powered cultivation tips and recommendations for a specific planting entry using Google's Generative AI.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | The ID of the planting entry |

### Response

**Status:** `200 OK`

```json
{
  "summary": "Comprehensive cultivation advice has been generated.",
  "tips": [
    "Monitor soil moisture regularly - adjust irrigation based on current weather conditions",
    "Check for pest and disease signs daily, especially on leaves and stems",
    "Maintain consistent care routine for optimal crop development"
  ],
  "instructions": [
    "Review weather forecasts regularly and adjust irrigation schedule accordingly",
    "Apply recommended fertilizers based on crop development stage",
    "Keep the planting area clear of weeds to reduce competition for nutrients"
  ],
  "cultivationAdvice": "For Wheat grown in Loamy soil with DRIP irrigation: Focus on consistent watering, pest monitoring, and timely nutrient management. Current status shows the crop is healthy."
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| summary | String | Brief summary of cultivation recommendations |
| tips | Array[String] | List of practical tips for cultivating the crop |
| instructions | Array[String] | List of actionable step-by-step instructions |
| cultivationAdvice | String | Detailed advice specific to crop, soil type, irrigation, and current status |

### Error Responses

**Status:** `404 Not Found` — Entry not found or does not belong to the authenticated user

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Entry not found with ID: 1",
  "path": "/api/plantings/1/ai-tips"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

---

## Error Handling

All operations use the following error handling strategy:

- **AI Service Unavailability**: When the Google GenAI service is unavailable, the endpoint gracefully falls back to providing generic but useful cultivation tips based on the entry's crop type, soil type, and irrigation method.
- **Entry Not Found**: Attempting to access an entry that doesn't exist or doesn't belong to the authenticated user returns a `404 Not Found`.
- **Unauthorized Access**: Attempting to access another user's entry is treated as if the entry doesn't exist (returns `404 Not Found`).
- **Invalid Token**: JWT-related errors return `401 Unauthorized`.

---

Tokens are validated on every protected request via the `JwtFilter`. If a token is expired, has an invalid signature, or is otherwise malformed, the server returns a `401 Unauthorized` response with a descriptive error message. All entry operations are scoped to the authenticated user — attempting to access or modify another user's entry returns a `404 Not Found`.
