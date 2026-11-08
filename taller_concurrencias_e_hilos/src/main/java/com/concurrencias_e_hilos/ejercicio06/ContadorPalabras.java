package com.concurrencias_e_hilos.ejercicio06;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ContadorPalabras {
    public Map<String, Integer> contar(List<String> documentos) throws InterruptedException {
        ConcurrentHashMap<String, Integer> conteos = new ConcurrentHashMap<>();
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < documentos.size(); i++) {
            String documento = documentos.get(i);
            Thread hilo = new Thread(() -> procesar(documento, conteos), "documento-" + (i + 1));
            hilos.add(hilo);
            hilo.start();
        }
        for (Thread hilo : hilos) hilo.join();
        return new TreeMap<>(conteos);
    }

    private void procesar(String documento, ConcurrentHashMap<String, Integer> conteos) {
        String texto = documento.toLowerCase(Locale.ROOT).trim();
        if (texto.isEmpty()) return;
        for (String palabra : texto.split("\\s+")) conteos.merge(palabra, 1, Integer::sum);
    }

    public Map<String, Integer> contarDocumentosDelTaller() throws InterruptedException {
        return contar(List.of(
                "java hilos java",
                "poo java solid",
                "hilos colecciones java",
                "solid poo"));
    }

    public int total(Map<String, Integer> conteos) {
        int total = 0;
        for (int cantidad : conteos.values()) total += cantidad;
        return total;
    }
}
