# ADR-005: Persistencia y almacenamiento — ¿Dónde y cómo se guardan los datos, y qué se pierde si falla?

- **Estado:** Aceptado
- **Fecha y responsable:** Christopher Rivera Hernández (333326) | 25/08/2026
- **Fecha de revisión:** 08/09/2026 (Verificado)

## Contexto

1. El sistema maneja información monetaria para +500 usuarios registrados con picos de 50 usuarios simultáneos.
2. Las transacciones (ingresos/gastos) deben relacionarse estrictamente con el usuario y categoría que las genera, evitando registros huérfanos.
3. Es obligatorio evitar errores de punto flotante o duplicidad de registros.
4. El sistema opera bajo una cuota estricta de almacenamiento en la nube.

## Decisión

Utilizar un motor de base de datos relacional ACID (PostgreSQL, `zentia_db`) configurando tipos de precisión numérica exacta `NUMERIC(19, 2)` mapeados a `BigDecimal` en Java, llaves foráneas obligatorias (`user_id`, `category_id`), índices compuestos por `(user_id, transaction_date)` para agilizar el Dashboard, y respaldos diarios incrementales.

## Alternativas descartadas

- **MongoDB (Documental):** Se descartó porque el sistema exige relaciones referenciales estrictas e integridad transaccional ACID sobre montos monetarios.
- **SQLite:** Se descartó como base principal por limitaciones de concurrencia en despliegues multiusuario en la nube.
- **Guardar montos como `float`/`double`:** Se descartó porque genera errores de centavos al calcular porcentajes o balances.

## Consecuencias

**Positivas (+):**
- PostgreSQL garantiza mediante llaves foráneas que jamás exista un ingreso o gasto sin usuario o sin categoría válida.
- Los cálculos monetarios son 100% exactos a nivel de aplicación y base de datos.
- Las agrupaciones matemáticas del mes (Dashboard) se resuelven en milisegundos gracias al índice compuesto.

**Negativas que aceptamos (-):**
- Esquema relacional riguroso: agregar nuevos atributos requiere modificar el esquema/migración de la tabla.
- Mantener índices compuestos consume espacio de la cuota de almacenamiento.
- En el peor escenario catastrófico (caída total del servidor justo antes del respaldo diario), se acepta perder un máximo de 24 horas de registros (`RPO = 24 h`).

## Métrica de verificación

- El 95% de las consultas e inserciones de transacciones responde en < 200 ms con 50 usuarios concurrentes.
- Restauración desde respaldo diario levanta el sistema en menos de 30 minutos (`RTO < 30 min`) con pérdida máxima de 24 h.
