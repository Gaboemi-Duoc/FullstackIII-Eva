package com.smartlogix.ms_inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.service.InventoryService;
import com.smartlogix.ms_inventory.dto.CreateItemRequest;
import com.smartlogix.ms_inventory.dto.UpdateCantidadRequest;
import com.smartlogix.ms_inventory.dto.UpdatePrecioRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Controlador REST encargado de exponer las operaciones de gestión de inventario.
 * <p>
 * Provee endpoints para consultar, crear, actualizar y eliminar items de inventario,
 * así como para realizar búsquedas por nombre, bodega o nivel de stock.
 * Las rutas expuestas por este controlador están bajo el prefijo {@code /api/inventory}.
 */
@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Crea una nueva instancia del controlador de inventario.
     *
     * @param inventoryService servicio que contiene la lógica de negocio del inventario
     */
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping
    public ResponseEntity<List<Item>> listarItems() {
        return ResponseEntity.ok(inventoryService.listarItems());
    }

    /**
     * Obtiene un item de inventario a partir de su identificador único.
     *
     * @param id identificador del item, debe ser mayor o igual a 1
     * @return el item encontrado envuelto en un {@link ResponseEntity} con estado 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerItem(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(inventoryService.obtenerPorId(id));
    }

     /**
     * Busca un item de inventario a partir de una coincidencia de nombre.
     *
     * @param nombre nombre (o parte de él) del item a buscar, no puede estar en blanco
     * @return el item encontrado envuelto en un {@link ResponseEntity} con estado 200
     */
    @GetMapping("/buscar")
    public ResponseEntity<Item> buscarPorNombre(@RequestParam @NotBlank String nombre) {
        return ResponseEntity.ok(inventoryService.buscarPorNombre(nombre));
    }

        /**
     * Lista los items cuya cantidad en stock está por debajo del umbral indicado.
     *
     * @param umbral cantidad mínima de referencia (mayor o igual a 0) para considerar stock bajo
     * @return listado de items con stock bajo envuelto en un {@link ResponseEntity} con estado 200
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Item>> stockBajo(@RequestParam @Min(0) Integer umbral) {
        return ResponseEntity.ok(inventoryService.itemsConStockBajo(umbral));
    }

   /**
     * Registra un nuevo item en el inventario a partir de los datos recibidos.
     *
     * @param request datos necesarios para crear el item (nombre, descripción, cantidad, precio y bodega)
     * @return el item creado envuelto en un {@link ResponseEntity} con estado 200
     */
    @PostMapping
    public ResponseEntity<Item> crearItem(@Valid @RequestBody CreateItemRequest request) {
        Item item = new Item();
        item.setNombre(request.getNombre());
        item.setDescripcion(request.getDescripcion());
        item.setCantidad(request.getCantidad());
        item.setPrecio(request.getPrecio());
        item.setBodega(request.getBodega());
        
        return ResponseEntity.ok(inventoryService.crearItem(item));
    }

    /**
     * Actualiza la cantidad en stock de un item existente.
     *
     * @param id identificador del item a actualizar, debe ser mayor o igual a 1
     * @param request datos con la nueva cantidad a asignar
     * @return el item actualizado envuelto en un {@link ResponseEntity} con estado 200
     */
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<Item> actualizarCantidad(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateCantidadRequest request) {
        return ResponseEntity.ok(inventoryService.actualizarCantidad(id, request.getCantidad()));
    }

    /**
     * Actualiza el precio unitario de un item existente.
     *
     * @param id identificador del item a actualizar, debe ser mayor o igual a 1
     * @param request datos con el nuevo precio a asignar
     * @return el item actualizado envuelto en un {@link ResponseEntity} con estado 200
     */
    @PutMapping("/{id}/precio")
    public ResponseEntity<Item> actualizarPrecio(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdatePrecioRequest request) {
        return ResponseEntity.ok(inventoryService.actualizarPrecio(id, request.getPrecio()));
    }

    @GetMapping("/bodega")
    public ResponseEntity<List<Item>> itemsPorBodega(@RequestParam @NotBlank String nombre) {
        return ResponseEntity.ok(inventoryService.obtenerItemsPorBodega(nombre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable @Min(1) Long id) {
        inventoryService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}