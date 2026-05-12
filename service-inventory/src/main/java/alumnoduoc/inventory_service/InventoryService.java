package alumnoduoc.inventory_service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryItem> listarItems() {
        return inventoryRepository.findAll();
    }

    public InventoryItem obtenerPorId(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado con id: " + id));
    }

    public InventoryItem buscarPorNombre(String nombre) {
        return inventoryRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado: " + nombre));
    }

    public List<InventoryItem> itemsConStockBajo(Integer umbral) {
        return inventoryRepository.findByCantidadLessThan(umbral);
    }

    public InventoryItem crearItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }

    public InventoryItem actualizarCantidad(Long id, Integer nuevaCantidad) {
        InventoryItem item = obtenerPorId(id);
        item.setCantidad(nuevaCantidad);
        return inventoryRepository.save(item);
    }

    public InventoryItem actualizarPrecio(Long id, Double nuevoPrecio) {
        InventoryItem item = obtenerPorId(id);
        item.setPrecio(nuevoPrecio);
        return inventoryRepository.save(item);
    }

    public void eliminarItem(Long id) {
        inventoryRepository.deleteById(id);
    }

    // RF13 — lista todos los ítems de una bodega específica
    public List<InventoryItem> obtenerItemsPorBodega(String bodega) {
        return inventoryRepository.findByBodega(bodega);
    }

    // RF13 — stock total por bodega en formato legible
    // Retorna: { "Bodega Central": 500, "Bodega Norte": 230 }
    public Map<String, Long> stockTotalPorBodega() {
        List<Object[]> resultados = inventoryRepository.stockTotalPorBodega();
        Map<String, Long> stockMap = new HashMap<>();
        for (Object[] fila : resultados) {
            String bodega = (String) fila[0];
            Long total = (Long) fila[1];
            stockMap.put(bodega, total);
        }
        return stockMap;
    }
}