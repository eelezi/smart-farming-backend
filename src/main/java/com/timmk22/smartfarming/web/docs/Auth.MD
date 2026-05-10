# Auth API Documentation

Base URL: `/api/auth`

All endpoints under this controller handle user registration, login, and retrieving the currently authenticated user. Authentication is implemented using **JWT (JSON Web Token)** via a `Bearer` token passed in the `Authorization` header.

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

JWT-related errors are handled separately and return `401 Unauthorized`:

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
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and receive a JWT token | No |
| GET | `/api/auth/me` | Get the currently authenticated user | Yes |

---

## POST `/api/auth/register`

Registers a new user account.

### Request

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Jane Farmer",
  "email": "jane@example.com",
  "password": "securePass123"
}
```

**Field Validation:**

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| name | String | Yes | Max 100 characters |
| email | String | Yes | Valid email format, max 120 characters |
| password | String | Yes | Min 6, max 255 characters |

### Response

**Status:** `200 OK`

```json
{
  "userId": 1,
  "name": "Jane Farmer",
  "email": "jane@example.com"
}
```

### Error Responses

**Status:** `400 Bad Request` — Email already registered

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email already in use.",
  "path": "/api/auth/register"
}
```

---

## POST `/api/auth/login`

Authenticates a user and returns a JWT token.

### Request

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "jane@example.com",
  "password": "securePass123"
}
```

**Field Validation:**

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| email | String | Yes | Valid email format |
| password | String | Yes | Non-blank |

### Response

**Status:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "name": "Jane Farmer",
  "email": "jane@example.com"
}
```

**Token Usage:**

Include the token in the `Authorization` header for all protected endpoints:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Error Responses

**Status:** `400 Bad Request` — Invalid credentials

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email or password.",
  "path": "/api/auth/login"
}
```

> **Note:** The error message is intentionally generic for both wrong email and wrong password to prevent user enumeration attacks.

---

## GET `/api/auth/me`

Returns the profile of the currently authenticated user.

### Request

**Headers:**
```
Authorization: Bearer <token>
```

### Response

**Status:** `200 OK`

```json
{
  "userId": 1,
  "name": "Jane Farmer",
  "email": "jane@example.com"
}
```

### Error Responses

**Status:** `400 Bad Request` — User not found

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "User not found.",
  "path": "/api/auth/me"
}
```

**Status:** `401 Unauthorized` — Missing or invalid token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token is invalid.",
  "path": "/api/auth/me"
}
```

**Status:** `401 Unauthorized` — Expired token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token has expired.",
  "path": "/api/auth/me"
}
```

**Status:** `401 Unauthorized` — Invalid signature

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The token's signature is invalid.",
  "path": "/api/auth/me"
}
```

---

Tokens are validated on every protected request via the `JwtFilter`. If a token is expired, has an invalid signature, or is otherwise malformed, the server returns a `401 Unauthorized` response with a descriptive error message.