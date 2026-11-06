package com.universidad.refactoringu11.inventario;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService {

    private static final String ENTRADA = "ENTRADA";
    private static final String SALIDA = "SALIDA";
    private static final int FACTOR_REORDEN = 3;

    private final List<Producto> productos = new ArrayList<>();
    private final List<String> movimientos = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public String actualizarInventario(Long productoId, int cantidad, String tipoMovimiento) {
        if (!sonDatosValidos(productoId, cantidad, tipoMovimiento)) return "ERROR_DATOS_INVALIDOS";
        Producto producto = buscarProducto(productoId);
        if (producto == null) return "ERROR_PRODUCTO_NO_EXISTE";
        if (excedeStockDisponible(producto, cantidad, tipoMovimiento)) return "ERROR_STOCK_INSUFICIENTE";
        producto.setStock(calcularNuevoStock(producto, cantidad, tipoMovimiento));
        registrarMovimiento(producto, cantidad, tipoMovimiento);
        return necesitaReorden(producto) ? alertarBajoStock(producto) : "OK_" + producto.getStock();
    }

    private boolean sonDatosValidos(Long productoId, int cantidad, String tipoMovimiento) {
        return productoId != null && cantidad != 0
                && (tipoMovimiento.equals(ENTRADA) || tipoMovimiento.equals(SALIDA));
    }

    private Producto buscarProducto(Long productoId) {
        for (Producto p : productos) {
            if (p.getId().equals(productoId)) return p;
        }
        return null;
    }

    // Solo una SALIDA puede exceder el stock disponible (comportamiento original).
    private boolean excedeStockDisponible(Producto producto, int cantidad, String tipoMovimiento) {
        return tipoMovimiento.equals(SALIDA) && producto.getStock() - cantidad < 0;
    }

    private int calcularNuevoStock(Producto producto, int cantidad, String tipoMovimiento) {
        return tipoMovimiento.equals(ENTRADA)
                ? producto.getStock() + cantidad
                : producto.getStock() - cantidad;
    }

    private boolean necesitaReorden(Producto producto) {
        return producto.getStock() < producto.getStockMinimo();
    }

    // Decision de diseno 1: se mantiene como metodo privado (ver README).
    private int calcularCantidadReorden(Producto producto) {
        return (producto.getStockMinimo() * FACTOR_REORDEN) - producto.getStock();
    }

    private void registrarMovimiento(Producto producto, int cantidad, String tipoMovimiento) {
        String registro = tipoMovimiento + " de " + cantidad + " unidades de "
                + producto.getNombre() + " -> stock final: " + producto.getStock();
        movimientos.add(registro);
        System.out.println(registro);
    }

    private String alertarBajoStock(Producto producto) {
        int cantidadReorden = calcularCantidadReorden(producto);
        System.out.println("ALERTA: " + producto.getNombre()
                + " bajo stock minimo. Reordenar " + cantidadReorden + " unidades.");
        return "OK_BAJO_STOCK_REORDEN_" + cantidadReorden;
    }
}
