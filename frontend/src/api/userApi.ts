import axios, { AxiosResponse } from "axios";

const BFF_API_URL = "http://localhost:8081/api/bff/users";

// Types
export interface User {
  id_user: number;
  username: string;
  token: string;
}

export interface LoginResponse {
  token: string;
  id_user: number;
  username?: string;
  [key: string]: any;
}

export interface LoginApiResponse {
  success: boolean;
  data: LoginResponse;
  message?: string;
}

export interface UserDetails {
  id: number;
  username: string;
  email?: string;
  [key: string]: any;
}

export interface UserApiResponse {
  success: boolean;
  data: UserDetails;
  message?: string;
}

export const login = async (username: string, password: string): Promise<User> => {
  try {
    const response: AxiosResponse<LoginApiResponse> = await axios.post(`${BFF_API_URL}/login`, {
      username,
      password,
    });

    if (response.data.success) {
      const userData: User = {
        id_user: response.data.data.id_user,
        username: response.data.data.username || username,
        token: response.data.data.token,
      };
      localStorage.setItem("authToken", userData.token);
      localStorage.setItem("userId", userData.id_user.toString());
      return userData;
    }

    throw new Error(response.data.message || "Login failed");
  } catch (error: any) {
    console.error("Login error:", error.response?.data || error.message);
    throw error;
  }
};

export const updateUsername = async (id: number, username: string): Promise<User> => {
  try {
    const token = localStorage.getItem("authToken");

    const response: AxiosResponse<UserApiResponse> = await axios.put(
      `${BFF_API_URL}/users/${id}/username`,
      { username },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (response.data.success) {
      // Return updated user object matching the User interface
      const updatedUser: User = {
        id_user: id,
        username: username,
        token: token || "",
      };
      return updatedUser;
    }

    throw new Error(response.data.message || "Failed to update username");
  } catch (error: any) {
    console.error("Update username error:", error.response?.data || error.message);
    throw error;
  }
};

export const getUserDetails = async (id: number): Promise<UserDetails> => {
  try {
    const token = localStorage.getItem("authToken");

    const response: AxiosResponse<UserApiResponse> = await axios.get(`${BFF_API_URL}/users/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.data.success) {
      return response.data.data;
    }

    throw new Error(response.data.message || "Failed to get user details");
  } catch (error: any) {
    console.error("Get user error:", error.response?.data || error.message);
    throw error;
  }
};