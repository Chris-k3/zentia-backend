# ADR-006: Comportamiento ante fallos y modo degradado — ¿Qué hace el sistema cuando algo se cae?

- **Estado:** Aceptado
- **Fecha y responsable:** Daniel Alberto Félix Anguiano (333357) | 25/08/2026
- **Fecha de revisión:** 21/09/2026 (Verificado)

## Contexto

1. Los usuarios acceden principalmente desde dispositivos móviles y pueden quedarse sin red repentinamente a mitad de una transacción financiera.
2. La base de datos puede experimentar lentitud bajo carga concurrente o consultas pesadas.
3. El inicio de sesión depende de los servicios externos de Google OAuth 2.0.

## Decisión

Implementar un límite de tiempo máximo (timeout) de 5 segundos en las peticiones y mostrar un banner de estado del sistema ("Servicio lento" o "Sin conexión a internet"), liberando la interfaz para evitar pantallas congeladas y prohibiendo el guardado de transacciones offline no autenticadas.

## Alternativas descartadas

- **Esperar indefinidamente a que la base de datos responda:** Se descartó porque congela la pantalla del usuario y satura los hilos del servidor.
- **Guardar los gastos en el almacenamiento local del celular sin red:** Se descartó porque el sistema requiere conexión para validar el token de seguridad y ejecutar los cálculos en el backend.

## Consecuencias

**Positivas (+):**
- Mejora drásticamente la usabilidad: el usuario recibe retroalimentación clara en máximo 5 segundos sin pantallas blancas o congeladas.
- Protege el backend y el pool de conexiones de PostgreSQL al abortar peticiones colgadas.

**Negativas que aceptamos (-):**
- Si el cliente corta la conexión al segundo 5 pero el backend termina de guardar en el segundo 6, el usuario verá un aviso de error temporal (mitigado al reintentar mediante clave de idempotencia).
- Usuarios con conexiones extremadamente lentas (> 5 s) deberán reintentar su operación.

## Métrica de verificación

- Simular artificialmente una tardanza de 10 segundos en la BD: el sistema cancela la espera a los 5 segundos exactos y muestra el banner "Servicio lento".
- Apagar el Wi-Fi/datos móviles e intentar registrar un gasto: la app muestra "Sin conexión a internet" de inmediato sin colapsar.
