package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClasificadorProductoTest {

    private final ClasificadorProducto clasificador = new ClasificadorProducto();

    @Test
    void libro_noTieneRecargo() {
        Producto p = new Producto(1L, "Refactoring", TipoProducto.LIBRO, 60.0, 10, 2);
        assertEquals(0.0, clasificador.calcularRecargoEnvio(p), 0.001);
    }

    @Test
    void electronico_recargaOchoPorCiento() {
        Producto p = new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 10, 2);
        assertEquals(8.0, clasificador.calcularRecargoEnvio(p), 0.001);
    }

    @Test
    void ropa_recargaTresPorCiento() {
        Producto p = new Producto(3L, "Camisa", TipoProducto.ROPA, 40.0, 10, 2);
        assertEquals(1.2, clasificador.calcularRecargoEnvio(p), 0.001);
    }

    @Test
    void alimento_recargoFijo() {
        Producto p = new Producto(4L, "Cafe", TipoProducto.ALIMENTO, 15.0, 10, 2);
        assertEquals(2.50, clasificador.calcularRecargoEnvio(p), 0.001);
    }

    // ------------------------------------------------------------------
    // Casos limite (se documentan ANTES de refactorizar)
    // ------------------------------------------------------------------

    @Test
    void alimento_recargoFijoNoDependeDelPrecio() {
        Producto barato = new Producto(5L, "Dulce", TipoProducto.ALIMENTO, 1.0, 10, 2);
        Producto caro = new Producto(6L, "Jamon", TipoProducto.ALIMENTO, 500.0, 10, 2);
        assertEquals(2.50, clasificador.calcularRecargoEnvio(barato), 0.001);
        assertEquals(2.50, clasificador.calcularRecargoEnvio(caro), 0.001);
    }

    @Test
    void electronico_conPrecioCero_noTieneRecargo() {
        Producto p = new Producto(7L, "Regalo", TipoProducto.ELECTRONICO, 0.0, 10, 2);
        assertEquals(0.0, clasificador.calcularRecargoEnvio(p), 0.001);
    }

    @Test
    void productoSinTipo_lanzaExcepcion() {
        Producto p = new Producto(8L, "Sin tipo", null, 10.0, 10, 2);
        assertThrows(NullPointerException.class, () -> clasificador.calcularRecargoEnvio(p));
    }
}
