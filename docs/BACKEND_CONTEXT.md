# Contexto del backend de Zentia

## Propósito

Zentia es un backend REST para gestionar usuarios y sus finanzas personales. El sistema
permite autenticarse, consultar categorías, registrar transacciones y generar un resumen
financiero por rango de fechas.

Este documento sirve como mapa de orientación para nuevos agentes. Describe el código que
existe actualmente; no debe interpretarse como una lista de funcionalidades futuras.
Facilita el no pedirle al gente leer el código nuevamente cada vez que inicias un task.

## Stack y ejecución

- Java 21.
- Spring Boot 4.1.1.
- Spring MVC para la API REST.
- Spring Data JPA/Hibernate para persistencia.
- Spring Security para autenticación de peticiones.
- PostgreSQL como base de datos.
- Maven mediante `pom.xml`.
- Puerto configurable con `PORT`; el valor predeterminado es `8080`.
- Conexión de base de datos configurable mediante `SPRING_DATASOURCE_URL`,
  `SPRING_DATASOURCE_USERNAME` y la contraseña correspondiente.

La clase de entrada es
`src/main/java/com/zentia/app/ZentiaApplication.java`.

La configuración de JPA usa `spring.jpa.hibernate.ddl-auto=update`, muestra SQL y aplica
el dialecto de PostgreSQL. No hay perfiles separados ni migraciones Flyway/Liquibase.

## Estructura de paquetes

```text
src/main/java/com/zentia/app/
├── finance/
│   ├── adapter/       Adaptadores de persistencia
│   ├── controller/    Endpoints de categorías y transacciones
│   ├── dto/           Objetos de entrada y salida de la API
│   ├── model/         Entidades y enums financieros
│   ├── port/          Interfaces de persistencia usadas por casos de uso
│   ├── repository/    Repositorios Spring Data
│   ├── services/      Servicios de catálogo
│   └── usecase/       Operaciones financieras del dominio
└── identity/
    ├── controller/    Endpoints de autenticación y perfil
    ├── dto/           Objetos de autenticación y usuario
    ├── model/         Entidad User
    ├── repository/    Persistencia de usuarios
    ├── security/      Configuración, filtro y utilidades JWT
    └── services/      Registro, login, teléfono y OTP
```

La aplicación usa una separación por capas. En finanzas, los casos de uso dependen de
`TransactionRepositoryPort` y `TransactionRepositoryAdapter` conecta ese puerto con
`TransactionRepository`. Los repositorios de usuarios y categorías se usan directamente
desde sus servicios.

## Modelo de datos y relaciones

### User

Archivo: `src/main/java/com/zentia/app/identity/model/User.java`

Tabla: `users`.

| Atributo | Tipo | Reglas |
|---|---|---|
| `id` | `Long` | Identificador generado |
| `email` | `String` | Obligatorio, único, máximo 100 |
| `googleId` | `String` | Obligatorio, único, máximo 100 |
| `name` | `String` | Obligatorio, máximo 100 |
| `pictureUrl` | `String` | Opcional, máximo 255 |
| `onboardingCompleted` | `boolean` | Obligatorio; inicia en `false` |
| `phoneNumber` | `String` | Opcional, único, máximo 20 |
| `createdAt` | `LocalDateTime` | Se asigna al crear; no se actualiza |

No existe una colección JPA inversa de transacciones en `User`.

### Category

Archivo: `src/main/java/com/zentia/app/finance/model/Category.java`

Tabla: `categories`.

| Atributo | Tipo | Reglas |
|---|---|---|
| `id` | `Long` | Identificador generado |
| `name` | `String` | Obligatorio, máximo 100 |
| `type` | `TransactionType` | Obligatorio; se guarda como texto |
| `icon` | `String` | Opcional, máximo 255 |

### Transaction

Archivo: `src/main/java/com/zentia/app/finance/model/Transaction.java`

Tabla: `transactions`.

| Atributo | Tipo | Reglas |
|---|---|---|
| `id` | `Long` | Identificador generado |
| `user` | `User` | `ManyToOne` obligatorio, columna `user_id` |
| `category` | `Category` | `ManyToOne` obligatorio, columna `category_id` |
| `amount` | `BigDecimal` | Obligatorio; precisión 19, escala 2 |
| `type` | `TransactionType` | Se guarda como texto |
| `description` | `String` | Opcional, máximo 255 |
| `transactionDate` | `LocalDate` | Obligatorio |
| `createdAt` | `LocalDateTime` | Se asigna al crear; no se actualiza |

Las relaciones son unidireccionales desde `Transaction`; no hay `mappedBy`, cascadas ni
eliminación en cascada.

```text
User 1 ─── N Transaction N ─── 1 Category
```

`TransactionType`, en
`src/main/java/com/zentia/app/finance/model/TransactionType.java`, solo admite
`INGRESO` y `GASTO`.

### Modelos que no existen como entidades

- **Onboarding/Cuestionario:** no hay una tabla ni clase propia; solo existe
  `User.onboardingCompleted`.
- **Dashboard:** no hay una tabla ni clase propia; se devuelve un mapa con totales.
- **Session/Token:** no hay una tabla; el JWT se genera y valida en memoria durante cada
  petición.

## Flujo de identidad y seguridad

`UserService.authenticateWithGoogle` recibe datos del login, busca primero por `googleId` y
después por email. Si no encuentra al usuario, crea un `User` con onboarding incompleto.
Luego genera un JWT interno mediante `JwtUtil`.

El JWT contiene:

