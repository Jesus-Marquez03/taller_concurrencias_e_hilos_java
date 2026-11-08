package com.concurrencias_e_hilos.ejercicio12;

import java.util.HashMap;
import java.util.Map;

public class InventarioSeguro implements Inventario {
    private final Map<String, Integer> existencias;

    public InventarioSeguro(Map<String, Integer> existenciasIniciales) {
        this.existencias = new HashMap<>(existenciasIniciales);
    }

    @Override
    public synchronized boolean reservar(String producto, int cantidad) {
        Integer disponible = existencias.get(producto);
        if (disponible == null || cantidad <= 0 || disponible < cantidad) return false;
        existencias.put(producto, disponible - cantidad);
        return true;
    }

    @Override
    public synchronized int stock(String producto) {
        return existencias.getOrDefault(producto, 0);
    }

    @Override
    public synchronized Map<String, Integer> existencias() {
        return Map.copyOf(existencias);
    }
}
