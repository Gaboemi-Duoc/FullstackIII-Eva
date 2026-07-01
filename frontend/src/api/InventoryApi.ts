import axios, { AxiosResponse } from "axios";
import type { Item, NewItem } from "../types";

const INVENTORY_API_URL = "http://localhost:8081/api/bff/inventory";

const getAuthHeaders = () => {
  const token = localStorage.getItem("authToken");

  return {
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  };
};

export const getItems = async (): Promise<Item[]> => {
  const response: AxiosResponse<Item[]> = await axios.get(
    INVENTORY_API_URL,
    getAuthHeaders()
  );
  return response.data;
};

export const getItemById = async (id: number): Promise<Item> => {
  const response: AxiosResponse<Item> = await axios.get(
    `${INVENTORY_API_URL}/${id}`,
    getAuthHeaders()
  );
  return response.data;
};

export const getStockBajo = async (umbral: number): Promise<Item[]> => {
  const response: AxiosResponse<Item[]> = await axios.get(
    `${INVENTORY_API_URL}/stock-bajo?umbral=${umbral}`,
    getAuthHeaders()
  );
  return response.data;
};

export const crearItem = async (item: NewItem): Promise<Item> => {
  const response: AxiosResponse<Item> = await axios.post(
    INVENTORY_API_URL,
    item,
    {
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("authToken")}`,
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const actualizarCantidad = async (
  id: number,
  cantidad: number
): Promise<Item> => {
  const response: AxiosResponse<Item> = await axios.put(
    `${INVENTORY_API_URL}/${id}/cantidad`,
    { cantidad },
    getAuthHeaders()
  );
  return response.data;
};

export const actualizarPrecio = async (
  id: number,
  precio: number
): Promise<Item> => {
  const response: AxiosResponse<Item> = await axios.put(
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