- `sub`: email.
- `userId`.
- `name`.
- `iat`.
- `exp`, con una duración de 24 horas.

`JwtAuthenticationFilter` lee el encabezado `Authorization: Bearer ...`, valida firma y
expiración, extrae `userId` y lo coloca como principal en el `SecurityContext`.

`SecurityConfig` mantiene sesiones stateless, deshabilita CSRF, instala el filtro JWT y
protege las rutas de transacciones. Permite las rutas POST de autenticación, el endpoint
de teléfono configurado, las categorías GET y las peticiones OPTIONS.

Importante: el backend no valida el `idToken` directamente contra Google. Valida el JWT
interno que genera Zentia después del login.

## Casos de uso financieros

### Registrar transacción

Archivo: `finance/usecase/RegistrarTransaccionUseCase.java`.

1. Valida que el monto sea mayor que cero.
2. Busca el usuario.
3. Busca la categoría.
4. Convierte el tipo a `TransactionType`.
5. Asigna usuario, categoría, monto, tipo, descripción y fecha.
6. Persiste mediante el puerto de transacciones.

El controlador obtiene el usuario desde el JWT y no confía en el `userId` recibido en el
DTO.

### Consultar historial

Archivo: `finance/usecase/ConsultarHistorialUseCase.java`.

Verifica que el usuario exista y consulta sus transacciones dentro de un rango de fechas.

### Actualizar transacción

Archivo: `finance/usecase/ActualizarTransaccionUseCase.java`.

Busca la transacción, comprueba que pertenece al usuario autenticado y actualiza monto,
categoría y descripción. No modifica tipo ni fecha.

### Eliminar transacción

Archivo: `finance/usecase/EliminarTransaccionUseCase.java`.

Busca la transacción, comprueba la pertenencia al usuario autenticado y la elimina.

### Generar dashboard

Archivo: `finance/usecase/GenerarResumenDashboardUseCase.java`.

Filtra las transacciones del usuario por fecha y devuelve:

- `totalIngresos`
- `totalGastos`
- `balanceActual = totalIngresos - totalGastos`

El resultado es `Map<String, BigDecimal>`, no una entidad persistente.

## API principal

| Método | Ruta | Acceso | Responsabilidad |
|---|---|---|---|
| POST | `/api/v1/auth/google` | Público | Login/registro con datos de Google |
| POST | `/api/v1/auth/otp/solicitar` | Público | Solicitar OTP por teléfono |
| POST | `/api/v1/auth/otp/verificar` | Público | Verificar OTP y generar JWT |
| PUT | `/api/v1/users/profile/phone` | Configurado como público | Vincular teléfono |
| GET | `/api/v1/categories` | Público | Listar categorías |
| POST | `/api/v1/transactions` | Autenticado | Registrar transacción |
| GET | `/api/v1/transactions/historial` | Autenticado | Consultar historial por fechas |
| GET | `/api/v1/transactions/dashboard` | Autenticado | Calcular resumen por fechas |
| PUT | `/api/v1/transactions/{transactionId}` | Autenticado | Actualizar transacción propia |
| DELETE | `/api/v1/transactions/{transactionId}` | Autenticado | Eliminar transacción propia |

Los parámetros y cuerpos detallados están en `API_DOCUMENTATION.md`.

## Flujos de negocio actuales

### Alta de usuario

1. El cliente llama al endpoint de autenticación.
2. Se busca un usuario por Google ID o email.
3. Si es nuevo, se crea con `onboardingCompleted = false`.
4. Se genera un JWT interno.
5. Se devuelve el usuario, el token y el estado de onboarding.

### Operación financiera

1. El cliente envía un JWT.
2. El filtro valida el token y establece el `userId`.
3. El controlador obtiene el usuario autenticado.
4. El caso de uso valida pertenencia y datos de dominio.
5. Se persiste o consulta la transacción.

### Onboarding

El estado inicial sí está implementado, pero el flujo completo no. No existe endpoint ni
caso de uso para marcar el onboarding como completado y las operaciones de transacciones y
dashboard no bloquean actualmente a usuarios con `onboardingCompleted = false`.

## Límites conocidos del estado actual

Estos puntos describen el comportamiento observado y son relevantes para futuros cambios:

- El `idToken` de Google recibido por el backend no se verifica contra Google.
- La clave usada para firmar JWT está definida en código, no en configuración externa.
- El endpoint de vinculación de teléfono está configurado como público y recibe `userId`.
- El filtro JWT no consulta nuevamente la existencia o estado del usuario en la base de datos.
- Los OTP se guardan en memoria, no tienen expiración ni límite de intentos y se imprimen en
  consola.
- No hay entidad ni flujo completo de onboarding.
- No hay manejo global de excepciones ni validación Bean Validation visible.
- Se usa `double` en el DTO de respuesta de transacciones aunque la entidad usa `BigDecimal`.
- No se valida en el caso de uso que el tipo de la transacción coincida con el tipo de la
  categoría.

## Archivos de referencia

- `pom.xml`: dependencias, Java y versión de Spring Boot.
- `src/main/resources/application.properties`: puerto, base de datos y JPA.
- `API_DOCUMENTATION.md`: detalle existente de solicitudes y respuestas.
- `src/main/java/com/zentia/app/identity`: identidad, usuarios y seguridad.
- `src/main/java/com/zentia/app/finance`: categorías, transacciones y dashboard.
- `README.md`: reglas de negocio y diseño por capas propuesto para RN-02, RN-04 y RN-07.
