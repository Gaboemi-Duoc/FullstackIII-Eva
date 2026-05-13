package alumnoduoc.inventory_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import alumnoduoc.inventory_service.model.Item;
import alumnoduoc.inventory_service.service.InventoryService;

import java.util.List;
import java.util.Map;

// Mismo patrón que UserController:
// @RestController = responde JSON automáticamente
// @CrossOrigin    = permite llamadas desde el BFF (puerto 8080)

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:8080")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // GET /api/inventory → lista todos los ítems
    @GetMapping
    public ResponseEntity<List<Item>> listarItems() {
        return ResponseEntity.ok(inventoryService.listarItems());
    }

    // GET /api/inventory/{id} → obtiene ítem por ID
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.obtenerPorId(id));
    }

    // GET /api/inventory/buscar?nombre=Tornillo → busca por nombre
    @GetMapping("/buscar")
    public ResponseEntity<Item> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(inventoryService.buscarPorNombre(nombre));
    }

    // GET /api/inventory/stock-bajo?umbral=10 → ítems con cantidad < umbral
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Item>> stockBajo(@RequestParam Integer umbral) {
        return ResponseEntity.ok(inventoryService.itemsConStockBajo(umbral));
    }

    // POST /api/inventory → crea ítem nuevo
    // Body: { "nombre": "...", "descripcion": "...", "cantidad": 100, "precio": 9.99 }
    @PostMapping
    public ResponseEntity<Item> crearItem(@RequestBody Item item) {
        return ResponseEntity.ok(inventoryService.crearItem(item));
    }

    // PUT /api/inventory/{id}/cantidad → actualiza solo el stock
    // Mismo patrón que actualizarUsername: recibe Map para leer un campo
    // Body: { "cantidad": 50 }
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<Item> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> datos) {
        Integer nuevaCantidad = datos.get("cantidad");
        return ResponseEntity.ok(inventoryService.actualizarCantidad(id, nuevaCantidad));
    }

    // PUT /api/inventory/{id}/precio → actualiza solo el precio
    // Body: { "precio": 14.99 }
    @PutMapping("/{id}/precio")
    public ResponseEntity<Item> actualizarPrecio(
            @PathVariable Long id,
            @RequestBody Map<String, Double> datos) {
        Double nuevoPrecio = datos.get("precio");
        return ResponseEntity.ok(inventoryService.actualizarPrecio(id, nuevoPrecio));
    }
    // GET /api/inventory/bodega?nombre=Bodega Central
    // → lista todos los ítems de esa bodega
    @GetMapping("/bodega")
    public ResponseEntity<List<Item>> itemsPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(inventoryService.obtenerItemsPorBodega(nombre));
    }

    // GET /api/inventory/stock-por-bodega
    // → stock total agrupado: { "Bodega Central": 500, "Bodega Norte": 230 }
    @GetMapping("/stock-por-bodega")
    public ResponseEntity<Map<String, Long>> stockPorBodega() {
        return ResponseEntity.ok(inventoryService.stockTotalPorBodega());
    }

    // DELETE /api/inventory/{id} → elimina ítem, responde 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        inventoryService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}