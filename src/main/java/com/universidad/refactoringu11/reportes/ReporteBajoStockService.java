package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.NotificadorBajoStock;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteBajoStockService implements NotificadorBajoStock {

    private final InventarioService inventarioService;
    private final Map<Long, Integer> reordenesNotificadas = new LinkedHashMap<>();

    // @Lazy rompe el ciclo de BEANS de Spring (InventarioService <-> ReporteBajoStockService):
    // sin el, la aplicacion no arranca. Es un ciclo distinto del ciclo de PAQUETES.
    public ReporteBajoStockService(@Lazy InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Override
    public void notificar(Producto producto, int cantidadReorden) {
        reordenesNotificadas.put(producto.getId(), cantidadReorden);
    }

    public String generarReporte() {
        List<Producto> bajoStock = inventarioService.listarProductosBajoStock();
        if (bajoStock.isEmpty()) {
            return "REPORTE DE BAJO STOCK: ningun producto bajo el minimo.";
        }
        StringBuilder reporte = new StringBuilder("REPORTE DE BAJO STOCK (")
                .append(bajoStock.size()).append(" producto(s))");
        for (Producto p : bajoStock) {
            reporte.append(System.lineSeparator())
                    .append("- ").append(p.getNombre())
                    .append(": stock ").append(p.getStock())
                    .append(" / minimo ").append(p.getStockMinimo())
                    .append(" / ").append(descripcionDeReorden(p));
        }
        return reporte.toString();
    }

    private String descripcionDeReorden(Producto producto) {
        Integer cantidad = reordenesNotificadas.get(producto.getId());
        return cantidad == null ? "sin alerta registrada" : "reordenar " + cantidad;
    }
}
