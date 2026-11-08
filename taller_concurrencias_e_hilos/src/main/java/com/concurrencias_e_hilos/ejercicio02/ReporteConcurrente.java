package com.concurrencias_e_hilos.ejercicio02;

import java.util.List;

public class ReporteConcurrente {
    public ResultadoReporte calcular(List<Integer> entrada) throws InterruptedException {
        if (entrada == null || entrada.isEmpty()) {
            throw new IllegalArgumentException("La entrada no puede estar vacia");
        }
        List<Integer> datos = List.copyOf(entrada);
        TareaSuma suma = new TareaSuma(datos);
        TareaMaximo maximo = new TareaMaximo(datos);
        TareaMinimo minimo = new TareaMinimo(datos);
        Thread hiloSuma = new Thread(suma, "calculo-suma");
        Thread hiloMaximo = new Thread(maximo, "calculo-maximo");
        Thread hiloMinimo = new Thread(minimo, "calculo-minimo");

        hiloSuma.start();
        hiloMaximo.start();
        hiloMinimo.start();
        hiloSuma.join();
        hiloMaximo.join();
        hiloMinimo.join();
        return new ResultadoReporte(suma.resultado, maximo.resultado, minimo.resultado);
    }

    public static ResultadoReporte ejecutarDatosDelTaller() throws InterruptedException {
        return new ReporteConcurrente().calcular(List.of(12, 7, 25, 9, 17, 30));
    }

    private static class TareaSuma implements Runnable {
        private final List<Integer> datos;
        private int resultado;

        TareaSuma(List<Integer> datos) {
            this.datos = datos;
        }

        @Override
        public void run() {
            for (int numero : datos) {
                resultado += numero;
            }
        }
    }

    private static class TareaMaximo implements Runnable {
        private final List<Integer> datos;
        private int resultado;

        TareaMaximo(List<Integer> datos) {
            this.datos = datos;
        }

        @Override
        public void run() {
            resultado = datos.get(0);
            for (int numero : datos) {
                if (numero > resultado) resultado = numero;
            }
        }
    }

    private static class TareaMinimo implements Runnable {
        private final List<Integer> datos;
        private int resultado;

        TareaMinimo(List<Integer> datos) {
            this.datos = datos;
        }

        @Override
        public void run() {
            resultado = datos.get(0);
            for (int numero : datos) {
                if (numero < resultado) resultado = numero;
            }
        }
    }
}
