import { describe, it, expect, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";

beforeEach(() => {
  localStorage.clear();
});

function renderWithRouter() {
  return render(
    <MemoryRouter initialEntries={["/inventory"]}>
      <Routes>
        <Route path="/" element={<p>Página de login</p>} />
        <Route
          path="/inventory"
          element={
            <ProtectedRoute>
              <p>Contenido protegido</p>
            </ProtectedRoute>
          }
        />
      </Routes>
    </MemoryRouter>
  );
}

describe("ProtectedRoute", () => {
  it("redirige a '/' si no hay token en localStorage", () => {
    renderWithRouter();
    expect(screen.getByText("Página de login")).toBeInTheDocument();
    expect(screen.queryByText("Contenido protegido")).not.toBeInTheDocument();
  });

  it("renderiza el contenido protegido si existe un token", () => {
    localStorage.setItem("authToken", "jwt-real");
    renderWithRouter();
    expect(screen.getByText("Contenido protegido")).toBeInTheDocument();
    expect(screen.queryByText("Página de login")).not.toBeInTheDocument();
  });
});