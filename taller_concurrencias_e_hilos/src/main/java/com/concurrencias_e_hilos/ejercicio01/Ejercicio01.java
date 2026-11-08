package com.concurrencias_e_hilos.ejercicio01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Ejercicio01 {
    private Ejercicio01() {
    }

    public static List<String> ejecutar() throws InterruptedException {
        List<String> mensajes = Collections.synchronizedList(new ArrayList<>());
        Thread comprobantes = new Thread(new GeneradorComprobantes(mensajes::add), "hilo-comprobantes");
        Thread notificaciones = new Thread(new EnviadorNotificaciones(mensajes::add), "hilo-notificaciones");

        comprobantes.start();
        notificaciones.start();
        comprobantes.join();
        notificaciones.join();
        mensajes.add("Proceso completo");
        return List.copyOf(mensajes);
    }

    public static String demostrarDobleInicio() throws InterruptedException {
        Thread hilo = new Thread(() -> { }, "hilo-unico");
        hilo.start();
        hilo.join();
        try {
            hilo.start();
            return "No se produjo la excepcion esperada";
        } catch (IllegalThreadStateException e) {
            return "Un Thread no puede iniciarse dos veces: " + e.getClass().getSimpleName();
        }
    }

    public static void mostrar() throws InterruptedException {
        ejecutar().forEach(System.out::println);
        System.out.println(demostrarDobleInicio());
        System.out.println("Nota: run() directo usa el hilo actual; start() crea un hilo nuevo.");
    }
}
