package alumnoduoc.bff.client;

import alumnoduoc.bff.model.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryServiceClient {

    @GetMapping("/api/inventory")
    List<Item> getAllItems();

    @GetMapping("/api/inventory/{id}")
    Item getItemById(@PathVariable("id") Long id);

    @GetMapping("/api/inventory/stock-bajo")
    List<Item> getStockBajo(@RequestParam("umbral") Integer umbral);

    @PostMapping("/api/inventory")
    Item crearItem(@RequestBody Item item);

    @PutMapping("/api/inventory/{id}/cantidad")
    Item actualizarCantidad(@PathVariable("id") Long id,
                                    @RequestBody Map<String, Integer> datos);

    @PutMapping("/api/inventory/{id}/precio")
    Item actualizarPrecio(@PathVariable("id") Long id,
                                @RequestBody Map<String, Double> datos);

    @DeleteMapping("/api/inventory/{id}")
    void eliminarItem(@PathVariable("id") Long id);
}
