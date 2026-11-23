# ADR-B-004: Tipos Exactos para Valores Monetarios

- **Estado:** Aceptado
- **Fecha:** 20/08/2026
- **Responsables:** Uriel, Chris y Daniel

## Contexto

Los tipos de punto flotante (`float` y `double`) generan errores binarios de representación decimal (ej. `0.10 + 0.20 != 0.30`) que corrompen balances financieros.

## Decisión

Usar obligatoriamente `BigDecimal` en Java y `DECIMAL`/`NUMERIC(19, 2)` en SQL, validando escala y manejando divisiones entre cero.

## Alternativas descartadas

- `double`/`float`
- Montos como `String`
- Enteros en centavos

## Consecuencias

**Positivas (+):**
- Exactitud matemática del 100% en sumas, restas y balances.

**Negativas (-):**
- Código más verboso en Java (uso de `.add()`, `.subtract()`, `.compareTo()` en lugar de operadores `+`, `-`, `==`).
