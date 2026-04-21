# Authentication and Authorization Contract

This document defines the shared contract for authentication across all feature branches.

## 1. Token Format

- Header: `Authorization: Bearer <jwt-token>`
- Algorithm: `HS256`
- Issuer: auth module (`/api/auth/*`)
- Expiration: `jwt.expiration` (default 86400000 ms)

## 2. JWT Claims

- `sub`: user identity used by auth service (email or phone)
- `role`: role string (`STUDENT`, `ADMIN`)
- `user_id`: persistent internal user ID (`Long`)
- `iat`: issued at
- `exp`: expiration

## 3. Auth API Endpoints

### Public endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/oauth2/google` (returns Google authorization URL)
- `GET /oauth2/authorization/google` (OAuth2 authorization flow entry)
- `GET /login/oauth2/code/google` (OAuth2 callback managed by Spring Security)

### Authenticated endpoints

- `GET /api/auth/profile`
- `PUT /api/auth/profile`

## 4. Response and Error Conventions

- `401 Unauthorized`: missing/invalid token or invalid credentials
- `403 Forbidden`: authenticated but access not allowed for resource
- `404 Not Found`: requested identity/resource not found
- `409 Conflict`: duplicate email/phone during registration

Auth success payload (`AuthResponse`):

- `userId`
- `token`
- `message`
- `username`
- `role`

## 5. Module Integration Notes

These notes are inferred from milestone ownership in README. Remote feature branch internals were not inspected directly in this workspace.

- `feat/achievement`: use `user_id` to bind achievement progress to owner; enforce self-access for students.
- `feat/learning-and-quiz`: use `user_id` for submission ownership and result retrieval.
- `feat/ClanLeague`: use `user_id` for clan membership and score attribution.
- `feat/Discussion-Forum`: use `user_id` for post/comment ownership and moderation checks.

## 6. Runtime Configuration

Set these env vars in deployment environments:

- `JWT_SECRET`
- `JWT_EXPIRATION` (optional)
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

For local development, fallback placeholders are provided in `application.properties` and must not be used in production.
