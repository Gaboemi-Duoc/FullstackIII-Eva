package com.smartlogix.ms_orders.controller;

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.dto.UpdateOrderStatusRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de exponer las operaciones de gestión de órdenes.
 * <p>
 * Provee endpoints para listar, consultar, crear, actualizar el estado y eliminar
 * órdenes de pedido. Las rutas expuestas por este controlador están bajo el
 * prefijo {@code /api/orders}.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion de Pedidos", description = "Operaciones CRUD y de seguimiento de órdenes")
public class OrderController {

    private final OrderService orderService;

    /**
     * Crea una nueva instancia del controlador de órdenes.
     *
     * @param orderService servicio que contiene la lógica de negocio de las órdenes
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Lista todas las órdenes registradas en el sistema.
     *
     * @return listado completo de órdenes envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Listar todas las órdenes", description = "Retorna el listado completo de órdenes registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<Order>> listarOrdenes() {
        return ResponseEntity.ok(orderService.listarOrdenes());
    }

    /**
     * Obtiene una orden a partir de su identificador único.
     *
     * @param id identificador de la orden
     * @return la orden encontrada envuelta en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Obtener orden por id", description = "Retorna una orden según su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> obtenerOrden(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.obtenerPorId(id));
    }

    /**
     * Registra una nueva orden de pedido a partir de los datos recibidos.
     *
     * @param request datos necesarios para crear la orden
     * @return la orden creada envuelta en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Crear orden", description = "Registra una nueva orden de pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<Order> crearOrden(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(orderService.crearOrden(request));
    }

    /**
     * Actualiza el estado de una orden existente (ej. PENDIENTE, ENVIADA, ENTREGADA).
     *
     * @param id identificador de la orden a actualizar
     * @param request datos con el nuevo estado a asignar
     * @return la orden actualizada envuelta en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Actualizar estado de una orden", description = "Cambia el estado de una orden existente (ej. PENDIENTE, ENVIADA, ENTREGADA).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.actualizarEstado(id, request.getStatus())
        );
    }

    /**
     * Lista las órdenes que se encuentran en un estado determinado.
     *
     * @param status estado por el cual filtrar las órdenes
     * @return listado de órdenes en el estado indicado, envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Listar órdenes por estado", description = "Retorna las órdenes que se encuentran en un estado determinado.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> obtenerPorEstado(
            @PathVariable OrderStatus status) {

        return ResponseEntity.ok(
                orderService.obtenerPorEstado(status)
        );
    }

    /**
     * Elimina una orden a partir de su identificador.
     *
     * @param id identificador de la orden a eliminar
     * @return respuesta sin contenido envuelta en un {@link ResponseEntity} con estado 204
     */
    @Operation(summary = "Eliminar orden", description = "Elimina una orden según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Orden eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {

        orderService.eliminarOrden(id);

        return ResponseEntity.noContent().build();
    }
}