import axios, { AxiosResponse } from "axios";

const BFF_API_URL = "http://localhost:8081/api/bff/users";

// Types
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

export interface UpdateUsernameResponse {
  id: number;
  username: string;
  [key: string]: any;
}

export const login = async (username: string, password: string): Promise<LoginResponse> => {
  try {
    const response: AxiosResponse<LoginApiResponse> = await axios.post(`${BFF_API_URL}/login`, {
      username,
      password,
    });

    if (response.data.success) {
      localStorage.setItem("authToken", response.data.data.token);
      localStorage.setItem("userId", response.data.data.id_user.toString());
      return response.data.data;
    }

    throw new Error(response.data.message || "Login failed");
  } catch (error: any) {
    console.error("Login error:", error.response?.data || error.message);
    throw error;
  }
};

export const updateUsername = async (id: number, username: string): Promise<UpdateUsernameResponse> => {
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
      return response.data.data;
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