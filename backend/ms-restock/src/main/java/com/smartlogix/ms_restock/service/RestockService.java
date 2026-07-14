package com.smartlogix.ms_restock.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.repository.RestockRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de solicitudes de
 * reabastecimiento (restock) de stock.
 * <p>
 * Encapsula las operaciones de consulta, creación, actualización de estado y
 * eliminación de solicitudes, delegando la persistencia en {@link RestockRepository}
 * y comunicándose con el microservicio de inventario ({@code ms-inventory}) vía
 * {@link RestTemplate} para validar items y actualizar stock.
 */
@Service
public class RestockService {

    private final RestockRepository restockRepository;
    private final RestTemplate restTemplate;

    @Value("${inventory-service.url:http://localhost:8080}") // Agregamos un valor por defecto por si falla en los tests
    private String inventoryServiceUrl;

    /**
     * Crea una nueva instancia del servicio de restock.
     *
     * @param restockRepository repositorio utilizado para acceder y persistir las solicitudes
     * @param restTemplate cliente HTTP utilizado para comunicarse con ms-inventory
     */
    // Aquí está la Buena Práctica: Inyección de Dependencias mediante el constructor
    public RestockService(RestockRepository restockRepository, RestTemplate restTemplate) {
        this.restockRepository = restockRepository;
        this.restTemplate = restTemplate;
    }

    // ─── Consultas ────────────────────────────────────────────────────────────
    // ... (TODO TU CÓDIGO HACIA ABAJO QUEDA EXACTAMENTE IGUAL) ...
    // ─── Consultas ────────────────────────────────────────────────────────────

    /**
     * Obtiene el listado completo de solicitudes de restock registradas.
     *
     * @return lista con todas las solicitudes existentes
     */
    public List<RestockRequest> listarSolicitudes() {
        return restockRepository.findAll();
    }

    /**
     * Busca una solicitud de restock por su identificador único.
     *
     * @param id identificador de la solicitud a buscar
     * @return la solicitud encontrada
     * @throws RuntimeException si no existe una solicitud con el id indicado
     */
    public RestockRequest obtenerPorId(Long id) {
        return restockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }

    /**
     * Lista las solicitudes de restock que se encuentran en un estado determinado.
     *
     * @param estado estado por el cual filtrar (no distingue mayúsculas/minúsculas)
     * @return lista de solicitudes en el estado indicado
     * @throws RuntimeException si el estado indicado no es válido
     */
    public List<RestockRequest> listarPorEstado(String estado) {
        validarEstado(estado);
        return restockRepository.findByEstado(estado.toUpperCase());
    }

    /**
     * Lista el historial de solicitudes de restock asociadas a un item de inventario.
     *
     * @param idItem identificador del item de inventario
     * @return lista de solicitudes asociadas al item indicado
     */
    public List<RestockRequest> listarPorItem(Long idItem) {
        return restockRepository.findByIdItem(idItem);
    }

    /**
     * Lista las solicitudes de restock asociadas a una bodega específica.
     *
     * @param bodega nombre de la bodega
     * @return lista de solicitudes asociadas a la bodega indicada
     */
    public List<RestockRequest> listarPorBodega(String bodega) {
        return restockRepository.findByBodega(bodega);
    }

    /**
     * Lista las solicitudes de restock en estado pendiente de una bodega específica.
     *
     * @param bodega nombre de la bodega
     * @return lista de solicitudes pendientes asociadas a la bodega indicada
     */
    public List<RestockRequest> pendientesPorBodega(String bodega) {
        return restockRepository.findByBodegaAndEstado(bodega, RestockRequest.EstadoRestock.PENDIENTE.name());
    }

    /**
     * Obtiene un resumen con el conteo de solicitudes de restock agrupadas por estado.
     *
     * @return mapa con el nombre de cada estado y la cantidad de solicitudes asociadas
     */
    public Map<String, Long> resumenPorEstado() {
        List<Object[]> filas = restockRepository.contarPorEstado();
        Map<String, Long> resumen = new HashMap<>();
        for (Object[] fila : filas) {
            String estado = (String) fila[0];
            Long total   = (Long)   fila[1];
            resumen.put(estado, total);
        }
        return resumen;
    }

    // ─── Creación ─────────────────────────────────────────────────────────────

