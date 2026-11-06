package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReporteBajoStockServiceTest {

    private InventarioService inventario;
    private ReporteBajoStockService reporte;

    @BeforeEach
    void setUp() {
        inventario = new InventarioService();
        reporte = new ReporteBajoStockService(inventario);
        inventario.setNotificador(reporte);
        inventario.agregarProducto(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5));
    }

    @Test
    void sinProductosBajoStock_reportaQueNoHayNinguno() {
        assertEquals("REPORTE DE BAJO STOCK: ningun producto bajo el minimo.", reporte.generarReporte());
    }

    @Test
    void movimientoQueDejaBajoStock_apareceEnElReporteConLaCantidadNotificada() {
        inventario.actualizarInventario(1L, 17, "SALIDA");

        String esperado = "REPORTE DE BAJO STOCK (1 producto(s))" + System.lineSeparator()
                + "- Clean Code: stock 3 / minimo 5 / reordenar 12";
        assertEquals(esperado, reporte.generarReporte());
    }

    @Test
    void productoRepuesto_dejaDeAparecerEnElReporte() {
        inventario.actualizarInventario(1L, 17, "SALIDA");
        inventario.actualizarInventario(1L, 10, "ENTRADA");

        assertEquals("REPORTE DE BAJO STOCK: ningun producto bajo el minimo.", reporte.generarReporte());
    }

    @Test
    void productoBajoStockSinNotificacion_seMarcaSinAlertaRegistrada() {
        inventario.agregarProducto(new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 1, 5));

        assertTrue(reporte.generarReporte().contains("- Teclado: stock 1 / minimo 5 / sin alerta registrada"));
    }
}
