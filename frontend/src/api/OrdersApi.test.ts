// src/api/OrdersApi.test.ts
import { describe, it, expect, vi, beforeEach } from "vitest";
import axios from "axios";
import {
  getOrders,
  crearOrder,
  actualizarEstadoOrder,
  eliminarOrder,
} from "./OrdersApi";
import type { Order, NewOrder } from "../types";

vi.mock("axios");
const mockedAxios = axios as unknown as {
  get: ReturnType<typeof vi.fn>;
  post: ReturnType<typeof vi.fn>;
  put: ReturnType<typeof vi.fn>;
  delete: ReturnType<typeof vi.fn>;
};

const orderMock: Order = {
  id_order: 1,
  customerName: "Juan Perez",
  customerEmail: "juan@example.com",
  deliveryAddress: "Av. Siempre Viva 123",
  total: 25990,
  status: "PENDIENTE",
  createdAt: "2026-07-01T10:00:00",
};

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.setItem("authToken", "fake-jwt-token");
});

describe("OrdersApi", () => {
  it("getOrders retorna la lista de pedidos con header Authorization", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: [orderMock] });

    const result = await getOrders();

    expect(result).toEqual([orderMock]);
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining("/orders"),
      expect.objectContaining({
        headers: expect.objectContaining({ Authorization: "Bearer fake-jwt-token" }),
      })
    );
  });

  it("crearOrder envia el payload correcto y retorna el pedido creado", async () => {
    const nuevo: NewOrder = {
      customerName: "Juan Perez",
      customerEmail: "juan@example.com",
      deliveryAddress: "Av. Siempre Viva 123",
      total: 25990,
      idItem: 1,
      cantidadSolicitada: 2,
    };
    mockedAxios.post = vi.fn().mockResolvedValueOnce({ data: orderMock });

    const result = await crearOrder(nuevo);

    expect(result).toEqual(orderMock);
    expect(mockedAxios.post).toHaveBeenCalledWith(
      expect.stringContaining("/orders"),
      nuevo,
      expect.anything()
    );
  });

  it("actualizarEstadoOrder hace PUT al endpoint de estado con el body correcto", async () => {
    const actualizado = { ...orderMock, status: "ENVIADO" };
    mockedAxios.put = vi.fn().mockResolvedValueOnce({ data: actualizado });

    const result = await actualizarEstadoOrder(1, "ENVIADO");

    expect(result.status).toBe("ENVIADO");
    expect(mockedAxios.put).toHaveBeenCalledWith(
      expect.stringContaining("/1/status"),
      { status: "ENVIADO" },
      expect.anything()
    );
  });

  it("eliminarOrder llama DELETE con el id correcto", async () => {
    mockedAxios.delete = vi.fn().mockResolvedValueOnce({});

    await eliminarOrder(1);

    expect(mockedAxios.delete).toHaveBeenCalledWith(expect.stringContaining("/1"), expect.anything());
  });

  it("propaga el error cuando axios falla", async () => {
    mockedAxios.get = vi.fn().mockRejectedValueOnce(new Error("Network Error"));

    await expect(getOrders()).rejects.toThrow("Network Error");
  });
});