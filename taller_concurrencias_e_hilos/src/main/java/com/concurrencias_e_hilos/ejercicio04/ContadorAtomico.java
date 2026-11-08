package com.concurrencias_e_hilos.ejercicio04;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorAtomico implements Contador {
    private final AtomicInteger valor = new AtomicInteger();

    @Override
    public void incrementar() {
        valor.incrementAndGet();
    }

    @Override
    public int valor() {
        return valor.get();
    }
}
