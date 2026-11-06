package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceTest {

    private InventarioService service;
    private Producto libro;

    private final PrintStream salidaOriginal = System.out;
    private ByteArrayOutputStream salidaCapturada;

    @BeforeEach
    void setUp() {
        service = new InventarioService();
        libro = new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5);
        service.agregarProducto(libro);
    }

    @AfterEach
    void restaurarSalidaEstandar() {
        System.setOut(salidaOriginal);
    }

    private void capturarSalidaEstandar() {
        salidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salidaCapturada, true));
    }

    // ------------------------------------------------------------------
    // Casos de retorno de actualizarInventario (los cuatro del enunciado)
    // ------------------------------------------------------------------

    @Test
    void entrada_incrementaStockYRetornaOk() {
        assertEquals("OK_30", service.actualizarInventario(1L, 10, "ENTRADA"));
        assertEquals(30, libro.getStock());
    }

    @Test
    void salida_conStockSuficiente_descuentaYRetornaOk() {
        assertEquals("OK_12", service.actualizarInventario(1L, 8, "SALIDA"));
    }

    @Test
    void salida_conStockInsuficiente_retornaErrorYNoModificaStock() {
        assertEquals("ERROR_STOCK_INSUFICIENTE", service.actualizarInventario(1L, 50, "SALIDA"));
        assertEquals(20, libro.getStock());
    }

    @Test
    void salida_dejaStockBajoElMinimo_activaReordenConCantidadCalculada() {
        // stockMinimo=5, stock inicial=20, salida de 17 -> nuevoStock=3 (<5)
        // cantidadReorden = stockMinimo*3 - nuevoStock = 15 - 3 = 12
        assertEquals("OK_BAJO_STOCK_REORDEN_12", service.actualizarInventario(1L, 17, "SALIDA"));
        assertEquals(3, libro.getStock());
    }

    @Test
    void tipoMovimientoInvalido_retornaError() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 5, "TRANSFERENCIA"));
    }

    @Test
    void productoInexistente_retornaError() {
        assertEquals("ERROR_PRODUCTO_NO_EXISTE", service.actualizarInventario(99L, 5, "ENTRADA"));
    }

    // ------------------------------------------------------------------
    // Casos limite (se documentan ANTES de refactorizar)
    // ------------------------------------------------------------------

    @Test
    void productoIdNulo_retornaErrorDatosInvalidos() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(null, 5, "ENTRADA"));
    }

    @Test
    void cantidadCero_retornaErrorDatosInvalidosYNoModificaStock() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 0, "ENTRADA"));
        assertEquals(20, libro.getStock());
    }

    @Test
    void tipoMovimientoEnMinusculas_noSeConsideraValido() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 5, "entrada"));
    }

    @Test
    void salida_dejaStockExactamenteEnElMinimo_noActivaReorden() {
        // stock 20 - 15 = 5, igual al minimo: la condicion es estrictamente "menor que"
        assertEquals("OK_5", service.actualizarInventario(1L, 15, "SALIDA"));
        assertEquals(5, libro.getStock());
    }

    @Test
    void salida_dejaStockUnoPorDebajoDelMinimo_activaReorden() {
        // stock 20 - 16 = 4 (<5) -> reorden = 15 - 4 = 11
        assertEquals("OK_BAJO_STOCK_REORDEN_11", service.actualizarInventario(1L, 16, "SALIDA"));
    }

    @Test
    void salida_hastaStockCero_esPermitidaYActivaReorden() {
        // stock 20 - 20 = 0: no es negativo, por lo tanto no es error; reorden = 15 - 0 = 15
        assertEquals("OK_BAJO_STOCK_REORDEN_15", service.actualizarInventario(1L, 20, "SALIDA"));
        assertEquals(0, libro.getStock());
    }

    @Test
    void entrada_queAunDejaStockBajoElMinimo_tambienActivaReorden() {
        Producto escaso = new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 1, 5);
        service.agregarProducto(escaso);
        // stock 1 + 2 = 3 (<5) -> reorden = 15 - 3 = 12
        assertEquals("OK_BAJO_STOCK_REORDEN_12", service.actualizarInventario(2L, 2, "ENTRADA"));
        assertEquals(3, escaso.getStock());
    }

    @Test
    void entrada_queLevantaStockPorEncimaDelMinimo_noActivaReorden() {
        Producto escaso = new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 1, 5);
        service.agregarProducto(escaso);
        assertEquals("OK_11", service.actualizarInventario(2L, 10, "ENTRADA"));
    }

    @Test
    void entrada_conCantidadNegativa_noValidaStockNegativo_comportamientoActual() {
        // Caracteriza el comportamiento ACTUAL (cuestionable): solo la rama SALIDA valida
        // que el stock no quede negativo; una ENTRADA negativa resta sin ninguna validacion.
        // stock 20 + (-50) = -30 -> (-30 < 5) -> reorden = 15 - (-30) = 45
        assertEquals("OK_BAJO_STOCK_REORDEN_45", service.actualizarInventario(1L, -50, "ENTRADA"));
        assertEquals(-30, libro.getStock());
    }

    @Test
    void conVariosProductos_actualizaUnicamenteElSolicitado() {
        Producto camisa = new Producto(3L, "Camisa", TipoProducto.ROPA, 40.0, 30, 10);
        service.agregarProducto(camisa);

        assertEquals("OK_25", service.actualizarInventario(3L, 5, "SALIDA"));

        assertEquals(25, camisa.getStock());
        assertEquals(20, libro.getStock());
    }

    // ------------------------------------------------------------------
    // Efectos observables por consola (registro de auditoria y alerta)
    // ------------------------------------------------------------------

    @Test
    void movimientoExitoso_imprimeElRegistroDeAuditoria() {
        capturarSalidaEstandar();

        service.actualizarInventario(1L, 10, "ENTRADA");

        assertEquals("ENTRADA de 10 unidades de Clean Code -> stock final: 30"
                + System.lineSeparator(), salidaCapturada.toString());
    }

    @Test
    void movimientoConReorden_imprimeRegistroYLuegoAlerta() {
        capturarSalidaEstandar();

        service.actualizarInventario(1L, 17, "SALIDA");

        assertEquals("SALIDA de 17 unidades de Clean Code -> stock final: 3" + System.lineSeparator()
                        + "ALERTA: Clean Code bajo stock minimo. Reordenar 12 unidades."
                        + System.lineSeparator(),
                salidaCapturada.toString());
    }

    @Test
    void movimientoRechazado_noImprimeNada() {
        capturarSalidaEstandar();

        service.actualizarInventario(1L, 50, "SALIDA");
        service.actualizarInventario(99L, 5, "ENTRADA");
        service.actualizarInventario(1L, 5, "TRANSFERENCIA");

        assertEquals("", salidaCapturada.toString());
    }
}
