# ADR-FB-005: Google OAuth 2.0 para Autenticación

- **Estado:** Aceptado
- **Fecha:** 20/08/2026
- **Responsable:** Uriel Ramírez

## Contexto

Los jóvenes usuarios requieren registro e inicio de sesión rápido sin fricción, y el equipo debe evitar el riesgo de almacenar contraseñas propias.

## Decisión

Delegar la autenticación a Google OAuth 2.0 y emitir un JWT interno en Spring Boot que proteja los endpoints y valide propiedad del recurso.

## Alternativas descartadas

- Contraseñas propias en BD
- Auth0/Clerk
- Confiar en `userId` del frontend

## Consecuencias

**Positivas (+):**
- Cero contraseñas almacenadas localmente; prevención de ataques IDOR.

**Negativas (-):**
- Dependencia del servicio de Google para el login y validación de expiración de tokens (`401 Unauthorized`).
