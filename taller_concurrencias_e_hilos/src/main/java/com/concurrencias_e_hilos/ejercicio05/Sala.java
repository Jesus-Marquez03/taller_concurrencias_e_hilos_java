package com.concurrencias_e_hilos.ejercicio05;

public class Sala {
    private final boolean[] asientos;

    public Sala(int cantidadAsientos) {
        if (cantidadAsientos <= 0) throw new IllegalArgumentException("La sala debe tener asientos");
        this.asientos = new boolean[cantidadAsientos];
    }

    public synchronized ResultadoReserva.Estado reservar(int asiento) {
        if (asiento < 1 || asiento > asientos.length) return ResultadoReserva.Estado.INVALIDO;
        if (asientos[asiento - 1]) return ResultadoReserva.Estado.OCUPADO;
        asientos[asiento - 1] = true;
        return ResultadoReserva.Estado.CONFIRMADA;
    }

    public synchronized int cantidadReservada() {
        int total = 0;
        for (boolean reservado : asientos) if (reservado) total++;
        return total;
    }
}
