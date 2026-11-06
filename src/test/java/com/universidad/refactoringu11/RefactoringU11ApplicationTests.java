package com.universidad.refactoringu11;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import com.universidad.refactoringu11.reportes.ReporteBajoStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Las pruebas de caracterizacion instancian los servicios a mano y no ven el cableado de
 * Spring. Esta prueba garantiza que el contexto arranca y que la inversion de dependencias
 * funciona de punta a punta con los beans reales.
 */
@SpringBootTest
class RefactoringU11ApplicationTests {

    @Autowired
    private InventarioService inventario;

    @Autowired
    private ReporteBajoStockService reporte;

    @Test
    void elContextoArrancaYLosBeansQuedanCableados() {
        assertNotNull(inventario);
        assertNotNull(reporte);
    }

    @Test
    void unaSalidaQueDejaBajoStock_llegaAlReporteATravesDeLaInterfaz() {
        inventario.agregarProducto(new Producto(100L, "Libro de prueba", TipoProducto.LIBRO, 10.0, 20, 5));

        assertEquals("OK_BAJO_STOCK_REORDEN_12", inventario.actualizarInventario(100L, 17, "SALIDA"));

        assertTrue(reporte.generarReporte().contains("- Libro de prueba: stock 3 / minimo 5 / reordenar 12"));
    }
}
