import axios from "axios";

const INVENTORY_API_URL =
  "http://localhost:8080/api/bff/inventory";

// Obtener token
const getAuthHeaders = () => {
  const token = localStorage.getItem("authToken");

  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

// Obtener todos los items
export const getItems = async () => {
  try {
    const response = await axios.get(
      INVENTORY_API_URL,
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error getting inventory items:",
      error.response?.data || error.message
    );

    throw error;
  }
};

// Obtener item por ID
export const getItemById = async (id) => {
  try {
    const response = await axios.get(
      `${INVENTORY_API_URL}/${id}`,
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error getting inventory item:",
      error.response?.data || error.message
    );

    throw error;
  }
};

// Obtener stock bajo
export const getStockBajo = async (umbral) => {
  try {
    const response = await axios.get(
      `${INVENTORY_API_URL}/stock-bajo?umbral=${umbral}`,
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error getting low stock items:",
      error.response?.data || error.message
    );

    throw error;
  }
};

// Crear item
export const crearItem = async (item) => {
  try {
    const response = await axios.post(
      INVENTORY_API_URL,
      item,
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error creating item:",
      error.response?.data || error.message
    );

    throw error;
  }
};

// Actualizar cantidad
export const actualizarCantidad = async (
  id,
  cantidad
) => {
  try {
    const response = await axios.put(
      `${INVENTORY_API_URL}/${id}/cantidad`,
      { cantidad },
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error updating quantity:",
      error.response?.data || error.message
    );

    throw error;
  }
};

// Actualizar precio
export const actualizarPrecio = async (
  id,
  precio
) => {
  try {
    const response = await axios.put(
      `${INVENTORY_API_URL}/${id}/precio`,
      { precio },
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error updating price:",
      error.response?.data || error.message
    );

    throw error;
  }
};

// Eliminar item
export const eliminarItem = async (id) => {
  try {
    const response = await axios.delete(
      `${INVENTORY_API_URL}/${id}`,
      getAuthHeaders()
    );

    return response.data;
  } catch (error) {
    console.error(
      "Error deleting item:",
      error.response?.data || error.message
    );

    throw error;
  }
};