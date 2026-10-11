# 📘 Zentia Backend - Documentación de la API REST para Frontend

Esta guía detalla todos los endpoints, métodos HTTP, estructuras de datos y mecanismos de autenticación necesarios para integrar el cliente frontend (React/Vite) con el backend de Zentia.

---

## ⚙️ 1. Configuración General

*   **URL Base:** `http://localhost:8080`
*   **Formato de Datos:** `application/json`
*   **Orígenes CORS habilitados:**
    *   `http://localhost:5173` (Vite)
    *   `http://localhost:3000` (Create React App / Next)
*   **Autenticación:**
    *   Los endpoints protegidos requieren la cabecera:
        ```http
        Authorization: Bearer <TOKEN_JWT>
        ```

---

## 🔐 2. Módulo de Identidad y Autenticación

### A. Autenticación Principal con Google OAuth 2.0
Registra o inicia sesión con las credenciales entregadas por el SDK de Google en React. Si es el primer acceso, crea el usuario y marca `isNewUser: true` y `onboardingCompleted: false`.

*   **Método:** `POST`
*   **Ruta:** `/api/v1/auth/google`
*   **Acceso:** Público (Sin token)
*   **Cuerpo (JSON):**
```json
{
  "googleId": "108392109384729103984",
  "email": "usuario@ejemplo.com",
  "name": "Chris",
  "pictureUrl": "https://lh3.googleusercontent.com/a/...",
  "idToken": "eyJhbGciOiJSUzI1NiIs..."
}
```

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "usuario@ejemplo.com",
  "name": "Chris",
  "onboardingCompleted": false,
  "isNewUser": true
}
```

---

### B. Solicitar Código OTP (Login de Respaldo)
Genera un código OTP de 6 dígitos para inicio de sesión por teléfono. *(En desarrollo, el código se imprime en la terminal del backend)*.

*   **Método:** `POST`
*   **Ruta:** `/api/v1/auth/otp/solicitar`
*   **Acceso:** Público (Sin token)
*   **Parámetros (Query Params / x-www-form-urlencoded):**
    *   `phoneNumber` *(string, obligatorio)*: Número de teléfono con lada (ej. `+523312345678`).

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "message": "Código OTP enviado exitosamente a +523312345678"
}
```

*   **Respuesta de Error (`404 Not Found`):** Si el teléfono no está vinculado a ninguna cuenta:
```json
{
  "error": "El número de teléfono no está registrado en ninguna cuenta."
}
```

---

### B. Verificar Código OTP y Obtener JWT
Valida el código de 6 dígitos ingresado por el usuario y devuelve el token de sesión.

*   **Método:** `POST`
*   **Ruta:** `/api/v1/auth/otp/verificar`
*   **Acceso:** Público (Sin token)
*   **Parámetros (Query Params / x-www-form-urlencoded):**
    *   `phoneNumber` *(string, obligatorio)*
    *   `codigo` *(string, obligatorio)*: Código de 6 dígitos (ej. `481920`).

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3VhcmlvQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJuYW1lIjoiQ2hyaXMiLCJpYXQiOjE3Mzg0MjgwMDAsImV4cCI6MTczODUxNDQwMH0.signature..."
}
```

*   **Respuesta de Error (`401 Unauthorized`):**
```json
{
  "error": "Código OTP inválido o expirado."
}
```

---

### C. Vincular Teléfono al Perfil de Usuario
Permite a un usuario existente (registrado originalmente por Google) asociar su número de teléfono.

*   **Método:** `PUT`
*   **Ruta:** `/api/v1/users/profile/phone`
*   **Acceso:** Público / Habilitado
*   **Parámetros (Query Params):**
    *   `userId` *(long, obligatorio)*
    *   `phoneNumber` *(string, obligatorio)*

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "id": 1,
  "name": "Chris",
  "email": "chris@example.com",
  "pictureUrl": "https://lh3.googleusercontent.com/...",
  "onboardingCompleted": true,
  "phoneNumber": "+523312345678"
}
```

*   **Respuesta de Error (`409 Conflict`):** Si el teléfono ya pertenece a otra cuenta:
```json
{
  "error": "El número de teléfono ya está vinculado a otra cuenta."
}
```

---

## 🏷️ 3. Módulo de Catálogo

