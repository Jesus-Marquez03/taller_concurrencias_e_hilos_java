package com.concurrencias_e_hilos.ejercicio12;

import java.util.List;
import java.util.Map;

public record InformeTienda(List<ResultadoPedido> resultados, Map<String, Integer> stockFinal) {
    public long cantidad(EstadoPedido estado) {
        return resultados.stream().filter(r -> r.estado() == estado).count();
    }

    public long cantidad(CausaResultado causa) {
        return resultados.stream().filter(r -> r.causa() == causa).count();
    }

    public long confirmadosDe(String producto) {
        return resultados.stream()
                .filter(r -> r.estado() == EstadoPedido.CONFIRMADO && r.producto().equals(producto))
                .count();
    }
}
