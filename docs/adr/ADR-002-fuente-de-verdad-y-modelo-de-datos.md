# ADR-002: Fuente de verdad y modelo de datos — ¿Dónde vive el dato autoritativo y quién puede modificarlo?

- **Estado:** Aceptado
- **Fecha y responsable:** Christopher Rivera Hernández (333326) | 25/08/2026
- **Fecha de revisión:** 08/09/2026 (Verificado)

## Contexto

1. El sistema maneja información monetaria para +500 usuarios registrados con picos de 50 a 100 usuarios simultáneos.
2. Existe riesgo de integridad por "condición de carrera": si un usuario experimenta latencia y presiona el botón de guardar varias veces, podría duplicar un gasto.
3. Las transacciones deben estar estrictamente ligadas a su dueño original, sin posibilidad de accesos cruzados.

## Decisión

Decidimos que el módulo Backend (Spring Boot + PostgreSQL) sea el único dueño y la fuente de verdad absoluta de las tres entidades centrales (`User`, `Transaction` y `Category`), delegando la generación de los identificadores únicos a la base de datos de forma autoincremental (`IDENTITY`) para lograr control total sobre las modificaciones del dinero.

## Alternativas descartadas

- **Frontend escribe directamente en la base de datos:** Se descarta porque permitiría saltarse las reglas de negocio y validaciones matemáticas del backend.
- **Generar IDs en el cliente:** Se descarta porque podría crear duplicados o conflictos al sincronizar.

## Consecuencias

**Positivas (+):**
- El backend centraliza todas las reglas de negocio y protege la integridad de los datos monetarios.
- Cualquier cliente actual o futuro (web o móvil) pasa obligatoriamente por las mismas validaciones, evitando escrituras inconsistentes.

**Negativas que aceptamos (-):**
- El módulo Backend se convierte en un punto único obligatorio de validación (cuello de botella controlado).
- Ningún cliente nuevo (ej. app móvil nativa futura) podrá escribir directo en la base de datos; deberá integrarse forzosamente con la API REST.

## Métrica de verificación

- Simular un ataque de latencia enviando 5 peticiones idénticas de "registrar gasto de $100" con la misma clave de idempotencia en el mismo milisegundo: el sistema registra solo 1 y rechaza las demás garantizando la invariante de no duplicidad.
