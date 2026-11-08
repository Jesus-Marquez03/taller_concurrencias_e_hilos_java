package com.concurrencias_e_hilos.ejercicio09;

import java.util.Set;

public record InformeImpresion(Set<Integer> completados, Set<Integer> fallidos,
                               Set<Integer> sinCompletar, int duplicados) {
}
