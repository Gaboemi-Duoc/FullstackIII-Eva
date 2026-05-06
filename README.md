# SmartLogix — Fullstack III (Evaluación)

Aplicación web fullstack de arquitectura orientada a microservicios para la gestión de usuarios de la plataforma **SmartLogix**. El proyecto implementa un frontend en React, un microservicio de usuarios en Spring Boot, un API Gateway con KrakenD, y una infraestructura completa orquestada con Docker Compose.

---

## Tabla de contenidos

- [Arquitectura general](#arquitectura-general)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Componentes implementados](#componentes-implementados)
  - [Frontend (React + Vite)](#frontend-react--vite)
  - [Microservicio de Usuarios (Spring Boot)](#microservicio-de-usuarios-spring-boot)
  - [API Gateway (KrakenD)](#api-gateway-krakend)
  - [Infraestructura Docker](#infraestructura-docker)
- [Endpoints disponibles](#endpoints-disponibles)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Servicios pendientes de implementar](#servicios-pendientes-de-implementar)

---

## Arquitectura general

El sistema sigue una arquitectura de microservicios donde cada componente tiene una responsabilidad bien definida:

```
[Navegador / Frontend React]
         │
         ▼
  [KrakenD API Gateway :8090]
         │
         ▼
  [service-user :8080]  ←── Base de datos H2 embebida
         │
  [Kafka :9092]          ←── Preparado para mensajería asíncrona
```

El frontend se comunica con el backend únicamente a través del API Gateway (KrakenD), que actúa como punto de entrada único y enrutador de peticiones hacia los microservicios correspondientes.

> El diagrama de contenedores completo se encuentra en `docs/Diagrama de Contenedores - Fullstack III.drawio.png`.

---

## Tecnologías utilizadas

| Capa | Tecnología | Versión |
|---|---|---|
| Frontend | React | 19.x |
| Bundler | Vite | 8.x |
| Routing frontend | React Router DOM | 7.x |
| HTTP client frontend | Axios | 1.15.x |
| Backend (microservicio) | Spring Boot | 4.0.6 |
| Lenguaje backend | Java | 25 |
| Persistencia | Spring Data JPA + H2 (in-memory) | — |
| Reducción de boilerplate | Lombok | — |
| Documentación API | SpringDoc OpenAPI (Swagger UI) | 3.0.2 |
| API Gateway | KrakenD | latest |
| Mensajería asíncrona | Apache Kafka (Bitnami) | latest |
| Contenedores | Docker + Docker Compose | — |

---

## Estructura del proyecto

```
FullstackIII-Eva-main/
├── docker-compose.yml          # Orquestación de todos los servicios
├── docs/
│   ├── Diagrama de Contenedores - Fullstack III.drawio.png
│   └── Fullstack III Parcial 1.pdf
├── frontend/                   # Aplicación React
│   ├── src/
│   │   ├── api/
│   │   │   └── userApi.js      # Funciones de llamada a la API
│   │   ├── components/
│   │   │   └── Navbar.jsx      # Barra de navegación
│   │   ├── pages/
│   │   │   ├── Login.jsx       # Página de inicio de sesión
│   │   │   └── Profile.jsx     # Página de perfil del usuario
│   │   ├── viewmodels/
│   │   │   └── UserView.jsx    # Context + estado global del usuario
│   │   ├── App.jsx             # Enrutador principal
│   │   └── main.jsx            # Punto de entrada React
│   ├── package.json
│   └── vite.config.js
├── service-user/               # Microservicio de usuarios (Spring Boot)
│   ├── src/main/java/alumnoduoc/user_service/
│   │   ├── User.java           # Entidad JPA
│   │   ├── UserRepository.java # Repositorio Spring Data
│   │   ├── UserService.java    # Lógica de negocio
│   │   ├── UserController.java # Controlador REST
│   │   └── UserServiceApplication.java
│   └── pom.xml
├── krakend/
│   └── compose/
│       └── krakend.json        # Configuración del API Gateway
├── bff/                        # Backend for Frontend (pendiente)
├── service-inventory/          # Microservicio de inventario (pendiente)
├── service-orders/             # Microservicio de órdenes (pendiente)
└── service-restock/            # Microservicio de restock (pendiente)
```

---

## Componentes implementados

### Frontend (React + Vite)

El frontend es una Single Page Application (SPA) construida con React 19 y empaquetada con Vite. Implementa el patrón **MVVM** mediante React Context API.

#### `UserView.jsx` — ViewModel / Estado global

Se implementó un contexto de React (`UserContext`) que actúa como ViewModel, centralizado el estado del usuario autenticado. Esto permite que cualquier componente del árbol acceda o modifique los datos del usuario sin necesidad de prop drilling.

```jsx
// Provee { user, setUser } a toda la aplicación
export const UserProvider = ({ children }) => { ... }
export const useUser = () => useContext(UserContext);
```

#### `App.jsx` — Enrutador principal

Envuelve la aplicación en el `UserProvider` y define dos rutas principales:
- `/` → Página de Login
- `/profile` → Página de Perfil (accesible tras autenticarse)

#### `Navbar.jsx` — Barra de navegación

Muestra el nombre de la aplicación **SmartLogix** y, cuando hay un usuario autenticado, muestra un avatar generado con la primera letra del username en mayúscula.

#### `Login.jsx` — Página de inicio de sesión

Formulario con campos `username` y `password`. Al hacer clic en "Login":
1. Llama a `login()` desde `userApi.js` (POST al backend).
2. Si es exitoso, guarda el usuario en el contexto global con `setUser`.
3. Redirige automáticamente a `/profile` usando `useNavigate`.
4. Si las credenciales son incorrectas, muestra un alert de error.

#### `Profile.jsx` — Página de perfil

Muestra los datos del usuario autenticado (username actual) y permite actualizarlos:
1. El usuario ingresa un nuevo username en el campo de texto.
2. Al hacer clic en "Actualizar", se llama a `updateUsername()` (PUT al backend).
3. Si el servidor responde con éxito, el estado global se actualiza y se muestra un alert de confirmación.

#### `userApi.js` — Capa de comunicación con la API

Centraliza todas las llamadas HTTP usando Axios. Actualmente implementa dos funciones:

- **`login(username, password)`** — `POST /api/users/login` — Autentica un usuario y retorna sus datos.
- **`updateUsername(id, username)`** — `PUT /api/users/{id}/username` — Actualiza el username del usuario identificado por su ID.

La URL base apunta directamente al microservicio en `http://localhost:8080/api/users` (en producción se enrutaría a través de KrakenD).

---

### Microservicio de Usuarios (Spring Boot)

Microservicio RESTful construido con Spring Boot 4.0.6 y Java 25. Gestiona el ciclo de vida completo de los usuarios de la plataforma.

#### `User.java` — Entidad JPA

Modelo de datos persistido en base de datos con los siguientes campos:

| Campo | Tipo | Restricción |
|---|---|---|
| `id_user` | Long | PK, autoincremental |
| `username` | String | NOT NULL |
| `email` | String | NOT NULL |
| `password` | String | NOT NULL |

Utiliza Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`) para eliminar código repetitivo. La tabla se mapea con el nombre `"user"` (entre comillas para evitar conflictos con palabras reservadas en SQL).

#### `UserRepository.java` — Repositorio Spring Data JPA

Extiende `JpaRepository<User, Long>`, lo que provee automáticamente los métodos CRUD estándar. Agrega dos consultas personalizadas derivadas del nombre del método:

- `findByUsernameAndPassword(String username, String password)` — usada para el login.
- `findByEmail(String email)` — disponible para búsquedas por email.

#### `UserService.java` — Capa de lógica de negocio

Clase de servicio que encapsula todas las operaciones sobre usuarios:

- **`listarUsuarios()`** — Retorna todos los usuarios registrados.
- **`obtenerPorId(Long id)`** — Busca un usuario por ID; lanza `RuntimeException` si no existe.
- **`registrarUsuario(User user)`** — Persiste un nuevo usuario en la base de datos.
- **`login(String username, String password)`** — Valida credenciales; lanza `RuntimeException("Credenciales incorrectas")` si no coinciden.
- **`actualizarUsername(Long id, String nuevoUsername)`** — Obtiene el usuario, actualiza su username y lo guarda.
- **`eliminarUsuario(Long id)`** — Elimina el usuario por ID.

#### `UserController.java` — Controlador REST

Expone los endpoints REST bajo el prefijo `/api/users`. Tiene habilitado CORS para el origen `http://localhost:5173` (puerto del servidor de desarrollo de Vite).

| Método HTTP | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/users` | Lista todos los usuarios |
| `GET` | `/api/users/{id}` | Obtiene un usuario por ID |
| `POST` | `/api/users/register` | Registra un nuevo usuario |
| `POST` | `/api/users/login` | Autentica un usuario |
| `PUT` | `/api/users/{id}/username` | Actualiza el username |
| `DELETE` | `/api/users/{id}` | Elimina un usuario |

#### Base de datos

Se utiliza **H2** como base de datos embebida en memoria. La dependencia `spring-boot-h2console` sugiere que la consola web de H2 está configurada para facilitar el debugging durante el desarrollo. Los datos se reinician cada vez que el servicio se levanta (comportamiento típico de H2 en modo `create-drop`).

#### Documentación automática (Swagger UI)

La dependencia `springdoc-openapi-starter-webmvc-ui` genera automáticamente documentación interactiva de la API. Al correr el servicio, la interfaz Swagger UI es accesible en:

```
http://localhost:8080/swagger-ui/index.html
```

---

### API Gateway (KrakenD)

KrakenD actúa como puerta de entrada única (`/api/gateway`) para el frontend hacia los microservicios. Está configurado en `krakend/compose/krakend.json` con los siguientes endpoints expuestos:

| Método | Endpoint en Gateway | Backend al que redirige |
|---|---|---|
| `POST` | `/api/users/login` | `http://backend:8080/api/users/login` |
| `POST` | `/api/users/register` | `http://backend:8080/api/users/register` |
| `PUT` | `/api/users/{id}/username` | `http://backend:8080/api/users/{id}/username` |

KrakenD corre en el puerto `8090` del host (mapeado al `8080` interno del contenedor). Depende de los servicios `bff` y `backend` para arrancar.

---

### Infraestructura Docker

El archivo `docker-compose.yml` define cinco servicios dentro de la red `smartlogix-net` (bridge):

| Servicio | Imagen / Build | Puerto host | Descripción |
|---|---|---|---|
| `kafka` | `bitnami/kafka:latest` | `9092` | Broker de mensajería Kafka en modo KRaft (sin Zookeeper) |
| `backend` | `./FullstackIII-Backend` | `8080` | Microservicio principal (Spring Boot) |
| `bff` | `./smartlogix-bff` | `8081` | Backend for Frontend (pendiente de implementar) |
| `krakend` | `devopsfaith/krakend:latest` | `8090` | API Gateway |
| `frontend` | `./FullstackIII-Front` | `5173` | Aplicación React |

**Kafka** está configurado en modo **KRaft** (sin necesidad de Zookeeper), con los roles `broker` y `controller` en el mismo nodo. Todos los servicios comparten la red `smartlogix-net`, lo que les permite comunicarse entre sí usando sus nombres de servicio como hostname (ej: `kafka:9092`, `backend:8080`).

---

## Endpoints disponibles

### Microservicio de Usuarios (`localhost:8080`)

```
GET    /api/users                   → Lista todos los usuarios
GET    /api/users/{id}              → Obtiene usuario por ID
POST   /api/users/register          → Registra un nuevo usuario
POST   /api/users/login             → Autentica un usuario
PUT    /api/users/{id}/username     → Actualiza el username
DELETE /api/users/{id}              → Elimina un usuario
```

**Ejemplo — Body de login:**
```json
{
  "username": "juan123",
  "password": "mipassword"
}
```

**Ejemplo — Body de registro:**
```json
{
  "username": "juan123",
  "email": "juan@example.com",
  "password": "mipassword"
}
```

**Ejemplo — Body de actualización de username:**
```json
{
  "username": "nuevonombre"
}
```

### API Gateway (`localhost:8090`)

```
POST   /api/users/login             → Proxy → backend:8080
POST   /api/users/register          → Proxy → backend:8080
PUT    /api/users/{id}/username     → Proxy → backend:8080
```

---

## Cómo levantar el proyecto

### Prerrequisitos

- Docker y Docker Compose instalados
- Node.js 18+ (para desarrollo local del frontend)
- Java 25 + Maven (para desarrollo local del backend)

### Levantar con Docker Compose

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd FullstackIII-Eva-main

# Levantar todos los servicios
docker compose up --build
```

Los servicios estarán disponibles en:

- **Frontend:** http://localhost:5173
- **API Gateway (KrakenD):** http://localhost:8090
- **Backend (service-user):** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **BFF:** http://localhost:8081

### Desarrollo local del frontend

```bash
cd frontend
npm install
npm run dev
```

### Desarrollo local del microservicio

```bash
cd service-user
./mvnw spring-boot:run
```

---

## Servicios pendientes de implementar

Los siguientes servicios están estructurados en el repositorio pero aún no tienen código (contienen únicamente un `dummy.txt`):

| Servicio | Descripción esperada |
|---|---|
| `bff/` | Backend for Frontend — capa de adaptación entre el gateway y el frontend |
| `service-inventory/` | Microservicio de gestión de inventario |
| `service-orders/` | Microservicio de gestión de órdenes |
| `service-restock/` | Microservicio de reposición de stock |

Kafka ya está disponible en la infraestructura Docker, preparado para la comunicación asíncrona entre estos microservicios cuando sean implementados.