package com.concurrencias_e_hilos.ejercicio04;

public class ContadorSincronizado implements Contador {
    private int valor;

    @Override
    public synchronized void incrementar() {
        valor++;
    }

    @Override
    public synchronized int valor() {
        return valor;
    }
}
