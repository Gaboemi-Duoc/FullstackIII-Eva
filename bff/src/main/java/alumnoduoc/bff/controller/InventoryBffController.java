package alumnoduoc.bff.controller;

import alumnoduoc.bff.client.InventoryServiceClient;
import alumnoduoc.bff.model.InventoryItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff/inventory")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryBffController {

    private final InventoryServiceClient inventoryServiceClient;

    public InventoryBffController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> listarItems() {
        return ResponseEntity.ok(inventoryServiceClient.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> obtenerItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryServiceClient.getItemById(id));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventoryItem>> stockBajo(@RequestParam Integer umbral) {
        return ResponseEntity.ok(inventoryServiceClient.getStockBajo(umbral));
    }

    @PostMapping
    public ResponseEntity<InventoryItem> crearItem(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(inventoryServiceClient.crearItem(item));
    }

    @PutMapping("/{id}/cantidad")
    public ResponseEntity<InventoryItem> actualizarCantidad(@PathVariable Long id,
                                                            @RequestBody Map<String, Integer> datos) {
        return ResponseEntity.ok(inventoryServiceClient.actualizarCantidad(id, datos));
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<InventoryItem> actualizarPrecio(@PathVariable Long id,
                                                        @RequestBody Map<String, Double> datos) {
        return ResponseEntity.ok(inventoryServiceClient.actualizarPrecio(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        inventoryServiceClient.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}
