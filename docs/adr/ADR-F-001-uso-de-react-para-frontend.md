# ADR-F-001: Uso de React para Frontend

- **Estado:** Aceptado
- **Fecha:** 18/08/2026
- **Responsable:** Uriel Ramírez

## Contexto

Se requiere una interfaz web que opere en PC y móvil con componentes reutilizables, formularios interactivos y visualización de gráficas financieras.

## Decisión

Adoptar React como librería/framework frontend con diseño responsivo Mobile-First.

## Alternativas descartadas

- React Native
- Flutter
- Java Swing
- Kotlin nativo

## Consecuencias

**Positivas (+):**
- Desarrollo ágil con componentes reutilizables y separación clara respecto al backend.

**Negativas (-):**
- Requiere configurar librería HTTP, manejo de estado de carga (>200 ms) y CORS con la API.
