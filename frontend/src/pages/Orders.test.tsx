import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Orders from "./Orders";
import * as OrdersApi from "../api/OrdersApi";
import type { Order } from "../types";

vi.mock("../api/OrdersApi");
const mockedApi = OrdersApi as unknown as {
  getOrders: ReturnType<typeof vi.fn>;
  crearOrder: ReturnType<typeof vi.fn>;
  actualizarEstadoOrder: ReturnType<typeof vi.fn>;
  eliminarOrder: ReturnType<typeof vi.fn>;
};

const orderMock: Order = {
  id_order: 1,
  customerName: "Juan Perez",
  customerEmail: "juan@example.com",
  deliveryAddress: "Av. Siempre Viva 123",
  total: 25990,
  status: "PENDIENTE",
  createdAt: "2026-07-01T10:00:00",
  idItem: 1,
  cantidadSolicitada: 2,
};

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Orders", () => {
  it("carga y muestra las órdenes desde la API", async () => {
    mockedApi.getOrders = vi.fn().mockResolvedValueOnce([orderMock]);
    render(<Orders />);
    await waitFor(() => expect(screen.getByText("Juan Perez")).toBeInTheDocument());
    expect(screen.getByText("PENDIENTE")).toBeInTheDocument();
  });

  it("muestra 'No hay órdenes' cuando la lista viene vacía", async () => {
    mockedApi.getOrders = vi.fn().mockResolvedValueOnce([]);
    render(<Orders />);
    await waitFor(() => expect(screen.getByText("No hay órdenes")).toBeInTheDocument());
  });

  it("valida que el ID de item sea mayor a 0 antes de crear la orden", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const user = userEvent.setup();
    mockedApi.getOrders = vi.fn().mockResolvedValue([]);
    mockedApi.crearOrder = vi.fn();

    render(<Orders />);
    await waitFor(() => expect(mockedApi.getOrders).toHaveBeenCalledTimes(1));

    await user.type(screen.getByPlaceholderText("Nombre cliente"), "Juan Perez");
    await user.type(screen.getByPlaceholderText("Correo cliente"), "juan@example.com");
    await user.type(screen.getByPlaceholderText("Dirección de entrega"), "Av. Siempre Viva 123");
    await user.type(screen.getByPlaceholderText("Total"), "25990");
    await user.type(screen.getByPlaceholderText("Cantidad solicitada"), "2");
    await user.click(screen.getByRole("button", { name: "Crear orden" }));

    expect(alertSpy).toHaveBeenCalledWith("Debes ingresar un ID de item válido");
    expect(mockedApi.crearOrder).not.toHaveBeenCalled();

    alertSpy.mockRestore();
  });

  it("crea una orden correctamente cuando todos los datos son válidos", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const user = userEvent.setup();
    mockedApi.getOrders = vi.fn().mockResolvedValue([]);
    mockedApi.crearOrder = vi.fn().mockResolvedValueOnce(orderMock);

    render(<Orders />);
    await waitFor(() => expect(mockedApi.getOrders).toHaveBeenCalledTimes(1));

    await user.type(screen.getByPlaceholderText("Nombre cliente"), "Juan Perez");
    await user.type(screen.getByPlaceholderText("Correo cliente"), "juan@example.com");
    await user.type(screen.getByPlaceholderText("Dirección de entrega"), "Av. Siempre Viva 123");
    await user.type(screen.getByPlaceholderText("Total"), "25990");
    await user.type(screen.getByPlaceholderText("ID del item de inventario"), "1");
    const cantidadInput = screen.getByPlaceholderText("Cantidad solicitada");
    fireEvent.change(cantidadInput, { target: { value: "2" } });
    await user.click(screen.getByRole("button", { name: "Crear orden" }));

    await waitFor(() =>
      expect(mockedApi.crearOrder).toHaveBeenCalledWith(
        expect.objectContaining({ customerName: "Juan Perez", idItem: 1, cantidadSolicitada: 2 })
      )
    );
    expect(alertSpy).toHaveBeenCalledWith(
      "Orden creada correctamente. El stock fue descontado del inventario."
    );

    alertSpy.mockRestore();
  });

  it("actualiza el estado de una orden usando el valor ingresado en el prompt", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const promptSpy = vi.spyOn(window, "prompt").mockReturnValue("DISPATCHED");
    const user = userEvent.setup();
    mockedApi.getOrders = vi.fn().mockResolvedValue([orderMock]);
    mockedApi.actualizarEstadoOrder = vi
      .fn()
      .mockResolvedValueOnce({ ...orderMock, status: "DISPATCHED" });

    render(<Orders />);
    await waitFor(() => expect(screen.getByText("Juan Perez")).toBeInTheDocument());

    await user.click(screen.getByRole("button", { name: "Estado" }));

    await waitFor(() =>
      expect(mockedApi.actualizarEstadoOrder).toHaveBeenCalledWith(1, "DISPATCHED")
    );
    expect(alertSpy).toHaveBeenCalledWith("Estado actualizado");

    promptSpy.mockRestore();
    alertSpy.mockRestore();
  });

  it("elimina una orden solo si se confirma el diálogo", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const confirmSpy = vi.spyOn(window, "confirm").mockReturnValue(true);
    const user = userEvent.setup();
    mockedApi.getOrders = vi.fn().mockResolvedValue([orderMock]);
    mockedApi.eliminarOrder = vi.fn().mockResolvedValueOnce(undefined);

    render(<Orders />);
    await waitFor(() => expect(screen.getByText("Juan Perez")).toBeInTheDocument());

    await user.click(screen.getByRole("button", { name: "Eliminar" }));

    await waitFor(() => expect(mockedApi.eliminarOrder).toHaveBeenCalledWith(1));
    expect(alertSpy).toHaveBeenCalledWith("Orden eliminada");

    confirmSpy.mockRestore();
    alertSpy.mockRestore();
  });
});