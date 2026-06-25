# Frontend — SmartLogix

Single Page Application (SPA) construida con React 19 y empaquetada con Vite. Implementa el patrón **MVVM** mediante React Context API.

---

## Patrones de Diseño aplicados
- ViewModel (React Context como estado global)
- Arquitectura basada en Componentes: Assets, Componentes, Páginas, Tipos, ViewModel, API
- Singleton (instancia única de Axios por módulo de API)

---

## Rutas de la aplicación

| Ruta | Componente | Auth requerida |
|---|---|---|
| `/` | `Login.tsx` | No |
| `/register` | `Register.tsx` | No |
| `/profile` | `Profile.tsx` | Sí |
| `/inventory` | `Inventory.tsx` | Sí |
| `/orders` | `Orders.tsx` | Sí |
| `/restock` | `Restock.tsx` | Sí |

---

## Estructura de archivos relevante

```
frontend/src/
├── api/
│   ├── ApiConfig.ts       # URL base del BFF (VITE_BFF_URL o localhost:8081)
│   ├── userApi.ts         # Login, registro, actualización de usuario
│   ├── InventoryApi.ts    # CRUD de ítems de inventario
│   ├── OrdersApi.ts       # CRUD de órdenes
│   └── RestockApi.ts      # CRUD de solicitudes de restock
├── components/
│   ├── Navbar.tsx         # Barra de navegación con avatar de usuario
│   └── ProtectedRoute.tsx # Redirige a / si no hay sesión activa
├── pages/                 # Una página por ruta
├── viewmodels/
│   └── UserViewModel.tsx  # Contexto global del usuario autenticado
└── types/
    └── index.tsx          # Tipos compartidos (Item, Order, RestockRequest, etc.)
```

---

## Cómo levantar (desarrollo local)

```bash
cd frontend
npm install
npm run dev
```

Disponible en: `http://localhost:5173`

---

## Cómo levantar con Docker Compose

```bash
# Desde la raíz del proyecto:
docker compose up --build frontend
```

Disponible en: `http://localhost:30080`

---

## Configuración de la URL del BFF

La URL base se configura en `src/api/ApiConfig.ts`:

```ts
BFF_URL: import.meta.env.VITE_BFF_URL || 'http://localhost:8081'
```

Para desarrollo local, el frontend apunta directamente al BFF en `:8081`.       
En producción (Docker), la variable de entorno `VITE_BFF_URL` debe apuntar al API Gateway KrakenD en `:8080`.

---

## Notas

- El frontend nunca llama directamente a los microservicios; toda comunicación pasa por el BFF (vía KrakenD en producción)
- La autenticación se gestiona con JWT almacenado en `localStorage`
- Las rutas protegidas redirigen a `/` si no hay token activo


