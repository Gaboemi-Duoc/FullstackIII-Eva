package alumnoduoc.inventory_service;

import org.springframework.stereotype.Service;

import java.util.List;

// @Service → Spring registra esta clase como bean
// Toda la lógica va aquí, nunca en el controller

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Inyección por constructor — mismo patrón que UserService
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // Equivalente a listarUsuarios()
    public List<InventoryItem> listarItems() {
        return inventoryRepository.findAll();
    }

    // Equivalente a obtenerPorId() — lanza RuntimeException si no existe
    public InventoryItem obtenerPorId(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado con id: " + id));
    }

    // Busca por nombre exacto (retorna Optional → 404 si no existe)
    public InventoryItem buscarPorNombre(String nombre) {
        return inventoryRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado: " + nombre));
    }

    // Retorna todos los ítems con cantidad menor al umbral dado
    // Útil para que service-restock sepa qué productos reponer
    public List<InventoryItem> itemsConStockBajo(Integer umbral) {
        return inventoryRepository.findByCantidadLessThan(umbral);
    }

    // Equivalente a registrarUsuario() — persiste el ítem nuevo
    public InventoryItem crearItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }

    // Equivalente a actualizarUsername() — busca, modifica, guarda
    public InventoryItem actualizarCantidad(Long id, Integer nuevaCantidad) {
        InventoryItem item = obtenerPorId(id);
        item.setCantidad(nuevaCantidad);           // Lombok generó el setter
        return inventoryRepository.save(item);
    }

    public InventoryItem actualizarPrecio(Long id, Double nuevoPrecio) {
        InventoryItem item = obtenerPorId(id);
        item.setPrecio(nuevoPrecio);
        return inventoryRepository.save(item);
    }

    // Equivalente a eliminarUsuario()
    public void eliminarItem(Long id) {
        inventoryRepository.deleteById(id);
    }
}