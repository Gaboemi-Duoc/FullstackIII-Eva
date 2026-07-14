// src/api/OrdersApi.ts
import axios, { AxiosResponse } from "axios";
import { Order, NewOrder, UpdateOrderStatus } from "../types";
import { getApiUrl } from "./ApiConfig";

const ORDERS_API_URL = getApiUrl('/orders');

const getAuthHeaders = () => {
  const token = localStorage.getItem("authToken");
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  };
};

export const getOrders = async (): Promise<Order[]> => {
  const response: AxiosResponse<Order[]> = await axios.get(
    ORDERS_API_URL,
    getAuthHeaders()
  );
  return response.data;
};

export const crearOrder = async (order: NewOrder): Promise<Order> => {
  const response: AxiosResponse<Order> = await axios.post(
    ORDERS_API_URL,
    order,
    {
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("authToken")}`,
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const actualizarEstadoOrder = async (
  id: number,
  status: string
): Promise<Order> => {
  const body: UpdateOrderStatus = { status };
  const response: AxiosResponse<Order> = await axios.put(
    `${ORDERS_API_URL}/${id}/status`,
    body,
    getAuthHeaders()
  );
  return response.data;
};

export const eliminarOrder = async (id: number): Promise<void> => {
  await axios.delete(`${ORDERS_API_URL}/${id}`, getAuthHeaders());
};