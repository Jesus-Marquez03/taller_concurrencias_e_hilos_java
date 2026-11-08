package com.concurrencias_e_hilos.ejercicio09;

public record Documento(int id, String titulo, int paginas, boolean fin) {
    public Documento(int id, String titulo, int paginas) {
        this(id, titulo, paginas, false);
    }

    public static Documento senalFin() {
        return new Documento(-1, "FIN", 0, true);
    }
}
