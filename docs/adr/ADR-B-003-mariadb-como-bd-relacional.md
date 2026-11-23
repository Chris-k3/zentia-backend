# ADR-B-003: MariaDB como BD Relacional

- **Estado:** Superseded (Reemplazado por [ADR-B-006](ADR-B-006-migracion-a-postgresql.md))
- **Fecha:** 20/08/2026

## Contexto

Se requería integridad referencial entre usuarios, perfiles, categorías y transacciones, descartando bases NoSQL como MongoDB.

## Decisión

Se había elegido inicialmente MariaDB como motor SQL principal.

## Alternativas descartadas

- MongoDB (sin relaciones estrictas)
- SQLite (baja concurrencia)

## Evolución y reemplazo

Decisión reemplazada durante el Sprint 1 de implementación por [ADR-B-006](ADR-B-006-migracion-a-postgresql.md) (PostgreSQL) para aprovechar mejores integraciones cloud PaaS y dialecto PostgreSQL en Hibernate.
