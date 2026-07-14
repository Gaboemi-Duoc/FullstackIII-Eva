import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Navbar from "./Navbar";
import * as UserViewModel from "../viewmodels/UserViewModel";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>();
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock("../viewmodels/UserViewModel");
const mockedUseUser = UserViewModel.useUser as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
});

function renderNavbar() {
  return render(
    <MemoryRouter>
      <Navbar />
    </MemoryRouter>
  );
}

describe("Navbar", () => {
  it("no muestra los links de navegación si no hay token", () => {
    mockedUseUser.mockReturnValue({ user: null, setUser: vi.fn() });
    renderNavbar();
    expect(screen.queryByText("Inventario")).not.toBeInTheDocument();
    expect(screen.queryByText("Logout")).not.toBeInTheDocument();
  });

  it("muestra los links de navegación y el avatar cuando hay token", () => {
    localStorage.setItem("authToken", "jwt-real");
    mockedUseUser.mockReturnValue({
      user: { id_user: 1, username: "isak", token: "jwt-real" },
      setUser: vi.fn(),
    });
    renderNavbar();
    expect(screen.getByText("Inventario")).toBeInTheDocument();
    expect(screen.getByText("Órdenes")).toBeInTheDocument();
    expect(screen.getByText("Restock")).toBeInTheDocument();
    expect(screen.getByText("Perfil")).toBeInTheDocument();
    expect(screen.getByText("I")).toBeInTheDocument();
  });

  it("hace logout: limpia localStorage, resetea el usuario y navega a /", async () => {
    localStorage.setItem("authToken", "jwt-real");
    localStorage.setItem("userId", "1");
    const setUser = vi.fn();
    mockedUseUser.mockReturnValue({
      user: { id_user: 1, username: "isak", token: "jwt-real" },
      setUser,
    });

    const user = userEvent.setup();
    renderNavbar();

    await user.click(screen.getByRole("button", { name: "Logout" }));

    expect(localStorage.getItem("authToken")).toBeNull();
    expect(localStorage.getItem("userId")).toBeNull();
    expect(setUser).toHaveBeenCalledWith(null);
    expect(mockNavigate).toHaveBeenCalledWith("/");
  });
});