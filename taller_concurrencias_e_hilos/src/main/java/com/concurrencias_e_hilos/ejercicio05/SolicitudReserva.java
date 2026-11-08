package com.concurrencias_e_hilos.ejercicio05;

import java.util.concurrent.Callable;

public class SolicitudReserva implements Callable<ResultadoReserva> {
    private final int cliente;
    private final int asiento;
    private final Sala sala;

    public SolicitudReserva(int cliente, int asiento, Sala sala) {
        this.cliente = cliente;
        this.asiento = asiento;
        this.sala = sala;
    }

    @Override
    public ResultadoReserva call() {
        return new ResultadoReserva(cliente, asiento, sala.reservar(asiento));
    }
}
