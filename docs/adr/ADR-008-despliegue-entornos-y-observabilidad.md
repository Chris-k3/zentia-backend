# ADR-008: Despliegue, entornos y observabilidad — ¿Dónde corre el sistema y cómo sabemos que funciona?

- **Estado:** Aceptado
- **Fecha y responsable:** Ángel Uriel Ramírez Salgado (333330) | 25/08/2026
- **Fecha de revisión:** 25/09/2026 (Verificado)

## Contexto

La aplicación necesita un entorno local reproducible para desarrollo y pruebas unitarias sin afectar datos reales, y un entorno de producción dedicado en la nube donde Frontend, Backend (contenedor Docker), Base de Datos PostgreSQL y proveedor OAuth operen como piezas desacopladas y observables.

## Decisión

Separar estrictamente los entornos Local/Testing y Producción, externalizar toda configuración sensible en variables de entorno (`.env` / `.env.example`), empaquetar el backend con un `Dockerfile` multi-stage (Eclipse Temurin Java 21) y monitorear tres señales clave: tasa de errores HTTP por minuto, tiempo de respuesta y disponibilidad.

## Alternativas descartadas

- **Usar un solo entorno compartido para desarrollo, pruebas y producción:** Se descartó porque una prueba o cambio en desarrollo podría afectar a los usuarios reales.
- **Desplegar todas las piezas acopladas en un mismo proceso o subir credenciales en `application.properties`:** Se descartó por riesgo de seguridad y dificultad para aislar fallas.
- **No implementar observabilidad/logs:** Se descartó porque impediría diagnosticar caídas o lentitud en consultas SQL.

## Consecuencias

**Positivas (+):**
- Las pruebas unitarias corren localmente en milisegundos usando `InMemoryTransactionRepositoryAdapter` sin tocar producción.
- Cada pieza (Frontend, Backend API, PostgreSQL) puede actualizarse y escalarse de forma independiente.
- Cero secretos o contraseñas expuestas en el repositorio GitHub.

**Negativas que aceptamos (-):**
- Requiere configurar y mantener sincronizadas las variables de entorno en dos ambientes distintos (local y nube).
- El equipo debe mantener documentación clara de despliegue (`README.md`, `Dockerfile` y `.env.example`).

## Métrica de verificación

- Medición de errores por minuto, latencia (< 200 ms) y disponibilidad (>= 95%).
- Cualquier fallo en un componente o consulta SQL se identifica mediante los registros estructurados del backend en menos de 5 minutos.
