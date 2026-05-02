# Soil Type API Documentation

Base URL: `/api/soil-types`

This controller exposes soil type reference data used to populate dropdowns in the frontend (e.g. the Add Entry form). It is a read-only endpoint — soil types are managed at the database level. Results are returned sorted alphabetically by name.

---

## Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/soil-types` | Get all soil types as id/name pairs | Yes |

---

## GET `/api/soil-types`

Returns a list of all available soil types, sorted alphabetically by name. Intended for use in frontend dropdowns and select inputs.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

### Response

**Status:** `200 OK`

```json
[
  { "id": 1, "name": "Clay" },
  { "id": 2, "name": "Loam" },
  { "id": 3, "name": "Sandy" }
]
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier of the soil type |
| name | String | Display name of the soil type (sorted A-Z) |

### Error Responses

**Status:** `401 Unauthorized` — Missing or invalid token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token is invalid.",
  "path": "/api/soil-types"
}
```

**Status:** `401 Unauthorized` — Expired token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token has expired.",
  "path": "/api/soil-types"
}
```