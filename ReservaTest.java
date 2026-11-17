package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

//Reserva es abstracta: se prueba con ReservaDirecta (no pide anticipo), que hereda todo lo de Reserva
class ReservaTest {

    private static final Logger LOG = Logger.getLogger(ReservaTest.class.getName());

    private Apartamento apartamento;
    private Huesped titular;
    private LinkedList<Ocupante> ocupantes;

    @BeforeEach
    void preparar() {
        apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        ocupantes = new LinkedList<>();
        ocupantes.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
    }

    //Reserva de 2 noches que entra en la fecha indicada, con el valor ya congelado
    private ReservaDirecta crearReserva(LocalDate entrada, int valor) {
        ReservaDirecta reserva = new ReservaDirecta("R1", entrada, entrada.plusDays(2), apartamento, titular, ocupantes);
        reserva.setValorEstancia(valor);
        return reserva;
    }

    @DisplayName("Toda reserva nace Pendiente, activa y con el folio vacío")
    @Test
    void testNacePendiente() {
        LOG.info("Iniciando prueba de reserva nueva...");

        ReservaDirecta reserva = new ReservaDirecta("R1", LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), apartamento, titular, ocupantes);

        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertTrue(reserva.estaActiva());
        assertEquals("R1", reserva.getCodigo());
        assertEquals(apartamento, reserva.getApartamento());
        assertEquals(titular, reserva.getTitular());
        assertEquals(0, reserva.getFolio().totalCargo());
        assertEquals(0, reserva.getValorEstancia());
        assertNull(reserva.getFechaCancelacion());
        assertNotNull(reserva.getFechaCreacion());

