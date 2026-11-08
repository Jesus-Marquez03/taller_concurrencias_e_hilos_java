package com.concurrencias_e_hilos.ejercicio12;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CoordinadorPedidos {
    public List<ResultadoPedido> procesarSecuencial(List<Pedido> pedidos, ProcesadorPedidos procesador) {
        List<ResultadoPedido> resultados = new ArrayList<>();
        for (Pedido pedido : pedidos) resultados.add(procesador.procesar(pedido));
        return List.copyOf(resultados);
    }

    public List<ResultadoPedido> procesarConcurrente(List<Pedido> pedidos,
                                                      ProcesadorPedidos procesador,
                                                      int trabajadores) throws InterruptedException {
        if (trabajadores <= 0) throw new IllegalArgumentException("Debe haber trabajadores");
        ExecutorService executor = Executors.newFixedThreadPool(trabajadores);
        try {
            List<Future<ResultadoPedido>> futuros = new ArrayList<>();
            for (Pedido pedido : pedidos) futuros.add(executor.submit(() -> procesador.procesar(pedido)));
            List<ResultadoPedido> resultados = new ArrayList<>();
            for (Future<ResultadoPedido> futuro : futuros) {
                try {
                    resultados.add(futuro.get());
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Un pedido no produjo resultado", e.getCause());
                }
            }
            return List.copyOf(resultados);
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("El executor de pedidos no termino");
            }
        }
    }
}
