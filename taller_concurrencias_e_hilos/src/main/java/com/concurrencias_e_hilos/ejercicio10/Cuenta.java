package com.concurrencias_e_hilos.ejercicio10;

public class Cuenta {
    private final String id;
    private final Object bloqueo = new Object();
    private long saldo;

    public Cuenta(String id, long saldoInicial) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El id es obligatorio");
        if (saldoInicial < 0) throw new IllegalArgumentException("El saldo no puede ser negativo");
        this.id = id;
        this.saldo = saldoInicial;
    }

    public String id() {
        return id;
    }

    Object bloqueo() {
        return bloqueo;
    }

    long saldoSinBloqueo() {
        return saldo;
    }

    void restar(long valor) {
        saldo -= valor;
    }

    void sumar(long valor) {
        saldo += valor;
    }

    public long saldo() {
        synchronized (bloqueo) {
            return saldo;
        }
    }
}
