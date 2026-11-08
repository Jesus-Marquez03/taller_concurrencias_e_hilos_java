package com.concurrencias_e_hilos.ejercicio07;

import java.util.concurrent.Callable;

public class ConsultaCotizacion implements Callable<Cotizacion> {
    private final int proveedor;

    public ConsultaCotizacion(int proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public Cotizacion call() {
        if (proveedor == 3 || proveedor == 7) {
            throw new IllegalStateException("Error tecnico del proveedor " + proveedor);
        }
        return new Cotizacion(proveedor, 100_000L + 1_000L * proveedor);
    }
}
