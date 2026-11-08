package com.concurrencias_e_hilos.ejercicio07;

import java.util.List;
import java.util.Map;

public record InformeCotizaciones(List<Cotizacion> exitosas, Map<Integer, String> errores) {
    public Cotizacion mejor() {
        return exitosas.stream()
                .min((a, b) -> Long.compare(a.precio(), b.precio()))
                .orElseThrow(() -> new IllegalStateException("No existen cotizaciones exitosas"));
    }

    public int totalResultados() {
        return exitosas.size() + errores.size();
    }
}
