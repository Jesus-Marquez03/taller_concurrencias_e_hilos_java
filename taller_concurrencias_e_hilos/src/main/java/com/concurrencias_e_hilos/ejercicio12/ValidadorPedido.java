package com.concurrencias_e_hilos.ejercicio12;

import java.util.Set;

public class ValidadorPedido {
    private final Set<String> productosValidos;

    public ValidadorPedido(Set<String> productosValidos) {
        this.productosValidos = Set.copyOf(productosValidos);
    }

    public CausaResultado validar(Pedido pedido) {
        if (pedido.cantidad() <= 0) return CausaResultado.CANTIDAD_INVALIDA;
        if (!productosValidos.contains(pedido.producto())) return CausaResultado.PRODUCTO_INEXISTENTE;
        return CausaResultado.NINGUNA;
    }
}
