## Frontend (React + Vite)
Requiere Node.js
React

http://localhost:5173

El frontend es una Single Page Application (SPA) construida con React 19 y empaquetada con Vite. Implementa el patrón **MVVM** mediante React Context API.

#### Patrones de Diseño usados:
    - ViewModel
    - Arquitectura basada en Componentes: Assets, Componentes, Paginas, Tipos, VM, API
    - Singletons

#### `UserView.tsx` — ViewModel / Estado global

Se implementó un contexto de React (`UserContext`) que actúa como ViewModel, centralizado el estado del usuario autenticado. Esto permite que cualquier componente del árbol acceda o modifique los datos del usuario sin necesidad de prop drilling.

```tsx
// Provee { user, setUser } a toda la aplicación
export const UserProvider = ({ children }) => { ... }
export const useUser = () => useContext(UserContext);
```

#### `App.tsx` — Enrutador principal

Envuelve la aplicación en el `UserProvider` y define dos rutas principales:
- `/` → Página de Login
- `/profile` → Página de Perfil (accesible tras autenticarse)

#### `Navbar.tsx` — Barra de navegación

Muestra el nombre de la aplicación **SmartLogix** y, cuando hay un usuario autenticado, muestra un avatar generado con la primera letra del username en mayúscula.

#### `Login.tsx` — Página de inicio de sesión

Formulario con campos `username` y `password`. Al hacer clic en "Login":
1. Llama a `login()` desde `userApi.js` (POST al backend).
2. Si es exitoso, guarda el usuario en el contexto global con `setUser`.
3. Redirige automáticamente a `/profile` usando `useNavigate`.
4. Si las credenciales son incorrectas, muestra un alert de error.

#### `Profile.tsx` — Página de perfil

Muestra los datos del usuario autenticado (username actual) y permite actualizarlos:
1. El usuario ingresa un nuevo username en el campo de texto.
2. Al hacer clic en "Actualizar", se llama a `updateUsername()` (PUT al backend).
3. Si el servidor responde con éxito, el estado global se actualiza y se muestra un alert de confirmación.

#### `userApi.js` — Capa de comunicación con la API

Centraliza todas las llamadas HTTP usando Axios. Actualmente implementa dos funciones:

- **`login(username, password)`** — `POST /api/users/login` — Autentica un usuario y retorna sus datos.
- **`updateUsername(id, username)`** — `PUT /api/users/{id}/username` — Actualiza el username del usuario identificado por su ID.

La URL base apunta directamente al microservicio en `http://localhost:8080/api/users` (en producción se enrutaría a través de KrakenD).

## Cómo levantar el proyecto
```bash
cd frontend
npm install
npm install axios react-router-dom
npm run dev
```

