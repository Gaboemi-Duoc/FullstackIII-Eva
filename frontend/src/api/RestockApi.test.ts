import { describe, it, expect, vi, beforeEach } from "vitest";
import axios from "axios";
import {
  getSolicitudes,
  getSolicitudById,
  getSolicitudesPorEstado,
  getResumenPorEstado,
  crearSolicitud,
  actualizarEstado,
  eliminarSolicitud,
} from "./RestockApi";
import type { RestockRequest, NewRestockRequest } from "../types";
 
// Mockeamos axios completo: no queremos llamadas HTTP reales en un test unitario
vi.mock("axios");
const mockedAxios = axios as unknown as {
  get: ReturnType<typeof vi.fn>;
  post: ReturnType<typeof vi.fn>;
  put: ReturnType<typeof vi.fn>;
  delete: ReturnType<typeof vi.fn>;
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
  localStorage.setItem("authToken", "fake-jwt-token");
});
 
describe("RestockApi", () => {
  it("getSolicitudes retorna la lista de solicitudes e incluye el header Authorization", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: [solicitudMock] });
 
    const result = await getSolicitudes();
 
    expect(result).toEqual([solicitudMock]);
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining("/restock"),
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: "Bearer fake-jwt-token",
        }),
      })
    );
  });
 
  it("getSolicitudById retorna una solicitud específica por id", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: solicitudMock });
 
    const result = await getSolicitudById(1);
 
    expect(result).toEqual(solicitudMock);
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining("/1"),
      expect.anything()
    );
  });
 
  it("getSolicitudesPorEstado filtra correctamente por query param", async () => {
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: [solicitudMock] });
 
    const result = await getSolicitudesPorEstado("PENDIENTE");
 
    expect(result).toEqual([solicitudMock]);
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining("estado?valor=PENDIENTE"),
      expect.anything()
    );
  });
 
  it("getResumenPorEstado retorna el conteo agrupado por estado", async () => {
    const resumen = { PENDIENTE: 3, APROBADA: 2 };
    mockedAxios.get = vi.fn().mockResolvedValueOnce({ data: resumen });
 
    const result = await getResumenPorEstado();
 
    expect(result).toEqual(resumen);
  });
 
  it("crearSolicitud envía el payload correcto y retorna la solicitud creada", async () => {
    const nueva: NewRestockRequest = {
      idItem: 10,
      nombreItem: "Teclado mecánico",
      bodega: "Bodega Central",
      cantidadSolicitada: 5,
    };
    mockedAxios.post = vi.fn().mockResolvedValueOnce({ data: solicitudMock });
 
    const result = await crearSolicitud(nueva);
 
    expect(result).toEqual(solicitudMock);
    expect(mockedAxios.post).toHaveBeenCalledWith(
      expect.stringContaining("/restock"),
      nueva,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: "Bearer fake-jwt-token",
        }),
      })
    );
  });
 
  it("actualizarEstado envía el nuevo estado en el body y hace PUT al endpoint correcto", async () => {
    const actualizada = { ...solicitudMock, estado: "APROBADA" };
    mockedAxios.put = vi.fn().mockResolvedValueOnce({ data: actualizada });
 
    const result = await actualizarEstado(1, "APROBADA");
 
    expect(result.estado).toBe("APROBADA");
    expect(mockedAxios.put).toHaveBeenCalledWith(
      expect.stringContaining("/1/estado"),
      { estado: "APROBADA" },
      expect.anything()
    );
  });
 
  it("eliminarSolicitud llama DELETE con el id correcto", async () => {
    mockedAxios.delete = vi.fn().mockResolvedValueOnce({});
 
    await eliminarSolicitud(1);
 
    expect(mockedAxios.delete).toHaveBeenCalledWith(
      expect.stringContaining("/1"),
      expect.anything()
    );
  });
 
  it("propaga el error cuando axios falla (ej. token inválido o servidor caído)", async () => {
    mockedAxios.get = vi.fn().mockRejectedValueOnce(new Error("Network Error"));
 
    await expect(getSolicitudes()).rejects.toThrow("Network Error");
  });
});
 