    /**
     * Crea una nueva solicitud de restock, validando previamente que el item de
     * inventario asociado exista en {@code ms-inventory}.
     * <p>
     * Al crearla, se asigna el estado inicial {@link RestockRequest.EstadoRestock#PENDIENTE},
     * la fecha de solicitud actual y se deja sin definir la fecha de actualización.
     *
     * @param solicitud datos de la solicitud a crear
     * @return la solicitud creada, ya persistida con su identificador asignado
     * @throws RuntimeException si el item indicado no existe en el inventario
     */
    public RestockRequest crearSolicitud(RestockRequest solicitud) {
        // Validar que el ítem existe en ms-inventory
        try {
            restTemplate.getForObject(
                inventoryServiceUrl + "/api/inventory/" + solicitud.getIdItem(),
                Map.class
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "No se pudo crear la solicitud: el ítem con id " +
                solicitud.getIdItem() + " no existe en el inventario."
            );
        }

        solicitud.setEstado(RestockRequest.EstadoRestock.PENDIENTE.name());
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setFechaActualizacion(null);
        return restockRepository.save(solicitud);
    }

    // ─── Actualización de estado ──────────────────────────────────────────────

    /**
     * Actualiza el estado de una solicitud de restock existente.
     * <p>
     * Si el nuevo estado corresponde a {@link RestockRequest.EstadoRestock#APROBADA},
     * además actualiza el stock del item correspondiente en {@code ms-inventory}.
     *
     * @param id identificador de la solicitud a actualizar
     * @param nuevoEstado nuevo estado a asignar
     * @return la solicitud actualizada
     * @throws RuntimeException si el estado indicado no es válido o si no existe
     *         una solicitud con el id indicado
     */
    public RestockRequest actualizarEstado(Long id, String nuevoEstado) {
        validarEstado(nuevoEstado);
        RestockRequest solicitud = obtenerPorId(id);

        // Si se aprueba, actualizar stock en ms-inventory
        if (nuevoEstado.equalsIgnoreCase(RestockRequest.EstadoRestock.APROBADA.name())) {
            actualizarStockEnInventario(solicitud);
        }

        solicitud.setEstado(nuevoEstado.toUpperCase());
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return restockRepository.save(solicitud);
    }

    // ─── Eliminación ──────────────────────────────────────────────────────────

    /**
     * Elimina una solicitud de restock a partir de su identificador.
     *
     * @param id identificador de la solicitud a eliminar
     * @throws RuntimeException si no existe una solicitud con el id indicado
     */
    public void eliminarSolicitud(Long id) {
        obtenerPorId(id);
        restockRepository.deleteById(id);
    }

    // ─── Integración con ms-inventory ────────────────────────────────────────

    /**
     * Actualiza el stock del item de inventario asociado a una solicitud aprobada,
     * sumando la cantidad solicitada a la cantidad actual registrada en {@code ms-inventory}.
     *
     * @param solicitud solicitud de restock aprobada cuyo item debe actualizarse
     * @throws RuntimeException si no se puede obtener o actualizar el item en ms-inventory
     */
    @SuppressWarnings("unchecked")
    private void actualizarStockEnInventario(RestockRequest solicitud) {
        try {
            // Obtener cantidad actual del ítem
            Map<String, Object> item = restTemplate.getForObject(
                inventoryServiceUrl + "/api/inventory/" + solicitud.getIdItem(),
                Map.class
            );

            if (item == null) throw new RuntimeException("Item no encontrado");

            int cantidadActual = ((Number) item.get("cantidad")).intValue();
            int nuevaCantidad  = cantidadActual + solicitud.getCantidadSolicitada();

            // Actualizar cantidad en ms-inventory
            Map<String, Integer> body = new HashMap<>();
            body.put("cantidad", nuevaCantidad);

            restTemplate.put(
                inventoryServiceUrl + "/api/inventory/" + solicitud.getIdItem() + "/cantidad",
                body
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "No se pudo actualizar el stock en ms-inventory: " + e.getMessage()
            );
        }
    }

    // ─── Validación interna ───────────────────────────────────────────────────

    /**
     * Valida que el estado indicado corresponda a uno de los valores definidos
     * en {@link RestockRequest.EstadoRestock}.
     *
     * @param estado valor de estado a validar
     * @throws RuntimeException si el estado indicado no es un valor permitido
     */
    private void validarEstado(String estado) {
        try {
            RestockRequest.EstadoRestock.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Estado inválido: '" + estado + "'. " +
                "Valores permitidos: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA"
            );
        }
    }
}