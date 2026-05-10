# Crop API Documentation

Base URL: `/api/crops`

This controller exposes crop reference data used to populate dropdowns in the frontend. It also provides plant disease detection functionality and PDF report generation.

---

## Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/crops` | Get all crops as id/name pairs | No             |
| POST | `/api/crops/diagnosis` | Analyze plant image for disease detection | Yes           |
| GET | `/api/crops/{cropId}/report` | Generate a PDF report for a specific crop | Yes           |

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

**Status:** `401 Unauthorized` — Invalid token

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

---

## GET `/api/crops/{cropId}/report`

Generates and downloads a PDF report for the authenticated user's planting data for the specified crop.

> **Note:** Only planting records belonging to the authenticated user are included in the report. If the user has no planting records for the given crop, a `404` is returned.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| cropId | Long | Yes | The ID of the crop to generate the report for |

### Response

**Status:** `200 OK`

Returns the PDF file as a binary download.

**Headers:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="crop-{cropName}-report-{date}.pdf"
```

**Body:** Binary PDF content.

### Error Responses

**Status:** `401 Unauthorized` — Invalid token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token is invalid.",
  "path": "/api/crops/{cropId}/report"
}
```

**Status:** `404 Not Found` — No planting records found for this user and crop

**Status:** `500 Internal Server Error` — Unexpected error during report generation
