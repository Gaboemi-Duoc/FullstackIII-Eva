import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Register from "./Register";
import * as userApi from "../api/userApi";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>();
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock("../api/userApi");
const mockedUserApi = userApi as unknown as { register: ReturnType<typeof vi.fn> };

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Register", () => {
  it("registra un usuario correctamente y navega a la raíz", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    mockedUserApi.register = vi.fn().mockResolvedValueOnce({ success: true });

    const user = userEvent.setup();
    render(<Register />);

    await user.type(screen.getByPlaceholderText("Usuario"), "isak");
    await user.type(screen.getByPlaceholderText("Correo"), "isak@duocuc.cl");
    await user.type(screen.getByPlaceholderText("Contraseña"), "1234");
    await user.click(screen.getByRole("button", { name: "Registrarse" }));

    await waitFor(() =>
      expect(mockedUserApi.register).toHaveBeenCalledWith({
        username: "isak",
        email: "isak@duocuc.cl",
        password: "1234",
      })
    );
    expect(alertSpy).toHaveBeenCalledWith("Usuario registrado correctamente");
    expect(mockNavigate).toHaveBeenCalledWith("/");

    alertSpy.mockRestore();
  });

  it("muestra una alerta de error si el registro falla y no navega", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    mockedUserApi.register = vi.fn().mockRejectedValueOnce(new Error("Usuario ya existe"));

    const user = userEvent.setup();
    render(<Register />);

    await user.type(screen.getByPlaceholderText("Usuario"), "isak");
    await user.type(screen.getByPlaceholderText("Correo"), "isak@duocuc.cl");
    await user.type(screen.getByPlaceholderText("Contraseña"), "1234");
    await user.click(screen.getByRole("button", { name: "Registrarse" }));

    await waitFor(() => expect(alertSpy).toHaveBeenCalledWith("Error al registrar usuario"));
    expect(mockNavigate).not.toHaveBeenCalled();

    alertSpy.mockRestore();
  });
});