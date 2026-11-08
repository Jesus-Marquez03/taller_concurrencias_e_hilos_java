package com.concurrencias_e_hilos.ejercicio05;

public record ResultadoReserva(int cliente, int asiento, Estado estado) {
    public enum Estado { CONFIRMADA, OCUPADO, INVALIDO }
}
