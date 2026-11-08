package com.concurrencias_e_hilos.ejercicio12;

import java.util.Map;

public interface Inventario {
    boolean reservar(String producto, int cantidad);
    int stock(String producto);
    Map<String, Integer> existencias();
}
