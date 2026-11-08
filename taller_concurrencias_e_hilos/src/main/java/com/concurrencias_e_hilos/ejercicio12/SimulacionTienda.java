package com.concurrencias_e_hilos.ejercicio12;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimulacionTienda {
    public static final Set<Integer> FALLOS_TECNICOS = Set.of(191, 193, 198);

    public InformeTienda ejecutarSecuencial(Set<Integer> fallos) {
        Inventario inventario = crearInventario();
        ProcesadorPedidos procesador = crearProcesador(inventario, fallos);
        List<ResultadoPedido> resultados = new CoordinadorPedidos().procesarSecuencial(
                new GeneradorPedidos().generar(200), procesador);
        return new InformeTienda(resultados, inventario.existencias());
    }

    public InformeTienda ejecutarConcurrente(int trabajadores, Set<Integer> fallos)
            throws InterruptedException {
        Inventario inventario = crearInventario();
        ProcesadorPedidos procesador = crearProcesador(inventario, fallos);
        List<ResultadoPedido> resultados = new CoordinadorPedidos().procesarConcurrente(
                new GeneradorPedidos().generar(200), procesador, trabajadores);
        return new InformeTienda(resultados, inventario.existencias());
    }

    private Inventario crearInventario() {
        return new InventarioSeguro(Map.of("P1", 30, "P2", 20, "P3", 10));
    }

    private ProcesadorPedidos crearProcesador(Inventario inventario, Set<Integer> fallos) {
        return new ProcesadorPedidos(new ValidadorPedido(Set.of("P1", "P2", "P3")),
                inventario, fallos);
    }
}
