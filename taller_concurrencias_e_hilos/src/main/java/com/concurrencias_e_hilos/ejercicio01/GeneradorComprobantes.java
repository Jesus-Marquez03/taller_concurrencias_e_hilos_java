package com.concurrencias_e_hilos.ejercicio01;

import java.util.function.Consumer;

public class GeneradorComprobantes implements Runnable {
    private final Consumer<String> salida;

    public GeneradorComprobantes(Consumer<String> salida) {
        this.salida = salida;
    }

    @Override
    public void run() {
        for (int paso = 1; paso <= 5; paso++) {
            salida.accept("Comprobante " + paso + " - " + Thread.currentThread().getName());
        }
    }
}
