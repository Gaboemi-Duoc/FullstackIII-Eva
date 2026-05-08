import axios from "axios";

const BFF_API_URL = "http://localhost:8080/api/bff/users";

export const login = async (username, password) => {
  try {
    const response = await axios.post(`${BFF_API_URL}/login`, {
      username,
      password,
    });
    
    if (response.data.success) {
      // Store token in localStorage or secure storage
      localStorage.setItem('authToken', response.data.data.token);
      localStorage.setItem('userId', response.data.data.id_user);
      return response.data.data;
    }
    throw new Error(response.data.message);
  } catch (error) {
    console.error("Login error:", error.response?.data || error.message);
    throw error;
  }
};

export const updateUsername = async (id, username) => {
  try {
    const token = localStorage.getItem('authToken');
    const response = await axios.put(
      `${BFF_API_URL}/users/${id}/username`,
      { username },
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    
    if (response.data.success) {
      return response.data.data;
    }
    throw new Error(response.data.message);
  } catch (error) {
    console.error("Update username error:", error.response?.data || error.message);
    throw error;
  }
};

export const getUserDetails = async (id) => {
  try {
    const token = localStorage.getItem('authToken');
    const response = await axios.get(`${BFF_API_URL}/users/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.data.success) {
      return response.data.data;
    }
    throw new Error(response.data.message);
  } catch (error) {
    console.error("Get user error:", error.response?.data || error.message);
    throw error;
  }
};