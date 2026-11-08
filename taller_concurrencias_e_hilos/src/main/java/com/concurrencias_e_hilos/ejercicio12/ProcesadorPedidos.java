package com.concurrencias_e_hilos.ejercicio12;

import java.util.Set;

public class ProcesadorPedidos {
    private final ValidadorPedido validador;
    private final Inventario inventario;
    private final Set<Integer> pedidosConFallo;

    public ProcesadorPedidos(ValidadorPedido validador, Inventario inventario,
                             Set<Integer> pedidosConFallo) {
        this.validador = validador;
        this.inventario = inventario;
        this.pedidosConFallo = Set.copyOf(pedidosConFallo);
    }

    public ResultadoPedido procesar(Pedido pedido) {
        CausaResultado validacion = validador.validar(pedido);
        if (validacion != CausaResultado.NINGUNA) {
            return new ResultadoPedido(pedido.id(), pedido.producto(), pedido.cantidad(),
                    EstadoPedido.RECHAZADO, validacion);
        }
        if (pedidosConFallo.contains(pedido.id())) {
            return new ResultadoPedido(pedido.id(), pedido.producto(), pedido.cantidad(),
                    EstadoPedido.ERROR, CausaResultado.FALLA_TECNICA);
        }
        if (inventario.reservar(pedido.producto(), pedido.cantidad())) {
            return new ResultadoPedido(pedido.id(), pedido.producto(), pedido.cantidad(),
                    EstadoPedido.CONFIRMADO, CausaResultado.NINGUNA);
        }
        return new ResultadoPedido(pedido.id(), pedido.producto(), pedido.cantidad(),
                EstadoPedido.RECHAZADO, CausaResultado.STOCK_INSUFICIENTE);
    }
}
