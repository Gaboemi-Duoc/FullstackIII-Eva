package alumnoduoc.bff.client;

import alumnoduoc.bff.model.InventoryItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryServiceClient {

    @GetMapping("/api/inventory")
    List<InventoryItem> getAllItems();

    @GetMapping("/api/inventory/{id}")
    InventoryItem getItemById(@PathVariable("id") Long id);

    @GetMapping("/api/inventory/stock-bajo")
    List<InventoryItem> getStockBajo(@RequestParam("umbral") Integer umbral);

    @PostMapping("/api/inventory")
    InventoryItem crearItem(@RequestBody InventoryItem item);

    @PutMapping("/api/inventory/{id}/cantidad")
    InventoryItem actualizarCantidad(@PathVariable("id") Long id,
                                    @RequestBody Map<String, Integer> datos);

    @PutMapping("/api/inventory/{id}/precio")
    InventoryItem actualizarPrecio(@PathVariable("id") Long id,
                                @RequestBody Map<String, Double> datos);

    @DeleteMapping("/api/inventory/{id}")
    void eliminarItem(@PathVariable("id") Long id);
}
