package com.concurrencias_e_hilos;

import com.concurrencias_e_hilos.ejercicio01.Ejercicio01;
import com.concurrencias_e_hilos.ejercicio02.ReporteConcurrente;
import com.concurrencias_e_hilos.ejercicio02.ResultadoReporte;
import com.concurrencias_e_hilos.ejercicio03.EstadisticasParticiones;
import com.concurrencias_e_hilos.ejercicio03.ResultadoEstadisticas;
import com.concurrencias_e_hilos.ejercicio04.*;
import com.concurrencias_e_hilos.ejercicio05.*;
import com.concurrencias_e_hilos.ejercicio06.ContadorPalabras;
import com.concurrencias_e_hilos.ejercicio07.*;
import com.concurrencias_e_hilos.ejercicio08.ResultadoCancelacion;
import com.concurrencias_e_hilos.ejercicio08.DemostracionCancelacion;
import com.concurrencias_e_hilos.ejercicio09.*;
import com.concurrencias_e_hilos.ejercicio10.*;
import com.concurrencias_e_hilos.ejercicio11.*;
import com.concurrencias_e_hilos.ejercicio12.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TallerConcurrenciaTest {
    @Test
    void ejercicio01ConservaOrdenesYMensajeFinal() throws Exception {
        List<String> mensajes = Ejercicio01.ejecutar();
        assertEquals(11, mensajes.size());
        assertEquals("Proceso completo", mensajes.get(10));
        int indiceComprobanteAnterior = -1;
        int indiceNotificacionAnterior = -1;
        for (int i = 1; i <= 5; i++) {
            int indiceComprobante = buscarIndice(mensajes, "Comprobante " + i + " ");
            int indiceNotificacion = buscarIndice(mensajes, "Notificacion " + i + " ");
            assertTrue(indiceComprobante > indiceComprobanteAnterior);
            assertTrue(indiceNotificacion > indiceNotificacionAnterior);
            indiceComprobanteAnterior = indiceComprobante;
            indiceNotificacionAnterior = indiceNotificacion;
        }
        assertTrue(Ejercicio01.demostrarDobleInicio().contains("IllegalThreadStateException"));
    }

    @Test
    void ejercicio02CalculaReporteCompleto() throws Exception {
        assertEquals(new ResultadoReporte(100, 30, 7), ReporteConcurrente.ejecutarDatosDelTaller());
        assertThrows(IllegalArgumentException.class,
                () -> new ReporteConcurrente().calcular(List.of()));
    }

    @Test
    void ejercicio03CombinaParticionesSinCompartirAcumuladores() throws Exception {
        EstadisticasParticiones servicio = new EstadisticasParticiones();
        ResultadoEstadisticas esperado = new ResultadoEstadisticas(5_000, 25_005_000L, 10_000);
        assertEquals(esperado, servicio.calcularDelUnoAlDiezMil(4));
        assertEquals(esperado, servicio.calcularDelUnoAlDiezMil(3));
        assertEquals(new ResultadoEstadisticas(1, 2, 2), servicio.calcular(List.of(1, 2), 5));
    }

    @Test
    void ejercicio04CorrigeContadorYDemuestraLaCarrera() throws Exception {
        DemostracionContadores demo = new DemostracionContadores();
        assertEquals(200_000, demo.ejecutar(new ContadorSincronizado(), 4, 50_000));
        assertEquals(200_000, demo.ejecutar(new ContadorAtomico(), 4, 50_000));
        assertEquals(20, demo.ejecutarVeinteVecesInseguro().size());
        assertEquals(1, assertTimeout(Duration.ofSeconds(2), demo::demostrarPerdidaDeterminista));
    }

    @Test
    void ejercicio05SoloConfirmaUnaReservaPorAsiento() throws Exception {
        ServicioReservas servicio = new ServicioReservas();
        for (int clientes : new int[]{50, 100}) {
            List<ResultadoReserva> resultados = servicio.ejecutar(clientes);
            assertEquals(10, resultados.stream()
                    .filter(r -> r.estado() == ResultadoReserva.Estado.CONFIRMADA).count());
            assertEquals(clientes - 10, resultados.stream()
                    .filter(r -> r.estado() == ResultadoReserva.Estado.OCUPADO).count());
        }
        Sala sala = new Sala(10);
        assertEquals(ResultadoReserva.Estado.INVALIDO, sala.reservar(0));
        assertEquals(ResultadoReserva.Estado.INVALIDO, sala.reservar(11));
        assertEquals(0, sala.cantidadReservada());
    }

    @Test
    void ejercicio06CuentaYOrdenaPalabras() throws Exception {
        ContadorPalabras contador = new ContadorPalabras();
        Map<String, Integer> resultado = contador.contarDocumentosDelTaller();
        assertEquals(Map.of("colecciones", 1, "hilos", 2, "java", 4, "poo", 2, "solid", 2), resultado);
        assertEquals(List.of("colecciones", "hilos", "java", "poo", "solid"),
                List.copyOf(resultado.keySet()));
        assertEquals(11, contador.total(resultado));
    }

    @Test
    void ejercicio07RegistraTodosLosProveedores() throws Exception {
        ServicioCotizaciones servicio = new ServicioCotizaciones();
        for (InformeCotizaciones informe : List.of(servicio.consultar(),
                servicio.consultarPorOrdenDeFinalizacion())) {
            assertEquals(6, informe.exitosas().size());
            assertEquals(Set.of(3, 7), informe.errores().keySet());
            assertEquals(8, informe.totalResultados());
            assertEquals(new Cotizacion(1, 101_000), informe.mejor());
            Set<Integer> ids = new HashSet<>(informe.errores().keySet());
            informe.exitosas().forEach(c -> assertTrue(ids.add(c.proveedor())));
            assertEquals(8, ids.size());
        }
    }

    @Test
    void ejercicio08CancelaYEsperaLaTerminacionReal() throws Exception {
        ResultadoCancelacion resultado = assertTimeout(Duration.ofSeconds(2),
                () -> new DemostracionCancelacion().ejecutar());
        assertTrue(resultado.cancelacionAceptada());
        assertTrue(resultado.interrupcionRecibida());
        assertTrue(resultado.limpiezaEjecutada());
        assertTrue(resultado.cuerpoFinalizado());
    }

    @Test
    void ejercicio09ProcesaUnaVezYSeRecuperaDelFallo() throws Exception {
        CentroImpresion centro = new CentroImpresion();
        InformeImpresion normal = assertTimeout(Duration.ofSeconds(5), () -> centro.procesar(false));
        assertEquals(20, normal.completados().size());
        assertTrue(normal.fallidos().isEmpty());
        assertTrue(normal.sinCompletar().isEmpty());
        assertEquals(0, normal.duplicados());

        InformeImpresion conFallo = assertTimeout(Duration.ofSeconds(5), () -> centro.procesar(true));
        assertEquals(Set.of(7), conFallo.fallidos());
        assertEquals(20, conFallo.completados().size() + conFallo.fallidos().size()
                + conFallo.sinCompletar().size());
        assertEquals(0, conFallo.duplicados());
    }

    @Test
    void ejercicio10ConservaSaldosSinDeadlock() throws Exception {
        ResultadoTransferencias resultado = assertTimeout(Duration.ofSeconds(5),
                () -> new ServicioTransferencias().ejecutarTaller());
        assertEquals(new ResultadoTransferencias(100_000, 100_000, 200_000), resultado);
        Cuenta cuenta = new Cuenta("X", 100);
        ServicioTransferencias servicio = new ServicioTransferencias();
        assertThrows(IllegalArgumentException.class, () -> servicio.transferir(cuenta, cuenta, 10));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.transferir(cuenta, new Cuenta("Y", 100), 0));
    }

    @Test
    void ejercicio11RespetaElLimiteYRecuperaPermisos() throws Exception {
        ServicioExternoLimitado servicio = new ServicioExternoLimitado();
        InformeServicio informe = servicio.ejecutar();
        assertEquals(26, informe.exitos());
        assertEquals(4, informe.errores());
        assertEquals(30, informe.total());
        assertTrue(informe.maximoSimultaneo() <= 3);
        assertEquals(0, informe.activasAlFinal());
        assertEquals(3, informe.permisosAlFinal());
        ResultadoCancelacionPermiso cancelacion = servicio.cancelarMientrasEspera();
        assertTrue(cancelacion.cancelada());
        assertTrue(cancelacion.finalizada());
        assertEquals(1, cancelacion.permisosFinales());
    }

    @Test
    void ejercicio12CumpleReglasConservacionYResultadosUnicos() throws Exception {
        GeneradorPedidos generador = new GeneradorPedidos();
        List<Pedido> pedidos = generador.generar(200);
        assertEquals(new Pedido(20, "P1", 0), pedidos.get(19));
        assertEquals(new Pedido(15, "P9", 1), pedidos.get(14));

        SimulacionTienda simulacion = new SimulacionTienda();
        verificarFaseSinFallos(simulacion.ejecutarSecuencial(Set.of()));
        verificarFaseSinFallos(simulacion.ejecutarConcurrente(8, Set.of()));

        InformeTienda fallos = simulacion.ejecutarConcurrente(8, SimulacionTienda.FALLOS_TECNICOS);
        assertEquals(43, fallos.cantidad(EstadoPedido.CONFIRMADO));
        assertEquals(154, fallos.cantidad(EstadoPedido.RECHAZADO));
        assertEquals(3, fallos.cantidad(EstadoPedido.ERROR));
        assertEquals(Map.of("P1", 0, "P2", 0, "P3", 1), fallos.stockFinal());
    }

    private void verificarFaseSinFallos(InformeTienda informe) {
        assertEquals(200, informe.resultados().size());
        assertEquals(43, informe.cantidad(EstadoPedido.CONFIRMADO));
        assertEquals(157, informe.cantidad(EstadoPedido.RECHAZADO));
        assertEquals(0, informe.cantidad(EstadoPedido.ERROR));
        assertEquals(10, informe.cantidad(CausaResultado.CANTIDAD_INVALIDA));
        assertEquals(10, informe.cantidad(CausaResultado.PRODUCTO_INEXISTENTE));
        assertEquals(30, informe.confirmadosDe("P1"));
        assertEquals(10, informe.confirmadosDe("P2"));
        assertEquals(3, informe.confirmadosDe("P3"));
        assertEquals(Map.of("P1", 0, "P2", 0, "P3", 1), informe.stockFinal());
        assertEquals(200, informe.resultados().stream().map(ResultadoPedido::pedidoId).distinct().count());
        int vendido = informe.resultados().stream()
                .filter(r -> r.estado() == EstadoPedido.CONFIRMADO)
                .mapToInt(ResultadoPedido::cantidad).sum();
        assertEquals(59, vendido);
    }

    private int buscarIndice(List<String> mensajes, String inicio) {
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).startsWith(inicio)) return i;
        }
        return -1;
    }
}
