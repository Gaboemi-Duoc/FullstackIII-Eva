package com.smartlogix.ms_inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.service.InventoryService;

import com.smartlogix.ms_inventory.dto.CreateItemRequest;
import com.smartlogix.ms_inventory.dto.UpdateCantidadRequest;
import com.smartlogix.ms_inventory.dto.UpdatePrecioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*") // <-- CORREGIDO
@Validated
@Tag(name = "Gestion de Inventario", description = "Operaciones CRUD para productos y control de stock")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Obtener item por id", description = "Retorna un item de inventario según su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerItem(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(inventoryService.obtenerPorId(id));
    }

    @Operation(summary = "Buscar item por nombre", description = "Busca un item de inventario según coincidencia de nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<Item> buscarPorNombre(@RequestParam @NotBlank String nombre) {
        return ResponseEntity.ok(inventoryService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Listar items con stock bajo", description = "Retorna los items cuya cantidad está por debajo del umbral indicado.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Item>> stockBajo(@RequestParam @Min(0) Integer umbral) {
        return ResponseEntity.ok(inventoryService.itemsConStockBajo(umbral));
    }

    @Operation(summary = "Crear item de inventario", description = "Registra un nuevo producto en el inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
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

    @Operation(summary = "Actualizar cantidad de un item", description = "Modifica la cantidad en stock de un item existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @PutMapping("/{id}/cantidad")
    public ResponseEntity<Item> actualizarCantidad(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateCantidadRequest request) {

        return ResponseEntity.ok(inventoryService.actualizarCantidad(id, request.getCantidad()));
    }

    @Operation(summary = "Actualizar precio de un item", description = "Modifica el precio unitario de un item existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precio actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @PutMapping("/{id}/precio")
    public ResponseEntity<Item> actualizarPrecio(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdatePrecioRequest request) {

        return ResponseEntity.ok(inventoryService.actualizarPrecio(id, request.getPrecio()));
    }

    @Operation(summary = "Listar items por bodega", description = "Retorna todos los items pertenecientes a una bodega específica.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/bodega")
    public ResponseEntity<List<Item>> itemsPorBodega(@RequestParam @NotBlank String nombre) {
        return ResponseEntity.ok(inventoryService.obtenerItemsPorBodega(nombre));
    }

    @Operation(summary = "Eliminar item", description = "Elimina un item de inventario según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable @Min(1) Long id) {
        inventoryService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}