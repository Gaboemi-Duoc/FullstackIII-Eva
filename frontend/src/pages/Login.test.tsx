import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Login from "./Login";
import * as userApi from "../api/userApi";
import * as UserViewModel from "../viewmodels/UserViewModel";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>();
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock("../api/userApi");
vi.mock("../viewmodels/UserViewModel");

const mockedUserApi = userApi as unknown as { login: ReturnType<typeof vi.fn> };
const mockedUseUser = UserViewModel.useUser as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
  mockedUseUser.mockReturnValue({ user: null, setUser: vi.fn() });
});

describe("Login", () => {
  it("redirige a /inventory automáticamente si ya existe un token guardado", () => {
    localStorage.setItem("authToken", "jwt-existente");
    render(<Login />);
    expect(mockNavigate).toHaveBeenCalledWith("/inventory");
  });

  it("no redirige si no hay token guardado", () => {
    render(<Login />);
    expect(mockNavigate).not.toHaveBeenCalled();
  });

  it("hace login correctamente, actualiza el usuario y navega a /inventory", async () => {
    const setUser = vi.fn();
    mockedUseUser.mockReturnValue({ user: null, setUser });
    mockedUserApi.login = vi
      .fn()
      .mockResolvedValueOnce({ id_user: 1, username: "isak", token: "jwt-real" });

    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByPlaceholderText("Usuario"), "isak");
    await user.type(screen.getByPlaceholderText("Contraseña"), "1234");
    await user.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => expect(mockedUserApi.login).toHaveBeenCalledWith("isak", "1234"));
    expect(setUser).toHaveBeenCalledWith({ id_user: 1, username: "isak", token: "jwt-real" });
    expect(mockNavigate).toHaveBeenCalledWith("/inventory");
  });

  it("muestra una alerta si el login falla y no navega", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    mockedUserApi.login = vi.fn().mockRejectedValueOnce(new Error("Credenciales inválidas"));

    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByPlaceholderText("Usuario"), "isak");
    await user.type(screen.getByPlaceholderText("Contraseña"), "wrong");
    await user.click(screen.getByRole("button", { name: "Ingresar" }));

    await waitFor(() => expect(alertSpy).toHaveBeenCalledWith("Error al iniciar sesión"));
    expect(mockNavigate).not.toHaveBeenCalledWith("/inventory");

    alertSpy.mockRestore();
  });

  it("navega a /register al presionar 'Crear cuenta'", async () => {
    const user = userEvent.setup();
    render(<Login />);
    await user.click(screen.getByRole("button", { name: "Crear cuenta" }));
    expect(mockNavigate).toHaveBeenCalledWith("/register");
  });
});