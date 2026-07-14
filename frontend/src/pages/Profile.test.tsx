import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Profile from "./Profile";
import * as UserViewModel from "../viewmodels/UserViewModel";
import * as userApi from "../api/userApi";

vi.mock("../viewmodels/UserViewModel");
vi.mock("../api/userApi");

const mockedUseUser = UserViewModel.useUser as unknown as ReturnType<typeof vi.fn>;
const mockedUserApi = userApi as unknown as { updateUsername: ReturnType<typeof vi.fn> };

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Profile", () => {
  it("muestra 'No hay usuario' cuando el usuario es null", () => {
    mockedUseUser.mockReturnValue({ user: null, setUser: vi.fn() });
    render(<Profile />);
    expect(screen.getByText("No hay usuario")).toBeInTheDocument();
  });

  it("muestra el username actual del usuario", () => {
    mockedUseUser.mockReturnValue({
      user: { id_user: 1, username: "isak", token: "jwt" },
      setUser: vi.fn(),
    });
    render(<Profile />);
    expect(screen.getByText("Username actual: isak")).toBeInTheDocument();
  });

  it("actualiza el username correctamente y limpia el input", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const setUser = vi.fn();
    mockedUseUser.mockReturnValue({
      user: { id_user: 1, username: "isak", token: "jwt" },
      setUser,
    });
    mockedUserApi.updateUsername = vi
      .fn()
      .mockResolvedValueOnce({ id_user: 1, username: "isak_chacana", token: "jwt" });

    const user = userEvent.setup();
    render(<Profile />);

    const input = screen.getByPlaceholderText("Nuevo username") as HTMLInputElement;
    await user.type(input, "isak_chacana");
    await user.click(screen.getByRole("button", { name: "Actualizar" }));

    await waitFor(() => expect(mockedUserApi.updateUsername).toHaveBeenCalledWith(1, "isak_chacana"));
    expect(setUser).toHaveBeenCalledWith(
      expect.objectContaining({ username: "isak_chacana" })
    );
    expect(alertSpy).toHaveBeenCalledWith("Username actualizado");
    expect(input.value).toBe("");

    alertSpy.mockRestore();
  });

  it("muestra una alerta de error si falla la actualización", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    mockedUseUser.mockReturnValue({
      user: { id_user: 1, username: "isak", token: "jwt" },
      setUser: vi.fn(),
    });
    mockedUserApi.updateUsername = vi.fn().mockRejectedValueOnce(new Error("fail"));

    const user = userEvent.setup();
    render(<Profile />);

    await user.type(screen.getByPlaceholderText("Nuevo username"), "isak_chacana");
    await user.click(screen.getByRole("button", { name: "Actualizar" }));

    await waitFor(() => expect(alertSpy).toHaveBeenCalledWith("Error al actualizar"));

    alertSpy.mockRestore();
  });
});