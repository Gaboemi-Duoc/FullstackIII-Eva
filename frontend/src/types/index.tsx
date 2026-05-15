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
export interface InventoryItem {
  id_item: number;
  nombre: string;
  descripcion: string;
  cantidad: number;
  precio: number;
  bodega: string;
}

export interface NewInventoryItem {
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