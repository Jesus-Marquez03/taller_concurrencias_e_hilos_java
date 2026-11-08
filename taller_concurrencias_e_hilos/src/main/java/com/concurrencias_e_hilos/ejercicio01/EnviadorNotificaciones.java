package com.concurrencias_e_hilos.ejercicio01;

import java.util.function.Consumer;

public class EnviadorNotificaciones implements Runnable {
    private final Consumer<String> salida;

    public EnviadorNotificaciones(Consumer<String> salida) {
        this.salida = salida;
    }

    @Override
    public void run() {
        for (int paso = 1; paso <= 5; paso++) {
            salida.accept("Notificacion " + paso + " - " + Thread.currentThread().getName());
        }
    }
}
