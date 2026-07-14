// src/api/userApi.ts
import axios, { AxiosResponse, AxiosError } from "axios";
import type { RegisterRequest } from "../types";
import { getApiUrl } from "./ApiConfig";

const BFF_API_URL = getApiUrl('/users');

// Define the expected error response structure
export interface ApiErrorResponse {
  message?: string;
  success?: boolean;
  [key: string]: unknown;
}

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
  [key: string]: unknown;
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
  [key: string]: unknown;
}

export interface UserApiResponse {
  success: boolean;
  data: UserDetails;
  message?: string;
}

// Type guard to check if error is AxiosError with our expected response type
function isAxiosErrorWithData(error: unknown): error is AxiosError<ApiErrorResponse> {
  return axios.isAxiosError(error);
}

// Helper to extract error message
function getErrorMessage(error: unknown): string {
  if (isAxiosErrorWithData(error)) {
    // Now error.response?.data is typed as ApiErrorResponse | undefined
    return error.response?.data?.message || error.message;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return String(error);
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
  } catch (error: unknown) {
    const errorMessage = getErrorMessage(error);
    console.error("Login error:", errorMessage);
    throw error;
  }
};

export const updateUsername = async (id: number, username: string): Promise<User> => {
  try {
    const token = localStorage.getItem("authToken");
    if (!token) {
      throw new Error("No authentication token found");
    }

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
      const updatedUser: User = {
        id_user: id,
        username: username,
        token: token,
      };
      return updatedUser;
    }

    throw new Error(response.data.message || "Failed to update username");
  } catch (error: unknown) {
    const errorMessage = getErrorMessage(error);
    console.error("Update username error:", errorMessage);
    throw error;
  }
};

export const getUserDetails = async (id: number): Promise<UserDetails> => {
  try {
    const token = localStorage.getItem("authToken");
    if (!token) {
      throw new Error("No authentication token found");
    }

    const response: AxiosResponse<UserApiResponse> = await axios.get(`${BFF_API_URL}/users/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.data.success) {
      return response.data.data;
    }

    throw new Error(response.data.message || "Failed to get user details");
  } catch (error: unknown) {
    const errorMessage = getErrorMessage(error);
    console.error("Get user error:", errorMessage);
    throw error;
  }
};

export const register = async (
  data: RegisterRequest
): Promise<unknown> => {
  const response = await axios.post(
    `${BFF_API_URL}/register`,
    data
  );
  return response.data;
};