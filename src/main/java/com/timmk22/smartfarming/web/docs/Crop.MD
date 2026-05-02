# Crop API Documentation

Base URL: `/api/crops`

This controller exposes crop reference data used to populate dropdowns in the frontend (e.g. the Add Entry form). It is a read-only endpoint — crops are managed at the database level.

---

## Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/crops` | Get all crops as id/name pairs | Yes |

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