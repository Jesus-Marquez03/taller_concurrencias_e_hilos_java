package com.concurrencias_e_hilos.ejercicio03;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasParticiones {
    public ResultadoEstadisticas calcular(List<Integer> numeros, int cantidadTrabajadores)
            throws InterruptedException {
        if (cantidadTrabajadores <= 0) {
            throw new IllegalArgumentException("Debe existir al menos un trabajador");
        }
        List<Integer> datos = List.copyOf(numeros);
        List<Trabajador> trabajadores = new ArrayList<>();
        List<Thread> hilos = new ArrayList<>();

        for (int i = 0; i < cantidadTrabajadores; i++) {
            int inicio = i * datos.size() / cantidadTrabajadores;
            int fin = (i + 1) * datos.size() / cantidadTrabajadores;
            Trabajador trabajador = new Trabajador(datos.subList(inicio, fin));
            trabajadores.add(trabajador);
            hilos.add(new Thread(trabajador, "particion-" + (i + 1)));
        }
        for (Thread hilo : hilos) hilo.start();
        for (Thread hilo : hilos) hilo.join();

        int pares = 0;
        long suma = 0;
        int elementos = 0;
        for (Trabajador trabajador : trabajadores) {
            EstadisticaParcial parcial = trabajador.resultado;
            pares += parcial.cantidadPares();
            suma += parcial.sumaPares();
            elementos += parcial.elementos();
        }
        return new ResultadoEstadisticas(pares, suma, elementos);
    }

    public ResultadoEstadisticas calcularDelUnoAlDiezMil(int trabajadores) throws InterruptedException {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= 10_000; i++) numeros.add(i);
        return calcular(numeros, trabajadores);
    }

    private static class Trabajador implements Runnable {
        private final List<Integer> particion;
        private EstadisticaParcial resultado;

        Trabajador(List<Integer> particion) {
            this.particion = particion;
        }

        @Override
        public void run() {
            int pares = 0;
            long suma = 0;
            for (int numero : particion) {
                if (numero % 2 == 0) {
                    pares++;
                    suma += numero;
                }
            }
            resultado = new EstadisticaParcial(pares, suma, particion.size());
        }
    }
}
