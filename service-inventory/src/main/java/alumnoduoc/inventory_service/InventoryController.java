package alumnoduoc.inventory_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Mismo patrón que UserController:
// @RestController = responde JSON automáticamente
// @CrossOrigin    = permite llamadas desde el BFF (puerto 8081)

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:8081")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // GET /api/inventory → lista todos los ítems
    @GetMapping
    public ResponseEntity<List<InventoryItem>> listarItems() {
        return ResponseEntity.ok(inventoryService.listarItems());
    }

    // GET /api/inventory/{id} → obtiene ítem por ID
    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> obtenerItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.obtenerPorId(id));
    }

    // GET /api/inventory/buscar?nombre=Tornillo → busca por nombre
    @GetMapping("/buscar")
    public ResponseEntity<InventoryItem> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(inventoryService.buscarPorNombre(nombre));
    }

    // GET /api/inventory/stock-bajo?umbral=10 → ítems con cantidad < umbral
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventoryItem>> stockBajo(@RequestParam Integer umbral) {
        return ResponseEntity.ok(inventoryService.itemsConStockBajo(umbral));
    }

    // POST /api/inventory → crea ítem nuevo
    // Body: { "nombre": "...", "descripcion": "...", "cantidad": 100, "precio": 9.99 }
    @PostMapping
    public ResponseEntity<InventoryItem> crearItem(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(inventoryService.crearItem(item));
    }

    // PUT /api/inventory/{id}/cantidad → actualiza solo el stock
    // Mismo patrón que actualizarUsername: recibe Map para leer un campo
    // Body: { "cantidad": 50 }
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<InventoryItem> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> datos) {
        Integer nuevaCantidad = datos.get("cantidad");
        return ResponseEntity.ok(inventoryService.actualizarCantidad(id, nuevaCantidad));
    }

    // PUT /api/inventory/{id}/precio → actualiza solo el precio
    // Body: { "precio": 14.99 }
    @PutMapping("/{id}/precio")
    public ResponseEntity<InventoryItem> actualizarPrecio(
            @PathVariable Long id,
            @RequestBody Map<String, Double> datos) {
        Double nuevoPrecio = datos.get("precio");
        return ResponseEntity.ok(inventoryService.actualizarPrecio(id, nuevoPrecio));
    }

    // DELETE /api/inventory/{id} → elimina ítem, responde 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        inventoryService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}