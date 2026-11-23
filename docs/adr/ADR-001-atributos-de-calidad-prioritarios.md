# ADR-001: Atributos de calidad prioritarios — ¿Qué debe proteger el sistema por encima de todo lo demás?

- **Estado:** Aceptado
- **Fecha y responsable:** Ángel Uriel Ramírez Salgado (333330) | 25/08/2026
- **Fecha de revisión:** 10/09/2026 (Verificado)

## Contexto

En tiempos de quincenas los usuarios registran sus movimientos financieros, generándose más de 100 peticiones simultáneas; no debe haber retrasos mayores a 200 ms y se debe soportar concurrencia en horas pico. Al ser información financiera privada, solo el usuario titular puede acceder a sus propios datos mediante validaciones constantes de seguridad. El público objetivo son universitarios y jóvenes trabajadores, por lo que la plataforma debe contar con interfaces amigables y responsivas (Mobile-First).

**Atributos sacrificados explícitamente:** Disponibilidad multi-región activa-activa y Portabilidad dinámica de motor.

## Decisión

1. Priorizar el **Rendimiento** para que ante +100 usuarios simultáneos realizando operaciones no existan retrasos superiores a 200 ms (p95).
2. Priorizar la **Seguridad** por encima de la comodidad de desarrollo para garantizar que solo el usuario propietario acceda a sus registros autenticándose mediante Google OAuth 2.0 y JWT.
3. Priorizar la **Usabilidad** mediante un diseño responsivo (Mobile-First) y flujo de Onboarding intuitivo.

## Alternativas descartadas

- **Disponibilidad:** Se descartó como prioridad #1 porque dependemos de la infraestructura de nuestro proveedor cloud de hosting y base de datos sin clúster multi-región.
- **Portabilidad:** Se rechazó priorizarla sobre el rendimiento y seguridad porque el objetivo es consolidar el MVP sobre PostgreSQL y Spring Boot.

## Consecuencias

**Positivas (+):**
- Mejor rendimiento y tiempos de carga reducidos (< 200 ms) al interactuar con cada módulo.
- Confiabilidad y seguridad para los usuarios al no entregar contraseñas locales.
- Mayor adopción por el público joven gracias a una interfaz comprensible en menos de 15 minutos.

**Negativas que aceptamos (-):**
- La aplicación queda temporalmente inutilizable si el proveedor cloud de PostgreSQL o el servidor de alojamiento entra en mantenimiento.
- No existe modo de escritura offline sin conexión a internet.

## Métrica de verificación

- Con pico de usuarios (+100 peticiones concurrentes), el 95% de las operaciones responde en < 200 ms.
- Un usuario no autenticado recibe `401 Unauthorized` en el 100% de los endpoints protegidos y jamás recupera datos de otro usuario.
- Un usuario nuevo comprende y opera el flujo principal en menos de 15 minutos desde su celular.
