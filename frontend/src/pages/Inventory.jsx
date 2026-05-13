import { useEffect, useState } from "react";
import {
  getItems,
  crearItem,
  eliminarItem,
  actualizarCantidad,
  actualizarPrecio,
  getStockBajo,
} from "../api/InventoryApi";

const Inventory = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const [nuevoItem, setNuevoItem] = useState({
    nombre: "",
    descripcion: "",
    cantidad: "",
    precio: "",
    bodega: "",
  });

  const [umbral, setUmbral] = useState("");

  const cargarItems = async () => {
    try {
      setLoading(true);
      const data = await getItems();
      setItems(data);
    } catch (error) {
      alert("Error cargando inventario");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarItems();
  }, []);

  const handleCrear = async (e) => {
    e.preventDefault();

    try {
      const itemParaEnviar = {
        nombre: nuevoItem.nombre,
        descripcion: nuevoItem.descripcion,
        cantidad: parseInt(nuevoItem.cantidad),
        precio: parseFloat(nuevoItem.precio),
        bodega: nuevoItem.bodega,
      };

      await crearItem(itemParaEnviar);

      alert("Item creado correctamente");

      setNuevoItem({
        nombre: "",
        descripcion: "",
        cantidad: "",
        precio: "",
        bodega: "",
      });

      cargarItems();
    } catch (error) {
      alert("Error creando item");
    }
  };

  const handleEliminar = async (id) => {
    if (!confirm("¿Seguro que deseas eliminar este item?")) return;

    try {
      await eliminarItem(id);
      alert("Item eliminado");
      cargarItems();
    } catch (error) {
      alert("Error eliminando item");
    }
  };

  const handleActualizarCantidad = async (id) => {
    const cantidad = prompt("Ingrese nueva cantidad:");

    if (cantidad === null || cantidad === "") return;

    try {
      await actualizarCantidad(id, parseInt(cantidad));
      alert("Cantidad actualizada");
      cargarItems();
    } catch (error) {
      alert("Error actualizando cantidad");
    }
  };

  const handleActualizarPrecio = async (id) => {
    const precio = prompt("Ingrese nuevo precio:");

    if (precio === null || precio === "") return;

    try {
      await actualizarPrecio(id, parseFloat(precio));
      alert("Precio actualizado");
      cargarItems();
    } catch (error) {
      alert("Error actualizando precio");
    }
  };

  const handleStockBajo = async () => {
    if (umbral === "") {
      alert("Ingresa un umbral");
      return;
    }

    try {
      setLoading(true);
      const data = await getStockBajo(parseInt(umbral));
      setItems(data);
    } catch (error) {
      alert("Error buscando stock bajo");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="inventory-page">
      <h2>Inventario</h2>

      {/* CREAR ITEM */}
      <div className="card">
        <h3>Crear nuevo item</h3>

        <form onSubmit={handleCrear}>
          <input
            type="text"
            placeholder="Nombre"
            value={nuevoItem.nombre}
            onChange={(e) =>
              setNuevoItem({ ...nuevoItem, nombre: e.target.value })
            }
            required
          />

          <input
            type="text"
            placeholder="Descripción"
            value={nuevoItem.descripcion}
            onChange={(e) =>
              setNuevoItem({ ...nuevoItem, descripcion: e.target.value })
            }
            required
          />

          <input
            type="number"
            placeholder="Cantidad"
            value={nuevoItem.cantidad}
            onChange={(e) =>
              setNuevoItem({ ...nuevoItem, cantidad: e.target.value })
            }
            required
          />

          <input
            type="number"
            step="0.01"
            placeholder="Precio"
            value={nuevoItem.precio}
            onChange={(e) =>
              setNuevoItem({ ...nuevoItem, precio: e.target.value })
            }
            required
          />

          <input
            type="text"
            placeholder="Bodega (ej: Bodega Central)"
            value={nuevoItem.bodega}
            onChange={(e) =>
              setNuevoItem({ ...nuevoItem, bodega: e.target.value })
            }
            required
          />

          <button type="submit">Crear</button>
        </form>
      </div>

      {/* STOCK BAJO */}
      <div className="card">
        <h3>Buscar stock bajo</h3>

        <input
          type="number"
          placeholder="Umbral (ej: 5)"
          value={umbral}
          onChange={(e) => setUmbral(e.target.value)}
        />

        <div className="stock-buttons">
          <button onClick={handleStockBajo}>Buscar</button>
          <button onClick={cargarItems}>Mostrar todos</button>
        </div>
      </div>

      {/* TABLA */}
      <div className="card">
        <h3>Listado de Items</h3>

        {loading ? (
          <p>Cargando...</p>
        ) : items.length === 0 ? (
          <p>No hay items</p>
        ) : (
          <table className="inventory-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Cantidad</th>
                <th>Precio</th>
                <th>Bodega</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {items.map((item) => (
                <tr key={item.id_item}>
                  <td>{item.id_item}</td>
                  <td>{item.nombre}</td>
                  <td>{item.descripcion}</td>
                  <td>{item.cantidad}</td>
                  <td>${item.precio}</td>
                  <td>{item.bodega}</td>

                  <td>
                    <button
                      className="small-btn"
                      onClick={() => handleActualizarCantidad(item.id_item)}
                    >
                      Cantidad
                    </button>

                    <button
                      className="small-btn"
                      onClick={() => handleActualizarPrecio(item.id_item)}
                    >
                      Precio
                    </button>

                    <button
                      className="small-btn danger"
                      onClick={() => handleEliminar(item.id_item)}
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

export default Inventory;