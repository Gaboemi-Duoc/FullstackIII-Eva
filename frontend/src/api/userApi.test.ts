// src/api/userApi.test.ts
import { describe, it, expect, vi, beforeEach } from "vitest";
import axios from "axios";
import {
  login,
  updateUsername,
  getUserDetails,
  register,
} from "./userApi";
import type { RegisterRequest } from "../types";

vi.mock("axios");
const mockedAxios = axios as unknown as {
  get: ReturnType<typeof vi.fn>;
  post: ReturnType<typeof vi.fn>;
  put: ReturnType<typeof vi.fn>;
  isAxiosError: ReturnType<typeof vi.fn>;
};

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
  mockedAxios.isAxiosError = vi.fn().mockReturnValue(false);
});

describe("userApi - login", () => {
  it("guarda el token y el userId en localStorage cuando el login es exitoso", async () => {
    mockedAxios.post = vi.fn().mockResolvedValueOnce({
      data: {
        success: true,
        data: { id_user: 7, username: "isak", token: "jwt-real" },
      },
    });

    const user = await login("isak", "1234");

    expect(user).toEqual({ id_user: 7, username: "isak", token: "jwt-real" });
    expect(localStorage.getItem("authToken")).toBe("jwt-real");
    expect(localStorage.getItem("userId")).toBe("7");
  });

  it("lanza un error si success es false en la respuesta", async () => {
    mockedAxios.post = vi.fn().mockResolvedValueOnce({
      data: { success: false, message: "Credenciales invalidas", data: {} },
    });

    await expect(login("isak", "wrong")).rejects.toThrow("Credenciales invalidas");
    // No debe guardar nada en localStorage si el login falla
    expect(localStorage.getItem("authToken")).toBeNull();
  });

  it("propaga el error si axios falla (ej. servidor caido)", async () => {
    mockedAxios.post = vi.fn().mockRejectedValueOnce(new Error("Network Error"));

    await expect(login("isak", "1234")).rejects.toThrow("Network Error");
  });
});

describe("userApi - updateUsername", () => {
  it("actualiza el username cuando hay token valido", async () => {
    localStorage.setItem("authToken", "jwt-real");
    mockedAxios.put = vi.fn().mockResolvedValueOnce({ data: { success: true, data: {} } });

    const result = await updateUsername(7, "isak_chacana");

    expect(result).toEqual({ id_user: 7, username: "isak_chacana", token: "jwt-real" });
    expect(mockedAxios.put).toHaveBeenCalledWith(
      expect.stringContaining("/users/7/username"),
      { username: "isak_chacana" },
      expect.objectContaining({
        headers: expect.objectContaining({ Authorization: "Bearer jwt-real" }),
      })
    );
  });

  it("lanza un error si no hay token en localStorage", async () => {
    await expect(updateUsername(7, "isak_chacana")).rejects.toThrow(
      "No authentication token found"
    );
    expect(mockedAxios.put).not.toHaveBeenCalled();
  });
});

describe("userApi - getUserDetails", () => {
  it("retorna los detalles del usuario cuando hay token valido", async () => {
    localStorage.setItem("authToken", "jwt-real");
    const detalles = { id: 7, username: "isak", email: "isak@duocuc.cl" };
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: { success: true, data: detalles } });

    const result = await getUserDetails(7);

    expect(result).toEqual(detalles);
  });

  it("lanza un error si no hay token en localStorage", async () => {
    await expect(getUserDetails(7)).rejects.toThrow("No authentication token found");
  });
});

describe("userApi - register", () => {
  it("envia el payload de registro correctamente", async () => {
    const nuevo: RegisterRequest = {
      username: "isak",
      email: "isak@duocuc.cl",
      password: "1234",
    };
    mockedAxios.post = vi.fn().mockResolvedValueOnce({ data: { success: true } });

    const result = await register(nuevo);

    expect(result).toEqual({ success: true });
    expect(mockedAxios.post).toHaveBeenCalledWith(
      expect.stringContaining("/register"),
      nuevo
    );
  });
});