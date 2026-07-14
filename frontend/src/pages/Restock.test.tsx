// src/pages/Restock.test.tsx
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Restock from "./Restock";
import * as RestockApi from "../api/RestockApi";
import type { RestockRequest } from "../types";

// Mockeamos el módulo completo de la API: el componente no debe golpear la red real
vi.mock("../api/RestockApi");
const mockedApi = RestockApi as unknown as {
  getSolicitudes: ReturnType<typeof vi.fn>;
  crearSolicitud: ReturnType<typeof vi.fn>;
  actualizarEstado: ReturnType<typeof vi.fn>;
  eliminarSolicitud: ReturnType<typeof vi.fn>;
};

const solicitudMock: RestockRequest = {
  idRestock: 1,
  idItem: 10,
  nombreItem: "Teclado mecánico",
  bodega: "Bodega Central",
  cantidadSolicitada: 5,
  estado: "PENDIENTE",
  fechaSolicitud: "2026-07-01T10:00:00",
  fechaActualizacion: null,
};

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Restock (página)", () => {
  it("muestra 'Cargando...' mientras se obtienen las solicitudes", async () => {
    mockedApi.getSolicitudes = vi.fn().mockReturnValue(new Promise(() => {})); // nunca resuelve

    render(<Restock />);

    expect(screen.getByText("Cargando...")).toBeInTheDocument();
  });

  it("renderiza la tabla con las solicitudes obtenidas de la API", async () => {
    mockedApi.getSolicitudes = vi.fn().mockResolvedValueOnce([solicitudMock]);

    render(<Restock />);

    await waitFor(() =>
      expect(screen.getByText("Teclado mecánico")).toBeInTheDocument()
    );
    expect(screen.getByText("Bodega Central")).toBeInTheDocument();
    expect(screen.getByText("PENDIENTE")).toBeInTheDocument();
  });

  it("muestra un mensaje de error si la carga inicial falla", async () => {
    mockedApi.getSolicitudes = vi.fn().mockRejectedValueOnce(new Error("fail"));

    render(<Restock />);

    await waitFor(() =>
      expect(
        screen.getByText("Error al cargar solicitudes de restock")
      ).toBeInTheDocument()
    );
  });

  it("crea una nueva solicitud al llenar el formulario y presionar 'Crear Solicitud'", async () => {
    const user = userEvent.setup();
    mockedApi.getSolicitudes = vi.fn().mockResolvedValue([]);
    mockedApi.crearSolicitud = vi.fn().mockResolvedValueOnce(solicitudMock);

    render(<Restock />);
    await waitFor(() => expect(mockedApi.getSolicitudes).toHaveBeenCalledTimes(1));

    await user.type(screen.getByPlaceholderText("Nombre ítem"), "Teclado mecánico");
    await user.type(screen.getByPlaceholderText("Bodega"), "Bodega Central");
    await user.click(screen.getByRole("button", { name: "Crear Solicitud" }));

    await waitFor(() =>
      expect(mockedApi.crearSolicitud).toHaveBeenCalledWith(
        expect.objectContaining({
          nombreItem: "Teclado mecánico",
          bodega: "Bodega Central",
        })
      )
    );
    // Tras crear, el componente vuelve a cargar la lista
    expect(mockedApi.getSolicitudes).toHaveBeenCalledTimes(2);
  });

  it("actualiza el estado de una solicitud al seleccionar una opción del combo", async () => {
    const user = userEvent.setup();
    mockedApi.getSolicitudes = vi.fn().mockResolvedValue([solicitudMock]);
    mockedApi.actualizarEstado = vi
      .fn()
      .mockResolvedValueOnce({ ...solicitudMock, estado: "APROBADA" });

    render(<Restock />);
    await waitFor(() =>
      expect(screen.getByText("Teclado mecánico")).toBeInTheDocument()
    );

    await user.selectOptions(screen.getByRole("combobox"), "APROBADA");

    await waitFor(() =>
      expect(mockedApi.actualizarEstado).toHaveBeenCalledWith(1, "APROBADA")
    );
  });

  it("elimina una solicitud al presionar el botón 'Eliminar'", async () => {
    const user = userEvent.setup();
    mockedApi.getSolicitudes = vi.fn().mockResolvedValue([solicitudMock]);
    mockedApi.eliminarSolicitud = vi.fn().mockResolvedValueOnce(undefined);

    render(<Restock />);
    await waitFor(() =>
      expect(screen.getByText("Teclado mecánico")).toBeInTheDocument()
    );

    await user.click(screen.getByRole("button", { name: "Eliminar" }));

    await waitFor(() =>
      expect(mockedApi.eliminarSolicitud).toHaveBeenCalledWith(1)
    );
  });

  it("muestra un error si falla la actualización de estado", async () => {
    const user = userEvent.setup();
    mockedApi.getSolicitudes = vi.fn().mockResolvedValue([solicitudMock]);
    mockedApi.actualizarEstado = vi.fn().mockRejectedValueOnce(new Error("fail"));

    render(<Restock />);
    await waitFor(() =>
      expect(screen.getByText("Teclado mecánico")).toBeInTheDocument()
    );

    await user.selectOptions(screen.getByRole("combobox"), "RECHAZADA");

    await waitFor(() =>
      expect(screen.getByText("Error al actualizar estado")).toBeInTheDocument()
    );
  });
});