package com.concurrencias_e_hilos.ejercicio05;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ServicioReservas {
    public List<ResultadoReserva> ejecutar(int clientes) throws InterruptedException {
        Sala sala = new Sala(10);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<ResultadoReserva>> futuros = new ArrayList<>();
            for (int i = 0; i < clientes; i++) {
                futuros.add(executor.submit(new SolicitudReserva(i + 1, (i % 10) + 1, sala)));
            }
            List<ResultadoReserva> resultados = new ArrayList<>();
            for (Future<ResultadoReserva> futuro : futuros) {
                try {
                    resultados.add(futuro.get());
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Fallo inesperado al reservar", e.getCause());
                }
            }
            return List.copyOf(resultados);
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) executor.shutdownNow();
        }
    }
}
