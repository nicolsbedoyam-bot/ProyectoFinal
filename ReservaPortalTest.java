package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ReservaPortalTest {

    private static final Logger LOG = Logger.getLogger(ReservaPortalTest.class.getName());

    private ReservaPortal reserva;

    @BeforeEach
    void preparar() {
        Apartamento apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        Huesped titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        LinkedList<Ocupante> ocupantes = new LinkedList<>();
        ocupantes.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        reserva = new ReservaPortal("P1", LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), apartamento, titular, ocupantes);
        reserva.setValorEstancia(1000000);
    }

    @DisplayName("La reserva del portal es del canal Portal")
    @Test
    void testCanal() {
        LOG.info("Iniciando prueba del canal portal...");

        assertEquals(Canal.PORTAL, reserva.getCanal());

        LOG.info("Finalizando prueba del canal portal...");
    }

    @DisplayName("El portal exige un anticipo del 30 % del valor de la estancia")
    @Test
    void testAnticipo() {
        LOG.info("Iniciando prueba del anticipo...");

        assertTrue(reserva.requiereAnticipo());
        assertEquals(300000, reserva.calcularAnticipo());
        assertEquals(0, reserva.calcularComision());

        LOG.info("Finalizando prueba del anticipo...");
    }

    @DisplayName("Sin el anticipo completo la reserva no se confirma")
    @Test
    void testConfirmarSinAnticipo() {
        LOG.info("Iniciando prueba de confirmación sin anticipo...");

        try {
            reserva.confirmar();
            fail("Sin anticipo no se puede confirmar");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        }

        reserva.getFolio().registrarPago(new Pago(299999, LocalDate.now(), MedioPago.TARJETA));
        try {
            reserva.confirmar();
            fail("Con el anticipo incompleto no se puede confirmar");
        } catch (IllegalStateException e) {
            assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        }

        LOG.info("Finalizando prueba de confirmación sin anticipo...");
    }

    @DisplayName("Con el anticipo pagado la reserva se confirma")
    @Test
    void testConfirmarConAnticipo() {
        LOG.info("Iniciando prueba de confirmación con anticipo...");

        reserva.getFolio().registrarPago(new Pago(300000, LocalDate.now(), MedioPago.TARJETA));
        reserva.confirmar();

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        LOG.info("Finalizando prueba de confirmación con anticipo...");
    }
}
