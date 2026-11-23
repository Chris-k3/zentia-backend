# ADR-003: Límites entre módulos y contratos internos — ¿Cómo se parte el sistema y qué promete cada parte?

- **Estado:** Aceptado
- **Fecha y responsable:** Christopher Rivera Hernández (333326) | 25/08/2026
- **Fecha de revisión:** 15/09/2026 (Verificado)

## Contexto

El sistema atiende a más de 500 personas registradas, procesando información de dos naturalezas con razones de cambio totalmente distintas: datos de identidad/sesión (tokens de Google, JWT y cuestionario de Onboarding) y cálculos monetarios exactos (ingresos, gastos y balances). Al existir datos financieros sensibles, la lógica matemática jamás debe quedar expuesta a vulnerabilidades originadas por cambios en credenciales.

## Decisión

Dividir el backend en dos módulos de negocio aislados por razón de cambio: **Gestión de Identidad y Perfil** (`com.zentia.app.identity`) y **Core / Lógica Financiera** (`com.zentia.app.finance`), logrando que los cambios en la autenticación externa no rompan la lógica matemática interna y viceversa.

## Alternativas descartadas

- **Microservicios distribuidos:** Se descartó porque para una carga de 50 a 300 usuarios concurrentes, separar en distintos servidores físicos introduciría latencia de red innecesaria y complejidad de despliegue.
- **Monolito sin división modular:** Se descartó porque un cambio en el token de Google OAuth 2.0 o en el Onboarding podría romper los cálculos matemáticos del dashboard debido al alto acoplamiento.

## Consecuencias

**Positivas (+):**
- El equipo puede modificar cómo se agrupan las categorías o cómo se calcula el balance (Core Financiero) sin miedo a romper el inicio de sesión.
- Contrato estricto: el módulo de identidad resuelve y expone un `userId` válido; el módulo financiero expone casos de uso CRUD y de resumen mediante puertos y DTOs.

**Negativas que aceptamos (-):**
- Mayor complejidad estructural interna, ya que obliga a crear objetos de transferencia de datos (DTOs) y puertos (Ports/Adapters).
- Queda prohibido exponer entidades crudas de base de datos directamente hacia el cliente.

## Métrica de verificación

- Verificar mediante pruebas unitarias (`RegistrarTransaccionUseCaseTest` y `ActualizarTransaccionUseCaseTest`) que los casos de uso financieros se ejecutan usando puertos en memoria sin importar librerías de OAuth o Spring Security.
