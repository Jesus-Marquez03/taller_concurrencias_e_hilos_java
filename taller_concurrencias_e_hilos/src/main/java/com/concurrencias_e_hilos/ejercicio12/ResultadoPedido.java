package com.concurrencias_e_hilos.ejercicio12;

public record ResultadoPedido(int pedidoId, String producto, int cantidad,
                              EstadoPedido estado, CausaResultado causa) {
}
