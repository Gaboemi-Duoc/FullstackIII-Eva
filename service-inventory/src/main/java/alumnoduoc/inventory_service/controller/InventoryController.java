package alumnoduoc.inventory_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import alumnoduoc.inventory_service.model.Item;
import alumnoduoc.inventory_service.service.InventoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*") // <-- CORREGIDO
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> listarItems() {
        return ResponseEntity.ok(inventoryService.listarItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Item> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(inventoryService.buscarPorNombre(nombre));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Item>> stockBajo(@RequestParam Integer umbral) {
        return ResponseEntity.ok(inventoryService.itemsConStockBajo(umbral));
    }

    @PostMapping
    public ResponseEntity<Item> crearItem(@RequestBody Item item) {
        return ResponseEntity.ok(inventoryService.crearItem(item));
    }

    @PutMapping("/{id}/cantidad")
    public ResponseEntity<Item> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> datos) {

        Integer nuevaCantidad = datos.get("cantidad");
        return ResponseEntity.ok(inventoryService.actualizarCantidad(id, nuevaCantidad));
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<Item> actualizarPrecio(
            @PathVariable Long id,
            @RequestBody Map<String, Double> datos) {

        Double nuevoPrecio = datos.get("precio");
        return ResponseEntity.ok(inventoryService.actualizarPrecio(id, nuevoPrecio));
    }

    @GetMapping("/bodega")
    public ResponseEntity<List<Item>> itemsPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(inventoryService.obtenerItemsPorBodega(nombre));
    }

    @GetMapping("/stock-por-bodega")
    public ResponseEntity<Map<String, Long>> stockPorBodega() {
        return ResponseEntity.ok(inventoryService.stockTotalPorBodega());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        inventoryService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}