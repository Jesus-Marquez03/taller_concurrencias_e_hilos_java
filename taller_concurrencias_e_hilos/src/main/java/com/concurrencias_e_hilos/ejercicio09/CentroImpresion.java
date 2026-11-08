package com.concurrencias_e_hilos.ejercicio09;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CentroImpresion {
    private static final int CONSUMIDORES = 3;

    public InformeImpresion procesar(boolean provocarFalloEnDocumento7) throws InterruptedException {
        BlockingQueue<Documento> cola = new ArrayBlockingQueue<>(4);
        Set<Integer> completados = ConcurrentHashMap.newKeySet();
        Set<Integer> fallidos = ConcurrentHashMap.newKeySet();
        AtomicInteger duplicados = new AtomicInteger();
        AtomicBoolean cancelar = new AtomicBoolean();
        ExecutorService executor = Executors.newFixedThreadPool(CONSUMIDORES + 1);
        List<Future<?>> futuros = new ArrayList<>();
        try {
            futuros.add(executor.submit(() -> producir(cola, cancelar)));
            for (int i = 0; i < CONSUMIDORES; i++) {
                futuros.add(executor.submit(() -> consumir(cola, provocarFalloEnDocumento7,
                        completados, fallidos, duplicados, cancelar)));
            }
            esperar(futuros, cancelar);
        } finally {
            cancelar.set(true);
            executor.shutdownNow();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("El centro de impresion no termino");
            }
        }

        Set<Integer> pendientes = new TreeSet<>();
        for (int id = 1; id <= 20; id++) {
            if (!completados.contains(id) && !fallidos.contains(id)) pendientes.add(id);
        }
        return new InformeImpresion(Set.copyOf(completados), Set.copyOf(fallidos),
                Set.copyOf(pendientes), duplicados.get());
    }

    private void producir(BlockingQueue<Documento> cola, AtomicBoolean cancelar) {
        try {
            for (int id = 1; id <= 20 && !cancelar.get(); id++) {
                publicar(cola, new Documento(id, "Documento " + id, (id % 5) + 1), cancelar);
            }
            for (int i = 0; i < CONSUMIDORES && !cancelar.get(); i++) {
                publicar(cola, Documento.senalFin(), cancelar);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void publicar(BlockingQueue<Documento> cola, Documento documento,
                          AtomicBoolean cancelar) throws InterruptedException {
        while (!cancelar.get() && !cola.offer(documento, 100, TimeUnit.MILLISECONDS)) {
            // El plazo permite revisar la cancelacion sin espera activa.
        }
    }

    private void consumir(BlockingQueue<Documento> cola, boolean provocarFallo,
                          Set<Integer> completados, Set<Integer> fallidos,
                          AtomicInteger duplicados, AtomicBoolean cancelar) {
        try {
            while (!cancelar.get()) {
                Documento documento = cola.poll(100, TimeUnit.MILLISECONDS);
                if (documento == null) continue;
                if (documento.fin()) return;
                if (provocarFallo && documento.id() == 7) {
                    fallidos.add(documento.id());
                    cancelar.set(true);
                    throw new IllegalStateException("Fallo al imprimir el documento 7");
                }
                if (!completados.add(documento.id())) duplicados.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void esperar(List<Future<?>> futuros, AtomicBoolean cancelar) throws InterruptedException {
        boolean fallo = false;
        for (Future<?> futuro : futuros) {
            try {
                futuro.get(5, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                fallo = true;
                cancelar.set(true);
                for (Future<?> otro : futuros) {
                    if (otro != futuro) otro.cancel(true);
                }
            } catch (TimeoutException e) {
                cancelar.set(true);
                for (Future<?> otro : futuros) otro.cancel(true);
                throw new IllegalStateException("El centro de impresion excedio el plazo", e);
            } catch (java.util.concurrent.CancellationException e) {
                if (!fallo) throw e;
            }
        }
    }
}
