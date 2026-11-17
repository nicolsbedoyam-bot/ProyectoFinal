package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticaAvanzadaTest {

    private static final Logger LOG = Logger.getLogger(EstadisticaAvanzadaTest.class.getName());

    private Empresa empresa;
    private EstadisticaAvanzada estadisticas;
    private Apartamento apartamento;
    private Huesped titular;
    private LocalDate entrada;
    private LocalDate salida;

    @BeforeEach
    void preparar() {
        empresa = new Empresa("900", "Aparta-Suites UQ", "Salento");
        estadisticas = new EstadisticaAvanzada(empresa);
        apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(apartamento);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 100000);
        titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        empresa.registrarHuesped(titular);
        entrada = LocalDate.now().plusDays(20);
        salida = entrada.plusDays(3);
    }

    private LinkedList<Ocupante> dosAdultos() {
        LinkedList<Ocupante> lista = new LinkedList<>();
        lista.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        lista.add(new Ocupante("Luis", LocalDate.of(1992, 1, 1)));
        return lista;
    }

    @DisplayName("Ocupación por apartamento: noches ocupadas frente a las noches del periodo")
    @Test
    void testOcupacionPorApartamento() {
        LOG.info("Iniciando prueba de ocupación por apartamento...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());

        //Una reserva Pendiente todavía no cuenta como ocupación
        assertTrue(estadisticas.ocupacionPorApartamento(entrada, entrada.plusDays(5)).contains("0 noches ocupadas de 6"));

        reserva.confirmar();
        String reporte = estadisticas.ocupacionPorApartamento(entrada, entrada.plusDays(5));

        assertTrue(reporte.contains("A1 - Suite 1: 3 noches ocupadas de 6 (50 %)"));

        LOG.info("Finalizando prueba de ocupación por apartamento...");
    }

    @DisplayName("Ocupación: solo se cuentan las noches que caen dentro del periodo")
    @Test
    void testOcupacionParcialDelPeriodo() {
        LOG.info("Iniciando prueba de ocupación parcial...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());
        reserva.confirmar();

        //El periodo empieza en la segunda noche de la reserva: quedan 2 noches dentro
        String reporte = estadisticas.ocupacionPorApartamento(entrada.plusDays(1), entrada.plusDays(4));

        assertTrue(reporte.contains("2 noches ocupadas de 4 (50 %)"));

        LOG.info("Finalizando prueba de ocupación parcial...");
    }

    @DisplayName("Ingresos por periodo: pagos recibidos menos devoluciones")
    @Test
    void testIngresosPorPeriodo() {
        LOG.info("Iniciando prueba de ingresos...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());
        empresa.registrarPago(reserva, 150000, MedioPago.EFECTIVO);

        String hoy = estadisticas.ingresosPorPeriodo(LocalDate.now(), LocalDate.now());
        assertTrue(hoy.contains(reserva.getCodigo()));
        assertTrue(hoy.contains("TOTAL: $150.000"));

        String otroPeriodo = estadisticas.ingresosPorPeriodo(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        assertTrue(otroPeriodo.contains("TOTAL: $0"));

        LOG.info("Finalizando prueba de ingresos...");
    }

    @DisplayName("Reservas por canal: incluye todos los estados, también canceladas y no-show")
    @Test
    void testReservasPorCanal() {
        LOG.info("Iniciando prueba de reservas por canal...");

        Reserva directa = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());
        directa.confirmar();
        Reserva portal = empresa.crearReservaPortal(titular, apartamento, salida, salida.plusDays(2), dosAdultos());
        portal.cancelar();

        String reporte = estadisticas.reservasPorCanal();

        assertTrue(reporte.contains("DIRECTA: 1 reservas"));
        assertTrue(reporte.contains("PORTAL: 1 reservas"));
        assertTrue(reporte.contains("EXTERNA: 0 reservas"));
        assertTrue(reporte.contains("CONFIRMADA: 1"));
        assertTrue(reporte.contains("CANCELADA: 1"));
        assertTrue(reporte.contains("NO_SHOW: 0"));

        LOG.info("Finalizando prueba de reservas por canal...");
    }

    @DisplayName("Saldos pendientes: reservas cuyo folio todavía debe dinero")
    @Test
    void testSaldosPendientes() {
        LOG.info("Iniciando prueba de saldos pendientes...");

        assertTrue(estadisticas.saldosPendientes().contains("TOTAL PENDIENTE: $0"));

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());
        assertTrue(estadisticas.saldosPendientes().contains("SALDO $600.000"));

        empresa.registrarPago(reserva, 600000, MedioPago.TRANSFERENCIA);
        assertFalse(estadisticas.saldosPendientes().contains(reserva.getCodigo()));
        assertTrue(estadisticas.saldosPendientes().contains("TOTAL PENDIENTE: $0"));

        LOG.info("Finalizando prueba de saldos pendientes...");
    }

    @DisplayName("Apartamentos con más novedades: de mayor a menor")
    @Test
    void testApartamentosConMasNovedades() {
        LOG.info("Iniciando prueba de novedades por apartamento...");

        Apartamento segundo = new Apartamento("A2", "Suite 2", 1, 2, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(segundo);
        empresa.registrarNovedad(new Novedad("Llave dañada", LocalDate.now(), TipoNovedad.DANIO, apartamento, null));
        empresa.registrarNovedad(new Novedad("Falta una toalla", LocalDate.now(), TipoNovedad.FALTANTE, segundo, null));
        empresa.registrarNovedad(new Novedad("Bombillo fundido", LocalDate.now(), TipoNovedad.DANIO, segundo, null));

        String reporte = estadisticas.apartamentosConMasNovedades();

        assertTrue(reporte.contains("A2 - Suite 2: 2 novedades"));
        assertTrue(reporte.contains("A1 - Suite 1: 1 novedades"));
        assertTrue(reporte.indexOf("A2 - Suite 2") < reporte.indexOf("A1 - Suite 1"));

        LOG.info("Finalizando prueba de novedades por apartamento...");
    }

    @DisplayName("Sin novedades el reporte no lista apartamentos")
    @Test
    void testSinNovedades() {
        LOG.info("Iniciando prueba sin novedades...");

        assertFalse(estadisticas.apartamentosConMasNovedades().contains("A1 - Suite 1"));

        LOG.info("Finalizando prueba sin novedades...");
    }

    @DisplayName("Ocupación por temporada: noches ocupadas frente a noches disponibles")
    @Test
    void testOcupacionPorTemporada() {
        LOG.info("Iniciando prueba de ocupación por temporada...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());
        reserva.confirmar();

        String reporte = estadisticas.ocupacionPorTemporada(entrada, salida.minusDays(1));

        assertTrue(reporte.contains("BAJA: 3 noches ocupadas de 3 (100 %)"));
        assertTrue(reporte.contains("MEDIA: 0 noches ocupadas de 0 (0 %)"));
        assertTrue(reporte.contains("ALTA: 0 noches ocupadas de 0 (0 %)"));

        LOG.info("Finalizando prueba de ocupación por temporada...");
    }

    @DisplayName("Cancelaciones: cantidad, porcentaje, devuelto y retenido")
    @Test
    void testComportamientoCancelaciones() {
        LOG.info("Iniciando prueba de cancelaciones...");

        //Cancelada con más de 7 días de antelación: se devuelve todo lo pagado
        Reserva cancelada = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, dosAdultos());
        empresa.registrarPago(cancelada, 200000, MedioPago.EFECTIVO);
        cancelada.cancelar();
        Reserva vigente = empresa.crearReservaDirecta(titular, apartamento, salida, salida.plusDays(2), dosAdultos());

        String reporte = estadisticas.comportamientoCancelaciones();

        assertTrue(reporte.contains("Reservas totales: 2"));
        assertTrue(reporte.contains("Canceladas: 1 (50 %)"));
        assertTrue(reporte.contains("No-show: 0 (0 %)"));
        assertTrue(reporte.contains("Canceladas por el canal DIRECTA: 1"));
        assertTrue(reporte.contains("Total devuelto: $200.000"));
        assertTrue(reporte.contains("Total retenido: $0"));
        assertTrue(reporte.contains("Antelación promedio al cancelar: 20 días"));
        assertEquals(EstadoReserva.PENDIENTE, vigente.getEstado());

        LOG.info("Finalizando prueba de cancelaciones...");
    }

    @DisplayName("Sin reservas los porcentajes son cero (no hay división entre cero)")
    @Test
    void testSinReservas() {
        LOG.info("Iniciando prueba sin reservas...");

        String reporte = estadisticas.comportamientoCancelaciones();

        assertTrue(reporte.contains("Reservas totales: 0"));
        assertTrue(reporte.contains("Canceladas: 0 (0 %)"));

        LOG.info("Finalizando prueba sin reservas...");
    }

    @DisplayName("Los reportes por periodo rechazan un rango de fechas inválido")
    @Test
    void testRangoInvalido() {
        LOG.info("Iniciando prueba de rango inválido...");

        try {
            estadisticas.ocupacionPorApartamento(salida, entrada);
            fail("Un rango con el fin antes del inicio debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("rango"));
        }

        try {
            estadisticas.ingresosPorPeriodo(null, entrada);
            fail("Una fecha nula debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("rango"));
        }

        try {
            estadisticas.ocupacionPorTemporada(salida, entrada);
            fail("Un rango con el fin antes del inicio debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("rango"));
        }

        LOG.info("Finalizando prueba de rango inválido...");
    }
}
