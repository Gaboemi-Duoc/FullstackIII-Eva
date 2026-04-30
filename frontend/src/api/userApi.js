import axios from "axios";

const API_URL = "http://localhost:8080/api/users";

export const login = async (username, password) => {
  const response = await axios.post(`${API_URL}/login`, {
    username,
    password,
  });
  return response.data;
};

export const updateUsername = async (id, username) => {
  const response = await axios.put(`${API_URL}/${id}/username`, {
    username,
  });
  return response.data;
};