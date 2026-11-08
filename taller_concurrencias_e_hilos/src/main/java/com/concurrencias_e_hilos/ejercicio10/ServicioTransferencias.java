package com.concurrencias_e_hilos.ejercicio10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ServicioTransferencias {
    public boolean transferir(Cuenta origen, Cuenta destino, long valor) {
        if (origen == null || destino == null) throw new IllegalArgumentException("Las cuentas son obligatorias");
        if (valor <= 0) throw new IllegalArgumentException("El valor debe ser positivo");
        if (origen == destino || origen.id().equals(destino.id())) {
            throw new IllegalArgumentException("Las cuentas deben ser diferentes");
        }

        Cuenta primera = origen.id().compareTo(destino.id()) < 0 ? origen : destino;
        Cuenta segunda = primera == origen ? destino : origen;
        synchronized (primera.bloqueo()) {
            synchronized (segunda.bloqueo()) {
                if (origen.saldoSinBloqueo() < valor) return false;
                origen.restar(valor);
                destino.sumar(valor);
                return true;
            }
        }
    }

    public ResultadoTransferencias ejecutarTaller() throws InterruptedException {
        Cuenta cuentaA = new Cuenta("A", 100_000);
        Cuenta cuentaB = new Cuenta("B", 100_000);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<Boolean>> tareas = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                tareas.add(() -> transferir(cuentaA, cuentaB, 100));
                tareas.add(() -> transferir(cuentaB, cuentaA, 100));
            }
            List<Future<Boolean>> futuros = executor.invokeAll(tareas, 5, TimeUnit.SECONDS);
            for (Future<Boolean> futuro : futuros) {
                if (futuro.isCancelled()) throw new IllegalStateException("Una transferencia excedio el plazo");
            }
            long saldoA = cuentaA.saldo();
            long saldoB = cuentaB.saldo();
            return new ResultadoTransferencias(saldoA, saldoB, saldoA + saldoB);
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("El executor de transferencias no termino");
            }
        }
    }
}
