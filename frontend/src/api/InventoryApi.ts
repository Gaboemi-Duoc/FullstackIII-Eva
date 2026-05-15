import axios, { AxiosResponse } from "axios";

const INVENTORY_API_URL = "http://localhost:8081/api/bff/inventory";

// Types
export interface InventoryItem {
  id?: number;
  name: string;
  description?: string;
  cantidad: number;
  precio: number;
  [key: string]: any;
}

export interface AuthHeaders {
  headers: {
    Authorization: string;
  };
}

const getAuthHeaders = (): AuthHeaders => {
  const token = localStorage.getItem("authToken");

  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

export const getItems = async (): Promise<InventoryItem[]> => {
  const response: AxiosResponse<InventoryItem[]> = await axios.get(
    INVENTORY_API_URL,
    getAuthHeaders()
  );
  return response.data;
};

export const getItemById = async (id: number): Promise<InventoryItem> => {
  const response: AxiosResponse<InventoryItem> = await axios.get(
    `${INVENTORY_API_URL}/${id}`,
    getAuthHeaders()
  );
  return response.data;
};

export const getStockBajo = async (umbral: number): Promise<InventoryItem[]> => {
  const response: AxiosResponse<InventoryItem[]> = await axios.get(
    `${INVENTORY_API_URL}/stock-bajo?umbral=${umbral}`,
    getAuthHeaders()
  );
  return response.data;
};

export const crearItem = async (item: Omit<InventoryItem, 'id'>): Promise<InventoryItem> => {
  const response: AxiosResponse<InventoryItem> = await axios.post(
    INVENTORY_API_URL,
    item,
    getAuthHeaders()
  );
  return response.data;
};

export const actualizarCantidad = async (id: number, cantidad: number): Promise<InventoryItem> => {
  const response: AxiosResponse<InventoryItem> = await axios.put(
    `${INVENTORY_API_URL}/${id}/cantidad`,
    { cantidad },
    getAuthHeaders()
  );
  return response.data;
};

export const actualizarPrecio = async (id: number, precio: number): Promise<InventoryItem> => {
  const response: AxiosResponse<InventoryItem> = await axios.put(
    `${INVENTORY_API_URL}/${id}/precio`,
    { precio },
    getAuthHeaders()
  );
  return response.data;
};

export const eliminarItem = async (id: number): Promise<void> => {
  const response: AxiosResponse<void> = await axios.delete(
    `${INVENTORY_API_URL}/${id}`,
    getAuthHeaders()
  );
  return response.data;
};