        LOG.info("Finalizando prueba de reserva nueva...");
    }

    @DisplayName("La reserva guarda la política de cancelación: la del enunciado por defecto")
    @Test
    void testPoliticaPorDefecto() {
        LOG.info("Iniciando prueba de política de la reserva...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 100000);
        assertEquals(7, reserva.getPoliticaCancelacion().getDiasDevolucion());
        assertEquals(2, reserva.getPoliticaCancelacion().getDiasRetencion());
        assertEquals(50.0, reserva.getPoliticaCancelacion().getPorcentajeRetencion());

        PoliticaCancelacion otra = new PoliticaCancelacion(10, 3, 40);
        ReservaDirecta conOtra = new ReservaDirecta("R2", LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), apartamento, titular, ocupantes, otra);
        assertEquals(10, conOtra.getPoliticaCancelacion().getDiasDevolucion());

        LOG.info("Finalizando prueba de política de la reserva...");
    }

    @DisplayName("Las noches son las que hay entre la entrada y la salida (la salida no se cuenta)")
    @Test
    void testCalcularNoches() {
        LOG.info("Iniciando prueba de noches...");

        LocalDate entrada = LocalDate.of(2027, 3, 10);
        ReservaDirecta reserva = new ReservaDirecta("R1", entrada, LocalDate.of(2027, 3, 12), apartamento, titular, ocupantes);

        assertEquals(2, reserva.calcularNoches());

        LOG.info("Finalizando prueba de noches...");
    }

    @DisplayName("Valor congelado: el valor se fija una sola vez y queda como cargo de alojamiento")
    @Test
    void testValorCongelado() {
        LOG.info("Iniciando prueba de valor congelado...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 400000);

        assertEquals(400000, reserva.getValorEstancia());
        assertEquals(1, reserva.getFolio().getCargos().size());
        assertEquals(TipoCargo.ALOJAMIENTO, reserva.getFolio().getCargos().get(0).tipo());
        assertEquals(400000, reserva.calcularTotal());

        try {
            reserva.setValorEstancia(500000);
            fail("El valor de la estancia no se puede fijar dos veces");
        } catch (IllegalStateException e) {
            assertEquals(400000, reserva.getValorEstancia());
        }

        LOG.info("Finalizando prueba de valor congelado...");
    }

    @DisplayName("Ciclo de vida: Pendiente, Confirmada, En curso y Finalizada")
    @Test
    void testCicloNormal() {
        LOG.info("Iniciando prueba del ciclo de vida...");

        ReservaDirecta reserva = crearReserva(LocalDate.now(), 200000);

        reserva.confirmar();
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        reserva.hacerCheckIn();
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());
        assertTrue(reserva.estaActiva());

        reserva.hacerCheckOut();
        assertEquals(EstadoReserva.FINALIZADA, reserva.getEstado());
        assertFalse(reserva.estaActiva());

        LOG.info("Finalizando prueba del ciclo de vida...");
    }

    @DisplayName("Cualquier cambio de estado fuera de la tabla se rechaza")
    @Test
    void testTransicionesInvalidas() {
        LOG.info("Iniciando prueba de transiciones inválidas...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 200000);

        try {
            reserva.cambiarEstado(EstadoReserva.EN_CURSO);
            fail("Pendiente no puede pasar a En curso");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        }

        try {
            reserva.cambiarEstado(EstadoReserva.FINALIZADA);
            fail("Pendiente no puede pasar a Finalizada");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        }

        reserva.confirmar();
        try {
            reserva.cambiarEstado(EstadoReserva.PENDIENTE);
            fail("Confirmada no puede volver a Pendiente");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de transiciones inválidas...");
    }

    @DisplayName("Finalizada, Cancelada y No-show son estados finales")
    @Test
    void testEstadosFinales() {
        LOG.info("Iniciando prueba de estados finales...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 200000);
        reserva.cancelar();

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertFalse(reserva.estaActiva());
        try {
            reserva.confirmar();
            fail("Una reserva cancelada no se puede confirmar");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de estados finales...");
    }

    @DisplayName("Check-in: la reserva debe estar Confirmada")
    @Test
    void testCheckInSinConfirmar() {
        LOG.info("Iniciando prueba de check-in sin confirmar...");

        ReservaDirecta reserva = crearReserva(LocalDate.now(), 200000);

        try {
            reserva.hacerCheckIn();
            fail("No se puede hacer check-in si la reserva no está Confirmada");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de check-in sin confirmar...");
    }

    @DisplayName("Check-in: ya debe haber llegado la fecha de entrada")
    @Test
    void testCheckInAntesDeLaFecha() {
        LOG.info("Iniciando prueba de check-in anticipado...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(3), 200000);
        reserva.confirmar();

        try {
            reserva.hacerCheckIn();
            fail("No se puede hacer check-in antes de la fecha de entrada");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de check-in anticipado...");
    }

    @DisplayName("Check-in: el apartamento debe estar Preparado")
    @Test
    void testCheckInApartamentoSinPreparar() {
        LOG.info("Iniciando prueba de check-in con apartamento sin preparar...");

        ReservaDirecta reserva = crearReserva(LocalDate.now(), 200000);
        reserva.confirmar();
        apartamento.actualizarEstadoOperativo(EstadoOperativo.EN_PREPARACION);

        try {
            reserva.hacerCheckIn();
            fail("No se puede hacer check-in si el apartamento no está Preparado");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        }

        apartamento.actualizarEstadoOperativo(EstadoOperativo.PREPARADO);
        reserva.hacerCheckIn();
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());

        LOG.info("Finalizando prueba de check-in con apartamento sin preparar...");
    }

    @DisplayName("El check-in deja el apartamento Ocupado y el check-out Pendiente de preparación")
    @Test
    void testEstadoDelApartamento() {
        LOG.info("Iniciando prueba del estado del apartamento...");

        ReservaDirecta reserva = crearReserva(LocalDate.now(), 200000);
        reserva.confirmar();

        reserva.hacerCheckIn();
        assertEquals(EstadoOperativo.OCUPADO, apartamento.getEstadoOperativo());

        reserva.hacerCheckOut();
        assertEquals(EstadoOperativo.PENDIENTE_PREPARACION, apartamento.getEstadoOperativo());

        LOG.info("Finalizando prueba del estado del apartamento...");
    }

    @DisplayName("El check-out solo se hace con la reserva En curso")
    @Test
    void testCheckOutSinCheckIn() {
        LOG.info("Iniciando prueba de check-out sin check-in...");

        ReservaDirecta reserva = crearReserva(LocalDate.now(), 200000);
        reserva.confirmar();

        try {
            reserva.hacerCheckOut();
            fail("No se puede hacer check-out sin check-in");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de check-out sin check-in...");
    }

    @DisplayName("Una reserva En curso no se puede cancelar")
    @Test
    void testNoSeCancelaEnCurso() {
        LOG.info("Iniciando prueba de cancelar en curso...");

        ReservaDirecta reserva = crearReserva(LocalDate.now(), 200000);
        reserva.confirmar();
        reserva.hacerCheckIn();

        try {
            reserva.cancelar();
            fail("Una reserva En curso no se puede cancelar");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de cancelar en curso...");
    }

    @DisplayName("Cancelar sin pagos: no hay devolución y el folio queda en cero")
    @Test
    void testCancelarSinPagos() {
        LOG.info("Iniciando prueba de cancelar sin pagos...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 400000);
        reserva.cancelar();

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertNotNull(reserva.getFechaCancelacion());
        assertEquals(0, reserva.getDevolucion());
        assertEquals(0, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba de cancelar sin pagos...");
    }

    @DisplayName("Más de 7 días de antelación: se devuelve todo lo pagado")
    @Test
    void testDevolucionTotal() {
        LOG.info("Iniciando prueba de devolución total...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(20), 400000);
        reserva.getFolio().registrarPago(new Pago(400000, LocalDate.now(), MedioPago.EFECTIVO));

        assertEquals(400000, reserva.calcularDevolucion(LocalDateTime.now()));
        reserva.cancelar();

        assertEquals(400000, reserva.getDevolucion());
        assertEquals(0, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba de devolución total...");
    }

    @DisplayName("Entre 2 y 7 días: se retiene el 50 % del valor de la estancia")
    @Test
    void testDevolucionParcial() {
        LOG.info("Iniciando prueba de devolución parcial...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(5), 400000);
        reserva.getFolio().registrarPago(new Pago(400000, LocalDate.now(), MedioPago.TRANSFERENCIA));
        reserva.cancelar();

        assertEquals(200000, reserva.getDevolucion());
        assertEquals(0, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba de devolución parcial...");
    }

    @DisplayName("Menos de 48 horas: se retiene todo")
    @Test
    void testSinDevolucion() {
        LOG.info("Iniciando prueba de cancelación tardía...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(1), 400000);
        reserva.getFolio().registrarPago(new Pago(400000, LocalDate.now(), MedioPago.EFECTIVO));
        reserva.cancelar();

        assertEquals(0, reserva.getDevolucion());
        assertEquals(0, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba de cancelación tardía...");
    }

    @DisplayName("Lo que se retiene nunca supera lo que el huésped ha pagado")
    @Test
    void testRetencionNoSuperaLoPagado() {
        LOG.info("Iniciando prueba del límite de la retención...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(5), 400000);
        reserva.getFolio().registrarPago(new Pago(100000, LocalDate.now(), MedioPago.EFECTIVO));
        reserva.cancelar();

        assertEquals(0, reserva.getDevolucion());
        assertEquals(0, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba del límite de la retención...");
    }

    @DisplayName("No-show: solo después de las 10:00 p. m. del día de entrada y se retiene lo pagado")
    @Test
    void testNoShow() {
        LOG.info("Iniciando prueba de no-show...");

        LocalDate entrada = LocalDate.now().plusDays(2);
        ReservaDirecta reserva = crearReserva(entrada, 400000);
        reserva.confirmar();
        reserva.getFolio().registrarPago(new Pago(150000, LocalDate.now(), MedioPago.EFECTIVO));

        try {
            reserva.declararNoShow(entrada.atTime(21, 59));
            fail("Antes de las 10:00 p. m. no se puede declarar no-show");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        }

        reserva.declararNoShow(entrada.atTime(22, 0));
        assertEquals(EstadoReserva.NO_SHOW, reserva.getEstado());
        assertEquals(0, reserva.getDevolucion());
        assertEquals(0, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba de no-show...");
    }

    @DisplayName("Solo una reserva Confirmada puede ser no-show")
    @Test
    void testNoShowSinConfirmar() {
        LOG.info("Iniciando prueba de no-show sin confirmar...");

        LocalDate entrada = LocalDate.now().plusDays(2);
        ReservaDirecta reserva = crearReserva(entrada, 400000);

        try {
            reserva.declararNoShow(entrada.atTime(23, 0));
            fail("Una reserva Pendiente no puede ser no-show");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de no-show sin confirmar...");
    }

    @DisplayName("Los ocupantes se guardan como una copia de la lista original")
    @Test
    void testOcupantesSonUnaCopia() {
        LOG.info("Iniciando prueba de ocupantes...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 100000);

        ocupantes.add(new Ocupante("Otro", LocalDate.of(2000, 1, 1)));
        assertEquals(1, reserva.getOcupantes().size());

        reserva.getOcupantes().clear();
        assertEquals(1, reserva.getOcupantes().size());

        LOG.info("Finalizando prueba de ocupantes...");
    }

    @DisplayName("Los datos de la reserva no pueden ser nulos")
    @Test
    void testDatosNulos() {
        LOG.info("Iniciando prueba de datos nulos...");

        try {
            new ReservaDirecta("R1", null, LocalDate.now(), apartamento, titular, ocupantes);
            fail("La fecha de entrada nula debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("nulos"));
        }

        try {
            new ReservaDirecta("R1", LocalDate.now(), LocalDate.now().plusDays(1), apartamento, null, ocupantes);
            fail("El titular nulo debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("nulos"));
        }

        LOG.info("Finalizando prueba de datos nulos...");
    }

    @DisplayName("El texto de la reserva incluye su código y su estado")
    @Test
    void testToString() {
        LOG.info("Iniciando prueba de texto de la reserva...");

        ReservaDirecta reserva = crearReserva(LocalDate.now().plusDays(10), 100000);

        assertTrue(reserva.toString().contains("R1"));
        assertTrue(reserva.toString().contains("PENDIENTE"));

        LOG.info("Finalizando prueba de texto de la reserva...");
    }
}
