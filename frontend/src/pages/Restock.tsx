import { useEffect, useState } from "react";
import {
  getSolicitudes,
  crearSolicitud,
  actualizarEstado,
  eliminarSolicitud,
} from "../api/RestockApi";
import type { RestockRequest, NewRestockRequest } from "../types";

export default function Restock() {
  const [solicitudes, setSolicitudes] = useState<RestockRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form nueva solicitud
  const [form, setForm] = useState<NewRestockRequest>({
    id_item: 0,
    nombre_item: "",
    bodega: "",
    cantidad_solicitada: 1,
  });

  const cargarSolicitudes = async () => {
    try {
      setLoading(true);
      const data = await getSolicitudes();
      setSolicitudes(data);
      setError(null);
    } catch {
      setError("Error al cargar solicitudes de restock");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarSolicitudes();
  }, []);

  const handleCrear = async () => {
    try {
      await crearSolicitud(form);
      setForm({ id_item: 0, nombre_item: "", bodega: "", cantidad_solicitada: 1 });
      await cargarSolicitudes();
    } catch {
      setError("Error al crear solicitud");
    }
  };

  const handleActualizarEstado = async (id: number, nuevoEstado: string) => {
    try {
      await actualizarEstado(id, nuevoEstado);
      await cargarSolicitudes();
    } catch {
      setError("Error al actualizar estado");
    }
  };

  const handleEliminar = async (id: number) => {
    try {
      await eliminarSolicitud(id);
      await cargarSolicitudes();
    } catch {
      setError("Error al eliminar solicitud");
    }
  };

  const getBadgeColor = (estado: string) => {
    switch (estado) {
      case "PENDIENTE":   return "#f59e0b";
      case "APROBADA":    return "#10b981";
      case "RECHAZADA":   return "#ef4444";
      case "COMPLETADA":  return "#3b82f6";
      default:            return "#6b7280";
    }
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h1>Gestión de Restock</h1>

      {error && (
        <div style={{ color: "red", marginBottom: "1rem" }}>{error}</div>
      )}

      {/* Formulario nueva solicitud */}
      <div style={{ marginBottom: "2rem", padding: "1rem", border: "1px solid #e5e7eb", borderRadius: "8px" }}>
        <h2>Nueva Solicitud</h2>
        <div style={{ display: "flex", gap: "0.5rem", flexWrap: "wrap" }}>
          <input
            type="number"
            placeholder="ID Ítem"
            value={form.id_item}
            onChange={(e) => setForm({ ...form, id_item: Number(e.target.value) })}
            style={{ padding: "0.5rem", borderRadius: "4px", border: "1px solid #d1d5db" }}
          />
          <input
            type="text"
            placeholder="Nombre ítem"
            value={form.nombre_item}
            onChange={(e) => setForm({ ...form, nombre_item: e.target.value })}
            style={{ padding: "0.5rem", borderRadius: "4px", border: "1px solid #d1d5db" }}
          />
          <input
            type="text"
            placeholder="Bodega"
            value={form.bodega}
            onChange={(e) => setForm({ ...form, bodega: e.target.value })}
            style={{ padding: "0.5rem", borderRadius: "4px", border: "1px solid #d1d5db" }}
          />
          <input
            type="number"
            placeholder="Cantidad"
            value={form.cantidad_solicitada}
            min={1}
            onChange={(e) => setForm({ ...form, cantidad_solicitada: Number(e.target.value) })}
            style={{ padding: "0.5rem", borderRadius: "4px", border: "1px solid #d1d5db" }}
          />
          <button
            onClick={handleCrear}
            style={{ padding: "0.5rem 1rem", backgroundColor: "#3b82f6", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}
          >
            Crear Solicitud
          </button>
        </div>
      </div>

      {/* Tabla de solicitudes */}
      {loading ? (
        <p>Cargando...</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ backgroundColor: "#f3f4f6" }}>
              <th style={{ padding: "0.75rem", textAlign: "left", border: "1px solid #e5e7eb" }}>ID</th>
              <th style={{ padding: "0.75rem", textAlign: "left", border: "1px solid #e5e7eb" }}>Ítem</th>
              <th style={{ padding: "0.75rem", textAlign: "left", border: "1px solid #e5e7eb" }}>Bodega</th>
              <th style={{ padding: "0.75rem", textAlign: "left", border: "1px solid #e5e7eb" }}>Cantidad</th>
              <th style={{ padding: "0.75rem", textAlign: "left", border: "1px solid #e5e7eb" }}>Estado</th>
              <th style={{ padding: "0.75rem", textAlign: "left", border: "1px solid #e5e7eb" }}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {solicitudes.map((s) => (
              <tr key={s.id_restock}>
                <td style={{ padding: "0.75rem", border: "1px solid #e5e7eb" }}>{s.id_restock}</td>
                <td style={{ padding: "0.75rem", border: "1px solid #e5e7eb" }}>{s.nombre_item}</td>
                <td style={{ padding: "0.75rem", border: "1px solid #e5e7eb" }}>{s.bodega}</td>
                <td style={{ padding: "0.75rem", border: "1px solid #e5e7eb" }}>{s.cantidad_solicitada}</td>
                <td style={{ padding: "0.75rem", border: "1px solid #e5e7eb" }}>
                  <span style={{
                    backgroundColor: getBadgeColor(s.estado),
                    color: "white",
                    padding: "0.25rem 0.5rem",
                    borderRadius: "4px",
                    fontSize: "0.875rem"
                  }}>
                    {s.estado}
                  </span>
                </td>
                <td style={{ padding: "0.75rem", border: "1px solid #e5e7eb" }}>
                  <select
                    onChange={(e) => handleActualizarEstado(s.id_restock, e.target.value)}
                    defaultValue=""
                    style={{ marginRight: "0.5rem", padding: "0.25rem" }}
                  >
                    <option value="" disabled>Cambiar estado</option>
                    <option value="APROBADA">APROBADA</option>
                    <option value="RECHAZADA">RECHAZADA</option>
                    <option value="COMPLETADA">COMPLETADA</option>
                  </select>
                  <button
                    onClick={() => handleEliminar(s.id_restock)}
                    style={{ padding: "0.25rem 0.5rem", backgroundColor: "#ef4444", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}