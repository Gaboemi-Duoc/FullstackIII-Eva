import axios, { AxiosResponse } from "axios";
import type { RestockRequest, NewRestockRequest, UpdateEstadoRequest } from "../types";

const RESTOCK_API_URL = "http://localhost:8081/api/bff/restock";

const getAuthHeaders = () => {
  const token = localStorage.getItem("authToken");
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  };
};

export const getSolicitudes = async (): Promise<RestockRequest[]> => {
  const response: AxiosResponse<RestockRequest[]> = await axios.get(
    RESTOCK_API_URL,
    getAuthHeaders()
  );
  return response.data;
};

export const getSolicitudById = async (id: number): Promise<RestockRequest> => {
  const response: AxiosResponse<RestockRequest> = await axios.get(
    `${RESTOCK_API_URL}/${id}`,
    getAuthHeaders()
  );
  return response.data;
};

export const getSolicitudesPorEstado = async (valor: string): Promise<RestockRequest[]> => {
  const response: AxiosResponse<RestockRequest[]> = await axios.get(
    `${RESTOCK_API_URL}/estado?valor=${valor}`,
    getAuthHeaders()
  );
  return response.data;
};

export const getResumenPorEstado = async (): Promise<Record<string, number>> => {
  const response: AxiosResponse<Record<string, number>> = await axios.get(
    `${RESTOCK_API_URL}/resumen`,
    getAuthHeaders()
  );
  return response.data;
};

export const crearSolicitud = async (solicitud: NewRestockRequest): Promise<RestockRequest> => {
  const response: AxiosResponse<RestockRequest> = await axios.post(
    RESTOCK_API_URL,
    solicitud,
    {
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("authToken")}`,
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const actualizarEstado = async (
  id: number,
  estado: string
): Promise<RestockRequest> => {
  const response: AxiosResponse<RestockRequest> = await axios.put(
    `${RESTOCK_API_URL}/${id}/estado`,
    { estado } as UpdateEstadoRequest,
    getAuthHeaders()
  );
  return response.data;
};

export const eliminarSolicitud = async (id: number): Promise<void> => {
  await axios.delete(`${RESTOCK_API_URL}/${id}`, getAuthHeaders());
};