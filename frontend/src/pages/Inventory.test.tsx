import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Inventory from "./Inventory";
import * as InventoryApi from "../api/InventoryApi";
import type { Item } from "../types";

vi.mock("../api/InventoryApi");
const mockedApi = InventoryApi as unknown as {
  getItems: ReturnType<typeof vi.fn>;
  crearItem: ReturnType<typeof vi.fn>;
  eliminarItem: ReturnType<typeof vi.fn>;
  actualizarCantidad: ReturnType<typeof vi.fn>;
  actualizarPrecio: ReturnType<typeof vi.fn>;
  getStockBajo: ReturnType<typeof vi.fn>;
};

const itemMock: Item = {
  id_item: 1,
  nombre: "Teclado mecánico",
  descripcion: "Teclado retroiluminado",
  cantidad: 20,
  precio: 15990,
  bodega: "Bodega Central",
};

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Inventory", () => {
  it("carga y muestra los items desde la API", async () => {
    mockedApi.getItems = vi.fn().mockResolvedValueOnce([itemMock]);
    render(<Inventory />);
    await waitFor(() => expect(screen.getByText("Teclado mecánico")).toBeInTheDocument());
    expect(screen.getByText("Bodega Central")).toBeInTheDocument();
  });

  it("muestra 'No hay items' cuando la lista viene vacía", async () => {
    mockedApi.getItems = vi.fn().mockResolvedValueOnce([]);
    render(<Inventory />);
    await waitFor(() => expect(screen.getByText("No hay items")).toBeInTheDocument());
  });

  it("crea un nuevo item al llenar el formulario y enviarlo", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const user = userEvent.setup();
    mockedApi.getItems = vi.fn().mockResolvedValue([]);
    mockedApi.crearItem = vi.fn().mockResolvedValueOnce(itemMock);

    render(<Inventory />);
    await waitFor(() => expect(mockedApi.getItems).toHaveBeenCalledTimes(1));

    await user.type(screen.getByPlaceholderText("Nombre"), "Teclado mecánico");
    await user.type(screen.getByPlaceholderText("Descripción"), "Teclado retroiluminado");
    await user.type(screen.getByPlaceholderText("Cantidad"), "20");
    await user.type(screen.getByPlaceholderText("Precio"), "15990");
    await user.type(screen.getByPlaceholderText("Bodega (ej: Bodega Central)"), "Bodega Central");
    await user.click(screen.getByRole("button", { name: "Crear" }));

    await waitFor(() =>
      expect(mockedApi.crearItem).toHaveBeenCalledWith(
        expect.objectContaining({ nombre: "Teclado mecánico", cantidad: 20, precio: 15990 })
      )
    );
    expect(alertSpy).toHaveBeenCalledWith("Item creado correctamente");

    alertSpy.mockRestore();
  });

  it("elimina un item solo si se confirma el diálogo", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const confirmSpy = vi.spyOn(window, "confirm").mockReturnValue(true);
    const user = userEvent.setup();
    mockedApi.getItems = vi.fn().mockResolvedValue([itemMock]);
    mockedApi.eliminarItem = vi.fn().mockResolvedValueOnce(undefined);

    render(<Inventory />);
    await waitFor(() => expect(screen.getByText("Teclado mecánico")).toBeInTheDocument());

    await user.click(screen.getByRole("button", { name: "Eliminar" }));

    expect(confirmSpy).toHaveBeenCalled();
    await waitFor(() => expect(mockedApi.eliminarItem).toHaveBeenCalledWith(1));
    expect(alertSpy).toHaveBeenCalledWith("Item eliminado");

    confirmSpy.mockRestore();
    alertSpy.mockRestore();
  });

  it("NO elimina el item si se cancela el diálogo de confirmación", async () => {
    const confirmSpy = vi.spyOn(window, "confirm").mockReturnValue(false);
    const user = userEvent.setup();
    mockedApi.getItems = vi.fn().mockResolvedValue([itemMock]);
    mockedApi.eliminarItem = vi.fn();

    render(<Inventory />);
    await waitFor(() => expect(screen.getByText("Teclado mecánico")).toBeInTheDocument());

    await user.click(screen.getByRole("button", { name: "Eliminar" }));

    expect(mockedApi.eliminarItem).not.toHaveBeenCalled();

    confirmSpy.mockRestore();
  });

  it("actualiza la cantidad de un item usando el valor ingresado en el prompt", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    const promptSpy = vi.spyOn(window, "prompt").mockReturnValue("50");
    const user = userEvent.setup();
    mockedApi.getItems = vi.fn().mockResolvedValue([itemMock]);
    mockedApi.actualizarCantidad = vi.fn().mockResolvedValueOnce({ ...itemMock, cantidad: 50 });

    render(<Inventory />);
    await waitFor(() => expect(screen.getByText("Teclado mecánico")).toBeInTheDocument());

    await user.click(screen.getByRole("button", { name: "Cantidad" }));

    await waitFor(() => expect(mockedApi.actualizarCantidad).toHaveBeenCalledWith(1, 50));
    expect(alertSpy).toHaveBeenCalledWith("Cantidad actualizada");

    promptSpy.mockRestore();
    alertSpy.mockRestore();
  });

  it("busca items con stock bajo usando el umbral ingresado", async () => {
    const user = userEvent.setup();
    mockedApi.getItems = vi.fn().mockResolvedValue([itemMock]);
    mockedApi.getStockBajo = vi.fn().mockResolvedValueOnce([itemMock]);

    render(<Inventory />);
    await waitFor(() => expect(screen.getByText("Teclado mecánico")).toBeInTheDocument());

    await user.type(screen.getByPlaceholderText("Umbral (ej: 5)"), "5");
    await user.click(screen.getByRole("button", { name: "Buscar" }));

    await waitFor(() => expect(mockedApi.getStockBajo).toHaveBeenCalledWith(5));
  });
});