package com.concurrencias_e_hilos.ejercicio08;

public record ResultadoCancelacion(boolean cancelacionAceptada, boolean interrupcionRecibida,
                                   boolean limpiezaEjecutada, boolean cuerpoFinalizado) {
}
