# Registro de Decisiones de Arquitectura (ADRs)

Este directorio reúne las decisiones tecnológicas de stack (PF #3) y las ocho decisiones arquitectónicas obligatorias (PF #4) de **Zentia**.

> **Nota de evolución arquitectónica:** En el entregable PF #2 se planteó inicialmente una base de datos no relacional, descartada en PF #3 a favor de un modelo relacional SQL ([ADR-B-003](ADR-B-003-mariadb-como-bd-relacional.md): MariaDB). Al iniciar la construcción del backend en Spring Boot (Plan de Desarrollo Backend y PF #5/#6), el equipo migró de MariaDB a PostgreSQL por su soporte para despliegues cloud (Docker/Render/Railway) y manejo estricto de tipos `NUMERIC(19,2)`. Por ello, `ADR-B-003` queda marcado como **Superseded** y se incorpora [`ADR-B-006`](ADR-B-006-migracion-a-postgresql.md).

## ADRs Tecnológicos y Evolución (PF #3)

| ID | Título | Estado | Fecha | Responsable(s) |
|---|---|---|---|---|
| [ADR-F-001](ADR-F-001-uso-de-react-para-frontend.md) | Uso de React para Frontend | Aceptado | 18/08/2026 | Uriel Ramírez |
| [ADR-B-002](ADR-B-002-spring-boot-para-api-rest.md) | Spring Boot para API REST | Aceptado | 20/08/2026 | Uriel, Chris y Daniel |
| [ADR-B-003](ADR-B-003-mariadb-como-bd-relacional.md) | MariaDB como BD Relacional | Superseded (`ADR-B-006`) | 20/08/2026 | Equipo Zentia |
| [ADR-B-004](ADR-B-004-tipos-exactos-para-valores-monetarios.md) | Tipos Exactos para Valores Monetarios | Aceptado | 20/08/2026 | Uriel, Chris y Daniel |
| [ADR-FB-005](ADR-FB-005-google-oauth-para-autenticacion.md) | Google OAuth 2.0 para Autenticación | Aceptado | 20/08/2026 | Uriel Ramírez |
| [ADR-B-006](ADR-B-006-migracion-a-postgresql.md) | Migración a PostgreSQL como BD Relacional Definitiva | Aceptado | 01/09/2026 | Chris y Daniel |

## ADRs Arquitectónicos Obligatorios (PF #4)

| ID | Título | Estado | Fecha | Responsable | Revisión |
|---|---|---|---|---|---|
| [ADR-001](ADR-001-atributos-de-calidad-prioritarios.md) | Atributos de calidad prioritarios | Aceptado | 25/08/2026 | Ángel Uriel Ramírez Salgado (333330) | 10/09/2026 (Verificado) |
| [ADR-002](ADR-002-fuente-de-verdad-y-modelo-de-datos.md) | Fuente de verdad y modelo de datos | Aceptado | 25/08/2026 | Christopher Rivera Hernández (333326) | 08/09/2026 (Verificado) |
| [ADR-003](ADR-003-limites-entre-modulos-y-contratos-internos.md) | Límites entre módulos y contratos internos | Aceptado | 25/08/2026 | Christopher Rivera Hernández (333326) | 15/09/2026 (Verificado) |
| [ADR-004](ADR-004-estilo-de-comunicacion-entre-componentes.md) | Estilo de comunicación entre componentes | Aceptado | 25/08/2026 | Ángel Uriel Ramírez Salgado (333330) | 31/08/2026 (Verificado) |
| [ADR-005](ADR-005-persistencia-y-almacenamiento.md) | Persistencia y almacenamiento | Aceptado | 25/08/2026 | Christopher Rivera Hernández (333326) | 08/09/2026 (Verificado) |
| [ADR-006](ADR-006-comportamiento-ante-fallos-y-modo-degradado.md) | Comportamiento ante fallos y modo degradado | Aceptado | 25/08/2026 | Daniel Alberto Félix Anguiano (333357) | 21/09/2026 (Verificado) |
| [ADR-007](ADR-007-identidad-permisos-y-datos-sensibles.md) | Identidad, permisos y datos sensibles | Aceptado | 25/08/2026 | Ángel Uriel Ramírez Salgado (333330) | 25/09/2026 (Verificado) |
| [ADR-008](ADR-008-despliegue-entornos-y-observabilidad.md) | Despliegue, entornos y observabilidad | Aceptado | 25/08/2026 | Ángel Uriel Ramírez Salgado (333330) | 25/09/2026 (Verificado) |
