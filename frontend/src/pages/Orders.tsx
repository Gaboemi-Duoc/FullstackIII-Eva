import { useEffect, useState } from "react";
import {
  getOrders,
  crearOrder,
  actualizarEstadoOrder,
  eliminarOrder,
} from "../api/OrdersApi";
import { Order, NewOrder } from "../types";

const Orders = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const [newOrder, setNewOrder] = useState<NewOrder>({
    customerName: "",
    customerEmail: "",
    deliveryAddress: "",
    total: 0,
  });

  const cargarOrders = async () => {
    try {
      setLoading(true);
      const data = await getOrders();
      setOrders(data);
    } catch {
      alert("Error cargando órdenes");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarOrders();
  }, []);

  const handleCrear = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await crearOrder({
        ...newOrder,
        total: Number(newOrder.total),
      });

      alert("Orden creada correctamente");

      setNewOrder({
        customerName: "",
        customerEmail: "",
        deliveryAddress: "",
        total: 0,
      });

      cargarOrders();
    } catch {
      alert("Error creando orden");
    }
  };

  const handleActualizarEstado = async (id: number) => {
    const status = prompt(
      "Nuevo estado: CREATED, CONFIRMED, IN_PREPARATION, DISPATCHED, DELIVERED, CANCELLED"
    );

    if (!status) return;

    try {
      await actualizarEstadoOrder(id, status);
      alert("Estado actualizado");
      cargarOrders();
    } catch {
      alert("Error actualizando estado");
    }
  };

  const handleEliminar = async (id: number) => {
    if (!confirm("¿Seguro que deseas eliminar esta orden?")) return;

    try {
      await eliminarOrder(id);
      alert("Orden eliminada");
      cargarOrders();
    } catch {
      alert("Error eliminando orden");
    }
  };

  return (
    <div className="inventory-page">
      <h2>Órdenes</h2>

      <div className="card">
        <h3>Crear nueva orden</h3>

        <form onSubmit={handleCrear}>
          <input
            type="text"
            placeholder="Nombre cliente"
            value={newOrder.customerName}
            onChange={(e) =>
              setNewOrder({ ...newOrder, customerName: e.target.value })
            }
            required
          />

          <input
            type="email"
            placeholder="Correo cliente"
            value={newOrder.customerEmail}
            onChange={(e) =>
              setNewOrder({ ...newOrder, customerEmail: e.target.value })
            }
            required
          />

          <input
            type="text"
            placeholder="Dirección de entrega"
            value={newOrder.deliveryAddress}
            onChange={(e) =>
              setNewOrder({ ...newOrder, deliveryAddress: e.target.value })
            }
            required
          />

          <input
            type="number"
            placeholder="Total"
            value={newOrder.total}
            onChange={(e) =>
              setNewOrder({
                ...newOrder,
                total: parseFloat(e.target.value) || 0,
              })
            }
            required
          />

          <button type="submit">Crear orden</button>
        </form>
      </div>

      <div className="card">
        <h3>Listado de Órdenes</h3>

        {loading ? (
          <p>Cargando...</p>
        ) : orders.length === 0 ? (
          <p>No hay órdenes</p>
        ) : (
          <table className="inventory-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Email</th>
                <th>Dirección</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Fecha</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {orders.map((order) => (
                <tr key={order.id_order}>
                  <td>{order.id_order}</td>
                  <td>{order.customerName}</td>
                  <td>{order.customerEmail}</td>
                  <td>{order.deliveryAddress}</td>
                  <td>${order.total}</td>
                  <td>{order.status}</td>
                  <td>{new Date(order.createdAt).toLocaleString()}</td>
                  <td>
                    <button
                      className="small-btn"
                      onClick={() => handleActualizarEstado(order.id_order)}
                    >
                      Estado
                    </button>

                    <button
                      className="small-btn danger"
                      onClick={() => handleEliminar(order.id_order)}
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
    </div>
  );
};

export default Orders;