package com.universidad.refactoringu11.inventario;

/**
 * Abstraccion que el dominio define y que la infraestructura implementa. Vive junto al dominio
 * para que la dependencia apunte siempre hacia el dominio y nunca al reves.
 */
public interface NotificadorBajoStock {

    /**
     * Se invoca una vez por cada movimiento que deja un producto bajo su stock minimo.
     *
     * @param producto        producto que quedo bajo el minimo
     * @param cantidadReorden unidades que se recomienda reordenar
     */
    void notificar(Producto producto, int cantidadReorden);
}