### A. Listar Todas las Categorías
Obtiene el catálogo completo de categorías para alimentar selectores y filtros en el frontend.

*   **Método:** `GET`
*   **Ruta:** `/api/v1/categories`
*   **Acceso:** Público (Sin token requerido)

*   **Respuesta Exitosa (`200 OK`):**
```json
[
  {
    "id": 1,
    "name": "Alimentación",
    "type": "GASTO",
    "icon": "fast-food-outline"
  },
  {
    "id": 2,
    "name": "Salario",
    "type": "INGRESO",
    "icon": "cash-outline"
  },
  {
    "id": 3,
    "name": "Transporte",
    "type": "GASTO",
    "icon": "car-outline"
  }
]
```

---

## 💰 4. Módulo Financiero (Transacciones)

> [!IMPORTANT]
> Todos los endpoints de transacciones extraen automáticamente el `userId` desde la firma del Token JWT. **No es necesario (ni permitido) enviar el `userId` en la URL o en el cuerpo**.

---

### A. Registrar Transacción
*   **Método:** `POST`
*   **Ruta:** `/api/v1/transactions`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Cuerpo (JSON):**
```json
{
  "categoryId": 1,
  "amount": 250.50,
  "type": "GASTO",
  "description": "Cena familiar",
  "transactionDate": "2026-10-10"
}
```
*(Nota: `transactionDate` acepta formato estándar `"YYYY-MM-DD"`. Si se omite o viene nulo, toma la fecha actual).*

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "id": 15,
  "description": "Cena familiar",
  "amount": 250.5,
  "date": "2026-10-10",
  "type": "GASTO",
  "categoryName": "Alimentación"
}
```

---

### B. Consultar Historial de Transacciones
Devuelve las transacciones del usuario autenticado en un rango de fechas.

*   **Método:** `GET`
*   **Ruta:** `/api/v1/transactions/historial?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Query Params:**
    *   `startDate` *(string, obligatorio)*: ej. `2026-10-01`
    *   `endDate` *(string, obligatorio)*: ej. `2026-10-31`

*   **Respuesta Exitosa (`200 OK`):**
```json
[
  {
    "id": 15,
    "description": "Cena familiar",
    "amount": 250.5,
    "date": "2026-10-10",
    "type": "GASTO",
    "categoryName": "Alimentación"
  },
  {
    "id": 14,
    "description": "Pago quincenal",
    "amount": 15000.0,
    "date": "2026-10-05",
    "type": "INGRESO",
    "categoryName": "Salario"
  }
]
```

---

### C. Resumen Financiero para Dashboard
Calcula de forma exacta (con `BigDecimal`) los ingresos totales, gastos totales y balance del usuario en el rango seleccionado.

*   **Método:** `GET`
*   **Ruta:** `/api/v1/transactions/dashboard?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Query Params:**
    *   `startDate` *(string, obligatorio)*: ej. `2026-10-01`
    *   `endDate` *(string, obligatorio)*: ej. `2026-10-31`

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "totalIngresos": 15000.00,
  "totalGastos": 3450.75,
  "balanceActual": 11549.25
}
```

---

### D. Actualizar Transacción
Modifica los datos de una transacción existente. El backend valida estrictamente que la transacción pertenezca al usuario logueado.

*   **Método:** `PUT`
*   **Ruta:** `/api/v1/transactions/{transactionId}`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Cuerpo (JSON):**
```json
{
  "categoryId": 3,
  "amount": 300.00,
  "description": "Cena familiar en restaurante"
}
```

*   **Respuesta Exitosa (`200 OK`):**
```json
{
  "id": 15,
  "description": "Cena familiar en restaurante",
  "amount": 300.0,
  "date": "2026-10-10",
  "type": "GASTO",
  "categoryName": "Transporte"
}
```

---

### E. Eliminar Transacción
Elimina de forma permanente una transacción del usuario.

*   **Método:** `DELETE`
*   **Ruta:** `/api/v1/transactions/{transactionId}`
*   **Headers:** `Authorization: Bearer <TOKEN>`

*   **Respuesta Exitosa (`204 No Content`):** Sin cuerpo.

---

## 💻 5. Ejemplo de Configuración en React (Axios)

```typescript
// api/client.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para inyectar automáticamente el Token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('zentia_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

