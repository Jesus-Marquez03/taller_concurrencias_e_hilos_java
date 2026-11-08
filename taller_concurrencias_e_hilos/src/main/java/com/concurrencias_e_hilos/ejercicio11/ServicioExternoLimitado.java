package com.concurrencias_e_hilos.ejercicio11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServicioExternoLimitado {
    public InformeServicio ejecutar() throws InterruptedException {
        Semaphore semaforo = new Semaphore(3);
        AtomicInteger activas = new AtomicInteger();
        AtomicInteger maximo = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<ResultadoSolicitud>> futuros = new ArrayList<>();
            for (int id = 1; id <= 30; id++) {
                int solicitud = id;
                futuros.add(executor.submit(() -> procesar(solicitud, semaforo, activas, maximo)));
            }
            int exitos = 0;
            int errores = 0;
            for (Future<ResultadoSolicitud> futuro : futuros) {
                try {
                    if (futuro.get().exitosa()) exitos++; else errores++;
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Fallo no controlado", e.getCause());
                }
            }
            return new InformeServicio(exitos, errores, exitos + errores, maximo.get(),
                    activas.get(), semaforo.availablePermits());
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("El executor del servicio no termino");
            }
        }
    }

    private ResultadoSolicitud procesar(int id, Semaphore semaforo,
                                        AtomicInteger activas, AtomicInteger maximo) {
        boolean adquirido = false;
        try {
            semaforo.acquire();
            adquirido = true;
            int actuales = activas.incrementAndGet();
            maximo.accumulateAndGet(actuales, Math::max);
            Thread.sleep(5); // Solo simula la duracion del servicio externo.
            if (id % 7 == 0) return new ResultadoSolicitud(id, false, "Error tecnico");
            return new ResultadoSolicitud(id, true, "OK");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ResultadoSolicitud(id, false, "Interrumpida");
        } finally {
            if (adquirido) {
                activas.decrementAndGet();
                semaforo.release();
            }
        }
    }

    public ResultadoCancelacionPermiso cancelarMientrasEspera() throws InterruptedException {
        Semaphore semaforo = new Semaphore(1);
        semaforo.acquire();
        CountDownLatch inicio = new CountDownLatch(1);
        CountDownLatch fin = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> futuro = executor.submit(() -> {
                boolean adquirido = false;
                inicio.countDown();
                try {
                    semaforo.acquire();
                    adquirido = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (adquirido) semaforo.release();
                    fin.countDown();
                }
            });
            if (!inicio.await(1, TimeUnit.SECONDS)) throw new IllegalStateException("La tarea no inicio");
            boolean cancelada = futuro.cancel(true);
            boolean finalizada = fin.await(2, TimeUnit.SECONDS);
            semaforo.release();
            return new ResultadoCancelacionPermiso(cancelada, finalizada, semaforo.availablePermits());
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
