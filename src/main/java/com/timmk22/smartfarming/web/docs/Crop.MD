# Crop API Documentation

Base URL: `/api/crops`

This controller exposes crop reference data used to populate dropdowns in the frontend. It also provides plant disease detection functionality.

---

## Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/crops` | Get all crops as id/name pairs | Yes |
| POST | `/api/crops/diagnosis` | Analyze plant image for disease detection | Yes |

---

## GET `/api/crops`

Returns a list of all available crops. Intended for use in frontend dropdowns and select inputs.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

### Response

**Status:** `200 OK`

```json
[
  { "id": 1, "name": "Wheat" },
  { "id": 2, "name": "Corn" },
  { "id": 3, "name": "Sunflower" }
]
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier of the crop |
| name | String | Display name of the crop |

### Error Responses

**Status:** `401 Unauthorized` — Missing or invalid token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token is invalid.",
  "path": "/api/crops"
}
```

**Status:** `401 Unauthorized` — Expired token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token has expired.",
  "path": "/api/crops"
}
```

---

## POST `/api/crops/diagnosis`

Analyzes a plant image to detect diseases. Uses AI-powered plant pathology analysis.

### Request

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Body:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| image | File | Yes | Plant image file (JPEG, PNG, etc.) |

### Response

**Status:** `200 OK`

```json
{
  "status": "DISEASE_FOUND",
  "analysis": "The plant shows signs of powdery mildew infection on the leaves. Recommend immediate treatment with appropriate fungicide."
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| status | String | One of: DISEASE_FOUND, DISEASE_NOT_FOUND, UNRECOGNIZABLE |
| analysis | String | Detailed AI-generated analysis of the plant condition |

### Error Responses

**Status:** `400 Bad Request` — Missing or empty image

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Image is required",
  "path": "/api/crops/diagnosis"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token is invalid.",
  "path": "/api/crops/diagnosis"
}
```

**Status:** `503 Service Unavailable` — AI service error

```json
{
  "status": 503,
  "error": "Service Unavailable",
  "message": "Failed to analyze plant image due to AI service error",
  "path": "/api/crops/diagnosis"
}
```
