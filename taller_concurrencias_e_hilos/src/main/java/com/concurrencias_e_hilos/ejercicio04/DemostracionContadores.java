package com.concurrencias_e_hilos.ejercicio04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DemostracionContadores {
    public int ejecutar(Contador contador, int trabajadores, int incrementos) throws InterruptedException {
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < trabajadores; i++) {
            Thread hilo = new Thread(() -> {
                for (int j = 0; j < incrementos; j++) contador.incrementar();
            }, "contador-" + (i + 1));
            hilos.add(hilo);
            hilo.start();
        }
        for (Thread hilo : hilos) hilo.join();
        return contador.valor();
    }

    public List<Integer> ejecutarVeinteVecesInseguro() throws InterruptedException {
        List<Integer> observados = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            observados.add(ejecutar(new ContadorInseguro(), 4, 50_000));
        }
        return observados;
    }

    public int demostrarPerdidaDeterminista() throws InterruptedException, TimeoutException {
        int[] valor = {0};
        CyclicBarrier despuesDeLeer = new CyclicBarrier(2);
        CyclicBarrier despuesDeEscribir = new CyclicBarrier(2);
        Runnable incrementoSeparado = () -> {
            int leido = valor[0];
            esperarBarrera(despuesDeLeer);
            valor[0] = leido + 1;
            esperarBarrera(despuesDeEscribir);
        };
        Thread primero = new Thread(incrementoSeparado, "carrera-1");
        Thread segundo = new Thread(incrementoSeparado, "carrera-2");
        primero.start();
        segundo.start();
        primero.join(TimeUnit.SECONDS.toMillis(2));
        segundo.join(TimeUnit.SECONDS.toMillis(2));
        if (primero.isAlive() || segundo.isAlive()) {
            primero.interrupt();
            segundo.interrupt();
            throw new TimeoutException("La demostracion no termino a tiempo");
        }
        return valor[0];
    }

    private void esperarBarrera(CyclicBarrier barrera) {
        try {
            barrera.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Trabajador interrumpido", e);
        } catch (BrokenBarrierException | TimeoutException e) {
            throw new IllegalStateException("No se pudo coordinar la demostracion", e);
        }
    }
}
