package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ReservaDirectaTest {

    private static final Logger LOG = Logger.getLogger(ReservaDirectaTest.class.getName());

    private ReservaDirecta reserva;

    @BeforeEach
    void preparar() {
        Apartamento apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        Huesped titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        LinkedList<Ocupante> ocupantes = new LinkedList<>();
        ocupantes.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        reserva = new ReservaDirecta("D1", LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), apartamento, titular, ocupantes);
        reserva.setValorEstancia(1000000);
    }

    @DisplayName("La reserva directa es del canal Directa")
    @Test
    void testCanal() {
        LOG.info("Iniciando prueba del canal directo...");

        assertEquals(Canal.DIRECTA, reserva.getCanal());

        LOG.info("Finalizando prueba del canal directo...");
    }

    @DisplayName("La reserva directa no exige anticipo ni paga comisión")
    @Test
    void testSinAnticipoNiComision() {
        LOG.info("Iniciando prueba de anticipo y comisión directos...");

        assertFalse(reserva.requiereAnticipo());
        assertEquals(0, reserva.calcularAnticipo());
        assertEquals(0, reserva.calcularComision());

        LOG.info("Finalizando prueba de anticipo y comisión directos...");
    }

    @DisplayName("La reserva directa se confirma sin pagar anticipo")
    @Test
    void testConfirmarSinAnticipo() {
        LOG.info("Iniciando prueba de confirmación directa...");

        reserva.confirmar();

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        LOG.info("Finalizando prueba de confirmación directa...");
    }
}
