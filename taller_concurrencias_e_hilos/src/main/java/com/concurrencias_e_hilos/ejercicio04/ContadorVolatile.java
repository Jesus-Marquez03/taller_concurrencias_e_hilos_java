package com.concurrencias_e_hilos.ejercicio04;

public class ContadorVolatile implements Contador {
    private volatile int valor;

    @Override
    public void incrementar() {
        valor++;
    }

    @Override
    public int valor() {
        return valor;
    }
}
