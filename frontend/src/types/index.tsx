// User types
export interface User {
  id_user: number;
  username: string;
  token: string;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

// Inventory types
export interface Item {
  id_item: number;
  nombre: string;
  descripcion: string;
  cantidad: number;
  precio: number;
  bodega: string;
}

export interface NewItem {
  nombre: string;
  descripcion: string;
  cantidad: number;
  precio: number;
  bodega: string;
}

// Context types
export interface UserContextType {
  user: User | null;
  setUser: React.Dispatch<React.SetStateAction<User | null>>;
}

// Restock types
export interface RestockRequest {
  idRestock: number;
  idItem: number;
  nombreItem: string;
  bodega: string;
  cantidadSolicitada: number;
  estado: string;
  fechaSolicitud: string;
  fechaActualizacion: string | null;
}

export interface NewRestockRequest {
  idItem: number;
  nombreItem: string;
  bodega: string;
  cantidadSolicitada: number;
}

export interface UpdateEstadoRequest {
  estado: string;
}

// Order types
export interface Order {
  id_order: number;
  customerName: string;
  customerEmail: string;
  deliveryAddress: string;
  total: number;
  status: string;
  createdAt: string;
  idItem?: number;
  cantidadSolicitada?: number;
}

export interface NewOrder {
  customerName: string;
  customerEmail: string;
  deliveryAddress: string;
  total: number;
  idItem: number;
  cantidadSolicitada: number;
}

export interface UpdateOrderStatus {
  status: string;
}