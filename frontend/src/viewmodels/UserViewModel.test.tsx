// src/viewmodels/UserViewModel.test.tsx
import { describe, it, expect, beforeEach, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { UserProvider, useUser } from "./UserViewModel";

// Pequeño componente de prueba que consume el contexto,
// ya que useUser() solo puede usarse dentro de un componente.
function Probe() {
  const { user } = useUser();
  return (
    <div>
      {user ? (
        <span>Usuario: {user.id_user}</span>
      ) : (
        <span>Sin usuario</span>
      )}
    </div>
  );
}

// Componente que llama useUser() fuera de un UserProvider,
// para probar que lanza el error esperado.
function ProbeSinProvider() {
  useUser();
  return null;
}

beforeEach(() => {
  localStorage.clear();
});

describe("UserViewModel (UserProvider / useUser)", () => {
  it("no setea usuario si no hay token ni userId en localStorage", () => {
    render(
      <UserProvider>
        <Probe />
      </UserProvider>
    );

    expect(screen.getByText("Sin usuario")).toBeInTheDocument();
  });

  it("carga el usuario desde localStorage si existen token y userId", () => {
    localStorage.setItem("authToken", "fake-jwt-token");
    localStorage.setItem("userId", "42");

    render(
      <UserProvider>
        <Probe />
      </UserProvider>
    );

    expect(screen.getByText("Usuario: 42")).toBeInTheDocument();
  });

  it("no setea usuario si falta el userId aunque exista el token", () => {
    localStorage.setItem("authToken", "fake-jwt-token");

    render(
      <UserProvider>
        <Probe />
      </UserProvider>
    );

    expect(screen.getByText("Sin usuario")).toBeInTheDocument();
  });

  it("lanza un error si useUser() se usa fuera de un UserProvider", () => {
    // Silenciamos el console.error que React tira por el throw dentro del render
    const consoleError = vi.spyOn(console, "error").mockImplementation(() => {});

    expect(() => render(<ProbeSinProvider />)).toThrow(
      "useUser must be used within a UserProvider"
    );

    consoleError.mockRestore();
  });
});