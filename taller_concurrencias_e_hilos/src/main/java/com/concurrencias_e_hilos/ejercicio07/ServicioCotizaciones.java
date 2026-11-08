package com.concurrencias_e_hilos.ejercicio07;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ServicioCotizaciones {
    public InformeCotizaciones consultar() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            Map<Integer, Future<Cotizacion>> futuros = new LinkedHashMap<>();
            for (int proveedor = 1; proveedor <= 8; proveedor++) {
                futuros.put(proveedor, executor.submit(new ConsultaCotizacion(proveedor)));
            }
            return recuperar(futuros);
        } finally {
            cerrar(executor);
        }
    }

    public InformeCotizaciones consultarPorOrdenDeFinalizacion() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ExecutorCompletionService<Cotizacion> completadas = new ExecutorCompletionService<>(executor);
        try {
            Map<Future<Cotizacion>, Integer> proveedores = new LinkedHashMap<>();
            for (int proveedor = 1; proveedor <= 8; proveedor++) {
                Future<Cotizacion> futuro = completadas.submit(new ConsultaCotizacion(proveedor));
                proveedores.put(futuro, proveedor);
            }
            List<Cotizacion> exitosas = new ArrayList<>();
            Map<Integer, String> errores = new LinkedHashMap<>();
            for (int i = 0; i < 8; i++) {
                Future<Cotizacion> futuro = completadas.take();
                int proveedor = proveedores.get(futuro);
                obtenerUno(futuro, proveedor, exitosas, errores);
            }
            return new InformeCotizaciones(List.copyOf(exitosas), Map.copyOf(errores));
        } finally {
            cerrar(executor);
        }
    }

    private InformeCotizaciones recuperar(Map<Integer, Future<Cotizacion>> futuros)
            throws InterruptedException {
        List<Cotizacion> exitosas = new ArrayList<>();
        Map<Integer, String> errores = new LinkedHashMap<>();
        for (Map.Entry<Integer, Future<Cotizacion>> entrada : futuros.entrySet()) {
            obtenerUno(entrada.getValue(), entrada.getKey(), exitosas, errores);
        }
        return new InformeCotizaciones(List.copyOf(exitosas), Map.copyOf(errores));
    }

    private void obtenerUno(Future<Cotizacion> futuro, int proveedor,
                            List<Cotizacion> exitosas, Map<Integer, String> errores)
            throws InterruptedException {
        try {
            exitosas.add(futuro.get());
        } catch (ExecutionException e) {
            errores.put(proveedor, e.getCause().getMessage());
        }
    }

    private void cerrar(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("El executor no termino");
            }
        }
    }
}
