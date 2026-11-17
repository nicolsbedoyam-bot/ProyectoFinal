package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaCancelacionTest {

    private static final Logger LOG = Logger.getLogger(PoliticaCancelacionTest.class.getName());

    private PoliticaCancelacion politica;
    private LocalDate hoy;

    @BeforeEach
    void preparar() {
        //Política del enunciado: más de 7 días se devuelve todo; de 2 a 7 se retiene el 50 %; menos de 2 días el 100 %
        politica = new PoliticaCancelacion(7, 2, 50);
        hoy = LocalDate.of(2026, 11, 15);
    }

    @DisplayName("Con más de 7 días de antelación se devuelve todo lo pagado (no se retiene nada)")
    @Test
    void testDevolucionTotal() {
        LOG.info("Iniciando prueba de devolución total...");

        assertEquals(0, politica.calcularRetencion(1000000, hoy, hoy.plusDays(30), EstadoReserva.CANCELADA));
        assertEquals(0, politica.calcularRetencion(1000000, hoy, hoy.plusDays(8), EstadoReserva.CANCELADA));

        LOG.info("Finalizando prueba de devolución total...");
    }

    @DisplayName("Entre 2 y 7 días se retiene el 50 % del valor de la estancia")
    @Test
    void testRetencionParcial() {
        LOG.info("Iniciando prueba de retención parcial...");

        assertEquals(500000, politica.calcularRetencion(1000000, hoy, hoy.plusDays(7), EstadoReserva.CANCELADA));
        assertEquals(500000, politica.calcularRetencion(1000000, hoy, hoy.plusDays(5), EstadoReserva.CANCELADA));
        assertEquals(500000, politica.calcularRetencion(1000000, hoy, hoy.plusDays(2), EstadoReserva.CANCELADA));

        LOG.info("Finalizando prueba de retención parcial...");
    }

    @DisplayName("Con menos de 48 horas se retiene el 100 %")
    @Test
    void testRetencionTotal() {
        LOG.info("Iniciando prueba de retención total...");

        assertEquals(1000000, politica.calcularRetencion(1000000, hoy, hoy.plusDays(1), EstadoReserva.CANCELADA));
        assertEquals(1000000, politica.calcularRetencion(1000000, hoy, hoy, EstadoReserva.CANCELADA));

        LOG.info("Finalizando prueba de retención total...");
    }

    @DisplayName("En caso de no-show se retiene el 100 % sin importar la antelación")
    @Test
    void testNoShow() {
        LOG.info("Iniciando prueba de no-show...");

        assertEquals(1000000, politica.calcularRetencion(1000000, hoy, hoy.plusDays(30), EstadoReserva.NO_SHOW));

        LOG.info("Finalizando prueba de no-show...");
    }

    @DisplayName("Con fechas nulas no se retiene nada")
    @Test
    void testFechasNulas() {
        LOG.info("Iniciando prueba de fechas nulas...");

        assertEquals(0, politica.calcularRetencion(1000000, null, hoy, EstadoReserva.CANCELADA));
        assertEquals(0, politica.calcularRetencion(1000000, hoy, null, EstadoReserva.CANCELADA));

        LOG.info("Finalizando prueba de fechas nulas...");
    }

    @DisplayName("La política se puede configurar con otros valores")
    @Test
    void testPoliticaConfigurable() {
        LOG.info("Iniciando prueba de política configurable...");

        PoliticaCancelacion otra = new PoliticaCancelacion(10, 3, 40);

        assertEquals(10, otra.getDiasDevolucion());
        assertEquals(3, otra.getDiasRetencion());
        assertEquals(40.0, otra.getPorcentajeRetencion());
        assertEquals(400000, otra.calcularRetencion(1000000, hoy, hoy.plusDays(5), EstadoReserva.CANCELADA));
        assertEquals(0, otra.calcularRetencion(1000000, hoy, hoy.plusDays(11), EstadoReserva.CANCELADA));

        otra.setPorcentajeRetencion(25);
        assertEquals(250000, otra.calcularRetencion(1000000, hoy, hoy.plusDays(5), EstadoReserva.CANCELADA));

        LOG.info("Finalizando prueba de política configurable...");
    }
}
