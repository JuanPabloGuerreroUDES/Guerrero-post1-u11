package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica el contrato con la abstraccion NotificadorBajoStock sin conocer ninguna
 * implementacion (este paquete no puede importar reportes).
 */
class InventarioNotificacionTest {

    private InventarioService service;
    private Producto libro;
    private final List<String> notificaciones = new ArrayList<>();

    @BeforeEach
    void setUp() {
        service = new InventarioService();
        libro = new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5);
        service.agregarProducto(libro);
        service.setNotificador((producto, cantidadReorden) ->
                notificaciones.add(producto.getNombre() + ":" + cantidadReorden));
    }

    @Test
    void bajoStock_notificaUnaVezConProductoYCantidadDeReorden() {
        service.actualizarInventario(1L, 17, "SALIDA");

        assertEquals(List.of("Clean Code:12"), notificaciones);
    }

    @Test
    void stockSuficiente_noNotifica() {
        service.actualizarInventario(1L, 8, "SALIDA");

        assertTrue(notificaciones.isEmpty());
    }

    @Test
    void movimientoRechazado_noNotifica() {
        service.actualizarInventario(1L, 50, "SALIDA");

        assertTrue(notificaciones.isEmpty());
    }

    @Test
    void sinNotificador_elResultadoEsElMismo() {
        service.setNotificador(null);

        assertEquals("OK_BAJO_STOCK_REORDEN_12", service.actualizarInventario(1L, 17, "SALIDA"));
    }

    @Test
    void listarProductosBajoStock_devuelveSoloLosQueEstanBajoElMinimo() {
        Producto escaso = new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 1, 5);
        service.agregarProducto(escaso);

        assertEquals(List.of(escaso), service.listarProductosBajoStock());
    }
}
