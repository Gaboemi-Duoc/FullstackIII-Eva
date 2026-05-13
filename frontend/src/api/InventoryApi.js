import axios from "axios";

const INVENTORY_API_URL = "http://localhost:8081/api/bff/inventory";

const getAuthHeaders = () => {
  const token = localStorage.getItem("authToken");

  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

export const getItems = async () => {
  const response = await axios.get(INVENTORY_API_URL, getAuthHeaders());
  return response.data;
};

export const getItemById = async (id) => {
  const response = await axios.get(`${INVENTORY_API_URL}/${id}`, getAuthHeaders());
  return response.data;
};

export const getStockBajo = async (umbral) => {
  const response = await axios.get(
    `${INVENTORY_API_URL}/stock-bajo?umbral=${umbral}`,
    getAuthHeaders()
  );
  return response.data;
};

export const crearItem = async (item) => {
  const response = await axios.post(INVENTORY_API_URL, item, getAuthHeaders());
  return response.data;
};

export const actualizarCantidad = async (id, cantidad) => {
  const response = await axios.put(
    `${INVENTORY_API_URL}/${id}/cantidad`,
    { cantidad },
    getAuthHeaders()
  );
  return response.data;
};

export const actualizarPrecio = async (id, precio) => {
  const response = await axios.put(
    `${INVENTORY_API_URL}/${id}/precio`,
    { precio },
    getAuthHeaders()
  );
  return response.data;
};

export const eliminarItem = async (id) => {
  const response = await axios.delete(`${INVENTORY_API_URL}/${id}`, getAuthHeaders());
  return response.data;
};