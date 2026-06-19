package com.smartlogix.bff.client;

import com.smartlogix.bff.model.RestockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "restock-service", url = "${restock-service.url}")
public interface RestockServiceClient {

    @GetMapping("/api/restock")
    List<RestockRequest> listarSolicitudes();

    @GetMapping("/api/restock/{id}")
    RestockRequest obtenerPorId(@PathVariable("id") Long id);

    @GetMapping("/api/restock/estado")
    List<RestockRequest> listarPorEstado(@RequestParam("valor") String valor);

    @GetMapping("/api/restock/item/{id_item}")
    List<RestockRequest> listarPorItem(@PathVariable("id_item") Long id_item);

    @GetMapping("/api/restock/bodega")
    List<RestockRequest> listarPorBodega(@RequestParam("nombre") String nombre);

    @GetMapping("/api/restock/bodega/pendientes")
    List<RestockRequest> pendientesPorBodega(@RequestParam("nombre") String nombre);

    @GetMapping("/api/restock/resumen")
    Map<String, Long> resumenPorEstado();

    @PostMapping("/api/restock")
    RestockRequest crearSolicitud(@RequestBody RestockRequest solicitud);

    @PutMapping("/api/restock/{id}/estado")
    RestockRequest actualizarEstado(@PathVariable("id") Long id,
                                    @RequestBody Map<String, String> datos);

    @DeleteMapping("/api/restock/{id}")
    void eliminarSolicitud(@PathVariable("id") Long id);
}