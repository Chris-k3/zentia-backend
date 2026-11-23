# ADR-B-006: Migración a PostgreSQL como BD Relacional Definitiva

- **Estado:** Aceptado (Reemplaza a [ADR-B-003](ADR-B-003-mariadb-como-bd-relacional.md))
- **Fecha:** 01/09/2026
- **Responsables:** Chris y Daniel

## Contexto

Al configurar el despliegue contenedorizado y los entornos PaaS en la nube junto con Spring Boot 4.1.1, PostgreSQL ofreció mejor soporte nativo, herramientas (`pgAdmin`) y manejo de `NUMERIC(19,2)`.

## Decisión

Adoptar PostgreSQL (`zentia_db`) con `org.postgresql.Driver` y `PostgreSQLDialect` como motor relacional definitivo.

## Alternativas descartadas

- Mantener MariaDB local
- Usar H2 en producción

## Consecuencias

**Positivas (+):**
- Soporte ACID de nivel empresarial, compatibilidad directa con proveedores cloud e índices compuestos rápidos.

**Negativas (-):**
- Requiere instalar y configurar PostgreSQL 15+ en el entorno local de cada desarrollador.
