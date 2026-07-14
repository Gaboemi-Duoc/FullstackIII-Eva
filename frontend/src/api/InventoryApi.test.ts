// src/api/InventoryApi.test.ts
import { describe, it, expect, vi, beforeEach } from "vitest";
import axios from "axios";
import {
  getItems,
  getItemById,
  getStockBajo,
  crearItem,
  actualizarCantidad,
  actualizarPrecio,
  eliminarItem,
} from "./InventoryApi";
import type { Item, NewItem } from "../types";

vi.mock("axios");
const mockedAxios = axios as unknown as {
  get: ReturnType<typeof vi.fn>;
  post: ReturnType<typeof vi.fn>;
  put: ReturnType<typeof vi.fn>;
  delete: ReturnType<typeof vi.fn>;
};

const itemMock: Item = {
  id_item: 1,
  nombre: "Teclado mecanico",
  descripcion: "Teclado retroiluminado",
  cantidad: 20,
  precio: 15990,
  bodega: "Bodega Central",
};

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.setItem("authToken", "fake-jwt-token");
});

describe("InventoryApi", () => {
  it("getItems retorna la lista de items con header Authorization", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: [itemMock] });

    const result = await getItems();

    expect(result).toEqual([itemMock]);
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining("/inventory"),
      expect.objectContaining({
        headers: expect.objectContaining({ Authorization: "Bearer fake-jwt-token" }),
      })
    );
  });

  it("getItemById retorna un item especifico", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: itemMock });

    const result = await getItemById(1);

    expect(result).toEqual(itemMock);
    expect(mockedAxios.get).toHaveBeenCalledWith(expect.stringContaining("/1"), expect.anything());
  });

  it("getStockBajo consulta el endpoint con el umbral correcto", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: [itemMock] });

    const result = await getStockBajo(5);

    expect(result).toEqual([itemMock]);
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining("stock-bajo?umbral=5"),
      expect.anything()
    );
  });

  it("crearItem envia el payload correcto y retorna el item creado", async () => {
    const nuevo: NewItem = {
      nombre: "Teclado mecanico",
      descripcion: "Teclado retroiluminado",
      cantidad: 20,
      precio: 15990,
      bodega: "Bodega Central",
    };
    mockedAxios.post = vi.fn().mockResolvedValueOnce({ data: itemMock });

    const result = await crearItem(nuevo);

    expect(result).toEqual(itemMock);
    expect(mockedAxios.post).toHaveBeenCalledWith(
      expect.stringContaining("/inventory"),
      nuevo,
      expect.anything()
    );
  });

  it("actualizarCantidad hace PUT al endpoint de cantidad con el body correcto", async () => {
    const actualizado = { ...itemMock, cantidad: 30 };
    mockedAxios.put = vi.fn().mockResolvedValueOnce({ data: actualizado });

    const result = await actualizarCantidad(1, 30);

    expect(result.cantidad).toBe(30);
    expect(mockedAxios.put).toHaveBeenCalledWith(
      expect.stringContaining("/1/cantidad"),
      { cantidad: 30 },
      expect.anything()
    );
  });

  it("actualizarPrecio hace PUT al endpoint de precio con el body correcto", async () => {
    const actualizado = { ...itemMock, precio: 19990 };
    mockedAxios.put = vi.fn().mockResolvedValueOnce({ data: actualizado });

    const result = await actualizarPrecio(1, 19990);

    expect(result.precio).toBe(19990);
    expect(mockedAxios.put).toHaveBeenCalledWith(
      expect.stringContaining("/1/precio"),
      { precio: 19990 },
      expect.anything()
    );
  });

  it("eliminarItem llama DELETE con el id correcto", async () => {
    mockedAxios.delete = vi.fn().mockResolvedValueOnce({ data: undefined });

    await eliminarItem(1);

    expect(mockedAxios.delete).toHaveBeenCalledWith(expect.stringContaining("/1"), expect.anything());
  });

  it("propaga el error cuando axios falla", async () => {
    mockedAxios.get = vi.fn().mockRejectedValueOnce(new Error("Network Error"));

    await expect(getItems()).rejects.toThrow("Network Error");
  });
});