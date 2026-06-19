package com.smartlogix.ms_restock;

import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.repository.RestockRepository;
import com.smartlogix.ms_restock.service.RestockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de RestockService.
 * Usa Mockito para aislar la capa de servicio del repositorio.
 * Cobertura objetiva: mayor al 60% de la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
class RestockServiceApplicationTests {

    @Mock
    private RestockRepository restockRepository;

    @InjectMocks
    private RestockService restockService;

    private RestockRequest solicitudMock;

    @BeforeEach
    void setUp() {
        solicitudMock = new RestockRequest();
        solicitudMock.setIdRestock(1L);
        solicitudMock.setIdItem(10L);
        solicitudMock.setNombreItem("Caja A");
        solicitudMock.setBodega("Bodega Central");
        solicitudMock.setCantidadSolicitada(50);
        solicitudMock.setEstado("PENDIENTE");
        solicitudMock.setFechaSolicitud(LocalDateTime.now());
    }

    // ─── listarSolicitudes ────────────────────────────────────────────────────

    @Test
    @DisplayName("listarSolicitudes: retorna lista de solicitudes")
    void listarSolicitudes_retornaLista() {
        when(restockRepository.findAll()).thenReturn(Arrays.asList(solicitudMock));

        List<RestockRequest> resultado = restockService.listarSolicitudes();

        assertEquals(1, resultado.size());
        assertEquals("Caja A", resultado.get(0).getNombreItem());
        verify(restockRepository, times(1)).findAll();
    }

    // ─── obtenerPorId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId: retorna solicitud cuando existe")
    void obtenerPorId_existente_retornaSolicitud() {
        when(restockRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));

        RestockRequest resultado = restockService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRestock());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(restockRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> restockService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─── crearSolicitud ───────────────────────────────────────────────────────

    @Test
    @DisplayName("crearSolicitud: fuerza estado PENDIENTE y persiste")
    void crearSolicitud_fuerzaEstadoPendiente() {
        RestockRequest nueva = new RestockRequest();
        nueva.setIdItem(5L);
        nueva.setNombreItem("Pallet B");
        nueva.setBodega("Bodega Norte");
        nueva.setCantidadSolicitada(20);
        nueva.setEstado("APROBADA");

        when(restockRepository.save(any(RestockRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RestockRequest resultado = restockService.crearSolicitud(nueva);

        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFechaSolicitud());
        assertNull(resultado.getFechaActualizacion());
        verify(restockRepository, times(1)).save(any(RestockRequest.class));
    }

    // ─── actualizarEstado ─────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarEstado: actualiza a APROBADA y registra timestamp")
    void actualizarEstado_estadoValido_actualizaCorrectamente() {
        when(restockRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(restockRepository.save(any(RestockRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RestockRequest resultado = restockService.actualizarEstado(1L, "APROBADA");

        assertEquals("APROBADA", resultado.getEstado());
        assertNotNull(resultado.getFechaActualizacion());
    }

    @Test
    @DisplayName("actualizarEstado: lanza excepcion con estado invalido")
    void actualizarEstado_estadoInvalido_lanzaExcepcion() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> restockService.actualizarEstado(1L, "INVALIDO"));

        assertTrue(ex.getMessage().contains("Estado inválido"));
    }

    // ─── listarPorEstado ──────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorEstado: retorna solicitudes filtradas por estado")
    void listarPorEstado_estadoValido_retornaLista() {
        when(restockRepository.findByEstado("PENDIENTE"))
                .thenReturn(Arrays.asList(solicitudMock));

        List<RestockRequest> resultado = restockService.listarPorEstado("PENDIENTE");

        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("listarPorEstado: lanza excepcion con estado invalido")
    void listarPorEstado_estadoInvalido_lanzaExcepcion() {
        assertThrows(RuntimeException.class,
                () -> restockService.listarPorEstado("DESCONOCIDO"));
    }

    // ─── eliminarSolicitud ────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarSolicitud: llama deleteById cuando la solicitud existe")
    void eliminarSolicitud_existente_eliminaCorrectamente() {
        when(restockRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        doNothing().when(restockRepository).deleteById(1L);

        assertDoesNotThrow(() -> restockService.eliminarSolicitud(1L));
        verify(restockRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarSolicitud: lanza excepcion si no existe")
    void eliminarSolicitud_noExistente_lanzaExcepcion() {
        when(restockRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> restockService.eliminarSolicitud(99L));
        verify(restockRepository, never()).deleteById(any());
    }

    // ─── listarPorBodega ──────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorBodega: retorna solicitudes de la bodega indicada")
    void listarPorBodega_retornaSolicitudesDeBodega() {
        when(restockRepository.findByBodega("Bodega Central"))
                .thenReturn(Arrays.asList(solicitudMock));

        List<RestockRequest> resultado = restockService.listarPorBodega("Bodega Central");

        assertEquals(1, resultado.size());
        assertEquals("Bodega Central", resultado.get(0).getBodega());
    }
}