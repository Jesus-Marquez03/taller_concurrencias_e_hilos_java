package com.concurrencias_e_hilos.ejercicio08;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DemostracionCancelacion {
    public ResultadoCancelacion ejecutar() throws InterruptedException {
        BlockingQueue<String> cola = new ArrayBlockingQueue<>(1);
        CountDownLatch inicio = new CountDownLatch(1);
        CountDownLatch finalizo = new CountDownLatch(1);
        AtomicBoolean interrumpida = new AtomicBoolean();
        AtomicBoolean limpieza = new AtomicBoolean();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> futuro = executor.submit(() -> {
                inicio.countDown();
                try {
                    cola.take();
                } catch (InterruptedException e) {
                    interrumpida.set(true);
                    Thread.currentThread().interrupt();
                } finally {
                    limpieza.set(true);
                    finalizo.countDown();
                }
            });
            if (!inicio.await(1, TimeUnit.SECONDS)) {
                throw new IllegalStateException("La tarea no inicio a tiempo");
            }
            boolean aceptada = futuro.cancel(true);
            boolean termino = finalizo.await(2, TimeUnit.SECONDS);
            return new ResultadoCancelacion(aceptada, interrumpida.get(), limpieza.get(), termino);
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("El executor no termino");
            }
        }
    }
}
