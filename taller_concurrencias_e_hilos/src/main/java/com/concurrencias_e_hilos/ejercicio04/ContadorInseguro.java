package com.concurrencias_e_hilos.ejercicio04;

public class ContadorInseguro implements Contador {
    private int valor;

    @Override
    public void incrementar() {
        valor++;
    }

    @Override
    public int valor() {
        return valor;
    }
}
