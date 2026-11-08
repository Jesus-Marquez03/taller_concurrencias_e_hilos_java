package com.concurrencias_e_hilos;

import com.concurrencias_e_hilos.ejercicio01.Ejercicio01;
import com.concurrencias_e_hilos.ejercicio02.ReporteConcurrente;
import com.concurrencias_e_hilos.ejercicio03.EstadisticasParticiones;
import com.concurrencias_e_hilos.ejercicio04.*;
import com.concurrencias_e_hilos.ejercicio05.ResultadoReserva;
import com.concurrencias_e_hilos.ejercicio05.ServicioReservas;
import com.concurrencias_e_hilos.ejercicio06.ContadorPalabras;
import com.concurrencias_e_hilos.ejercicio07.ServicioCotizaciones;
import com.concurrencias_e_hilos.ejercicio08.DemostracionCancelacion;
import com.concurrencias_e_hilos.ejercicio09.CentroImpresion;
import com.concurrencias_e_hilos.ejercicio10.ServicioTransferencias;
import com.concurrencias_e_hilos.ejercicio11.ServicioExternoLimitado;
import com.concurrencias_e_hilos.ejercicio12.InformeTienda;
import com.concurrencias_e_hilos.ejercicio12.SimulacionTienda;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int opcion = -1;
            while (opcion != 0) {
                mostrarMenu();
                String entrada = scanner.nextLine();
                try {
                    opcion = Integer.parseInt(entrada);
                    if (opcion != 0) ejecutar(opcion);
                } catch (NumberFormatException e) {
                    System.out.println("Ingrese un numero del 0 al 12.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("La ejecucion fue interrumpida.");
                    return;
                } catch (Exception e) {
                    System.out.println("No se pudo ejecutar: " + e.getMessage());
                }
                System.out.println();
            }
        }
        System.out.println("Programa finalizado.");
    }

    private static void mostrarMenu() {
        System.out.println("====================================");
        System.out.println(" TALLER DE CONCURRENCIA EN JAVA");
        System.out.println("====================================");
        System.out.println("1. Dos tareas independientes");
        System.out.println("2. Reporte concurrente");
        System.out.println("3. Estadisticas por particiones");
        System.out.println("4. Contador de visitas");
        System.out.println("5. Reserva de asientos");
        System.out.println("6. Conteo de palabras");
        System.out.println("7. Cotizaciones");
        System.out.println("8. Cancelacion de tareas");
        System.out.println("9. Centro de impresion");
        System.out.println("10. Transferencias bancarias");
        System.out.println("11. Acceso limitado a servicio");
        System.out.println("12. Tienda concurrente");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    private static void ejecutar(int opcion) throws Exception {
        switch (opcion) {
            case 1 -> Ejercicio01.mostrar();
            case 2 -> System.out.println(ReporteConcurrente.ejecutarDatosDelTaller());
            case 3 -> {
                EstadisticasParticiones servicio = new EstadisticasParticiones();
                System.out.println("4 trabajadores: " + servicio.calcularDelUnoAlDiezMil(4));
                System.out.println("3 trabajadores: " + servicio.calcularDelUnoAlDiezMil(3));
            }
            case 4 -> mostrarContadores();
            case 5 -> mostrarReservas();
            case 6 -> {
                ContadorPalabras contador = new ContadorPalabras();
                Map<String, Integer> resultado = contador.contarDocumentosDelTaller();
                resultado.forEach((palabra, cantidad) -> System.out.println(palabra + " = " + cantidad));
                System.out.println("Total = " + contador.total(resultado));
            }
            case 7 -> System.out.println(new ServicioCotizaciones().consultar());
            case 8 -> System.out.println(new DemostracionCancelacion().ejecutar());
            case 9 -> {
                CentroImpresion centro = new CentroImpresion();
                System.out.println("Fase normal: " + centro.procesar(false));
                System.out.println("Fase con fallo: " + centro.procesar(true));
            }
            case 10 -> System.out.println(new ServicioTransferencias().ejecutarTaller());
            case 11 -> {
                ServicioExternoLimitado servicio = new ServicioExternoLimitado();
                System.out.println(servicio.ejecutar());
                System.out.println("Cancelacion esperando permiso: " + servicio.cancelarMientrasEspera());
            }
            case 12 -> mostrarTienda();
            default -> System.out.println("Opcion fuera del rango 0 a 12.");
        }
    }

    private static void mostrarContadores() throws Exception {
        DemostracionContadores demo = new DemostracionContadores();
        System.out.println("Esperado por ejecucion: 200000");
        System.out.println("Inseguro (20 ejecuciones): " + demo.ejecutarVeinteVecesInseguro());
        System.out.println("Sincronizado: " + demo.ejecutar(new ContadorSincronizado(), 4, 50_000));
        System.out.println("Atomico: " + demo.ejecutar(new ContadorAtomico(), 4, 50_000));
        System.out.println("Volatile (no garantiza atomicidad): "
                + demo.ejecutar(new ContadorVolatile(), 4, 50_000));
        System.out.println("Carrera coordinada, esperado 2 y observado: "
                + demo.demostrarPerdidaDeterminista());
    }

    private static void mostrarReservas() throws InterruptedException {
        ServicioReservas servicio = new ServicioReservas();
        for (int clientes : new int[]{50, 100}) {
            var resultados = servicio.ejecutar(clientes);
            long confirmadas = resultados.stream()
                    .filter(r -> r.estado() == ResultadoReserva.Estado.CONFIRMADA).count();
            System.out.println(clientes + " clientes: " + confirmadas + " confirmadas y "
                    + (clientes - confirmadas) + " rechazadas");
        }
    }

    private static void mostrarTienda() throws InterruptedException {
        SimulacionTienda simulacion = new SimulacionTienda();
        imprimirInforme("Secuencial sin fallos", simulacion.ejecutarSecuencial(Set.of()));
        imprimirInforme("Concurrente sin fallos", simulacion.ejecutarConcurrente(8, Set.of()));
        imprimirInforme("Concurrente con fallos " + SimulacionTienda.FALLOS_TECNICOS,
                simulacion.ejecutarConcurrente(8, SimulacionTienda.FALLOS_TECNICOS));
    }

    private static void imprimirInforme(String titulo, InformeTienda informe) {
        System.out.println(titulo + ": pedidos=" + informe.resultados().size()
                + ", confirmados=" + informe.cantidad(com.concurrencias_e_hilos.ejercicio12.EstadoPedido.CONFIRMADO)
                + ", rechazados=" + informe.cantidad(com.concurrencias_e_hilos.ejercicio12.EstadoPedido.RECHAZADO)
                + ", errores=" + informe.cantidad(com.concurrencias_e_hilos.ejercicio12.EstadoPedido.ERROR)
                + ", stock=" + informe.stockFinal());
    }
}
