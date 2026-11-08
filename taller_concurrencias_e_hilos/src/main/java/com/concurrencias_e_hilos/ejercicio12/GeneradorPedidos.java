package com.concurrencias_e_hilos.ejercicio12;

import java.util.ArrayList;
import java.util.List;

public class GeneradorPedidos {
    public List<Pedido> generar(int cantidad) {
        List<Pedido> pedidos = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            if (i % 20 == 0) {
                pedidos.add(new Pedido(i, "P1", 0));
            } else if (i % 15 == 0) {
                pedidos.add(new Pedido(i, "P9", 1));
            } else {
                int indice = (i - 1) % 3;
                pedidos.add(new Pedido(i, "P" + (indice + 1), 1 + indice));
            }
        }
        return List.copyOf(pedidos);
    }
}
