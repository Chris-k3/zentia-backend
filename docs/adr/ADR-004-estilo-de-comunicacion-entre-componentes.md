# ADR-004: Estilo de comunicación entre componentes — ¿Cómo hablan las partes entre sí?

- **Estado:** Aceptado
- **Fecha y responsable:** Ángel Uriel Ramírez Salgado (333330) | 25/08/2026
- **Fecha de revisión:** 31/08/2026 (Verificado)

## Contexto

El frontend en React se comunica con el backend mediante una API REST. El 95% de las operaciones involucran consultas y modificaciones de transacciones en la base de datos donde el usuario requiere confirmación inmediata de si su dinero quedó registrado correctamente, considerando posibles demoras de red o de base de datos.

## Decisión

Utilizar comunicación HTTP REST síncrona en el backend para las operaciones CRUD y de Dashboard (con tiempos máximos de espera de 5 s, manejo controlado de errores e idempotencia), mientras que el frontend realiza las peticiones de forma asíncrona no bloqueante (`async`/`await`) mostrando estados de carga para no congelar la interfaz.

## Alternativas descartadas

- **Eventos / Colas de mensajería (RabbitMQ/Kafka) para el CRUD principal:** Se descartaron para las operaciones transaccionales básicas porque el usuario necesita saber de inmediato si su gasto fue procesado y no lidiar con estados "pendientes" eventuales.

## Consecuencias

**Positivas (+):**
- El flujo visual de la aplicación no se congela mientras espera respuesta del servidor.
- Confirmación inmediata y determinista (`HTTP 200`/`201`/`204`) sobre el estado del dinero.
- Los errores de comunicación se manejan limpiamente mediante timeouts, reintentos controlados e idempotencia.

**Negativas que aceptamos (-):**
- Las operaciones CRUD dependen temporalmente de que el backend y PostgreSQL estén disponibles en ese instante.
- El frontend debe programar y gestionar explícitamente los estados de carga (`loading`), error y reintento.

## Métrica de verificación

- El 95% de las operaciones CRUD responde en < 200 ms bajo carga definida.
- Operaciones que superen 5 s finalizan con timeout controlado mientras el frontend muestra estado de carga sin bloquear la UI.
