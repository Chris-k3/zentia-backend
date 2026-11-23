# ADR-007: Identidad, permisos y datos sensibles — ¿Quién puede hacer qué y qué hay que proteger?

- **Estado:** Aceptado
- **Fecha y responsable:** Ángel Uriel Ramírez Salgado (333330) | 25/08/2026
- **Fecha de revisión:** 25/09/2026 (Verificado)

## Contexto

1. El sistema maneja datos financieros privados (ingresos, gastos, metas de ahorro) de +500 usuarios, exigiendo que un usuario jamás pueda ver o alterar información ajena.
2. El cliente se ejecuta en el navegador y puede ser manipulado para enviar un `userId` falso en el JSON o URL.
3. El equipo no asumirá el riesgo crítico de almacenar ni encriptar contraseñas propias en la base de datos.

## Decisión

Delegar la autenticación inicial a Google OAuth 2.0 y centralizar la autorización estrictamente en el backend mediante tokens JWT firmados (`HMAC-SHA256`). En cada petición HTTP protegida, `JwtAuthenticationFilter` valida el token, extrae el `userId` verificado hacia el `SecurityContext` y los casos de uso comprueban la propiedad de cada registro.

## Alternativas descartadas

- **Crear un sistema propio de usuarios y contraseñas locales:** Se descartó por el alto riesgo de seguridad en almacenamiento y recuperación de credenciales.
- **Confiar en el `userId` enviado por el frontend en el cuerpo o query string:** Se descartó absolutamente porque permitiría ataques IDOR (consultar o borrar finanzas de otra persona).

## Consecuencias

**Positivas (+):**
- Inicio de sesión rápido y seguro; al no existir columna de contraseña en la tabla `users`, se elimina el riesgo de filtración de contraseñas.
- Protección total contra suplantación de identidad (IDOR) en los endpoints de transacciones.

**Negativas que aceptamos (-):**
- Fuerte dependencia operativa hacia Google OAuth 2.0 durante el inicio de sesión.
- El backend asume el costo computacional de validar firma y expiración del JWT en cada petición HTTP.

## Métrica de verificación

- **Prueba de intrusión cruzada:** Enviar petición con token válido del "Usuario A" intentando editar o eliminar el ID de una transacción del "Usuario B"; el backend la rechaza inmediatamente con `SecurityException`.
- **Prueba de expiración:** Enviar petición CRUD con token expirado o alterado; el backend responde `401 Unauthorized` sin colapsar.
