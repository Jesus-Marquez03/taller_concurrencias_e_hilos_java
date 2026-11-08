package com.concurrencias_e_hilos.ejercicio11;

public record InformeServicio(int exitos, int errores, int total, int maximoSimultaneo,
                              int activasAlFinal, int permisosAlFinal) {
}
