# ADR-B-002: Spring Boot para API REST

- **Estado:** Aceptado
- **Fecha:** 20/08/2026
- **Responsables:** Uriel, Chris y Daniel

## Contexto

El sistema requiere centralizar reglas de negocio, validación de datos, autenticación, autorización, cálculos financieros y persistencia fuera del navegador.

## Decisión

Utilizar Java 21 con Spring Boot 4.1.1 organizado por capas (Clean Architecture).

## Alternativas descartadas

- Node.js/Express
- Backend integrado en Next.js
- Spring MVC sin Boot

## Consecuencias

**Positivas (+):**
- Tipado fuerte, ecosistema robusto (Spring Security, Spring Data JPA) y separación total del cliente.

**Negativas (-):**
- Curva de aprendizaje de Java/Spring y configuración de filtros de seguridad/CORS.
