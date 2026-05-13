package alumnoduoc.inventory_service.service;

import org.springframework.stereotype.Service;

import alumnoduoc.inventory_service.model.Item;
import alumnoduoc.inventory_service.repository.InventoryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Item> listarItems() {
        return inventoryRepository.findAll();
    }

    public Item obtenerPorId(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado con id: " + id));
    }

    public Item buscarPorNombre(String nombre) {
        return inventoryRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado: " + nombre));
    }

    public List<Item> itemsConStockBajo(Integer umbral) {
        return inventoryRepository.findByCantidadLessThan(umbral);
    }

    public Item crearItem(Item item) {
        return inventoryRepository.save(item);
    }

    public Item actualizarCantidad(Long id, Integer nuevaCantidad) {
        Item item = obtenerPorId(id);
        item.setCantidad(nuevaCantidad);
        return inventoryRepository.save(item);
    }

    public Item actualizarPrecio(Long id, Double nuevoPrecio) {
        Item item = obtenerPorId(id);
        item.setPrecio(nuevoPrecio);
        return inventoryRepository.save(item);
    }

    public void eliminarItem(Long id) {
        inventoryRepository.deleteById(id);
    }

    // RF13 — lista todos los ítems de una bodega específica
    public List<Item> obtenerItemsPorBodega(String bodega) {
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
