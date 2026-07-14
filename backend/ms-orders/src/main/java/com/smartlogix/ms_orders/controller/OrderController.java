package com.smartlogix.ms_orders.controller;

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.dto.UpdateOrderStatusRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.service.OrderService;
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {

        orderService.eliminarOrden(id);

        return ResponseEntity.noContent().build();
    }
}