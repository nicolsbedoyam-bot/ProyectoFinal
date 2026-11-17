package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ReservaExternaTest {

    private static final Logger LOG = Logger.getLogger(ReservaExternaTest.class.getName());

    private ReservaExterna reserva;

    @BeforeEach
    void preparar() {
        Apartamento apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        Huesped titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        LinkedList<Ocupante> ocupantes = new LinkedList<>();
        ocupantes.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        reserva = new ReservaExterna("X1", LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), apartamento, titular, ocupantes, "BKG-123");
        reserva.setValorEstancia(1000000);
    }

    @DisplayName("La reserva externa es del canal Externa y guarda el código de la plataforma")
    @Test
    void testCanalYCodigo() {
        LOG.info("Iniciando prueba del canal externo...");

        assertEquals(Canal.EXTERNA, reserva.getCanal());
        assertEquals("BKG-123", reserva.getCodigoExterno());

        reserva.setCodigoExterno("BKG-999");
        assertEquals("BKG-999", reserva.getCodigoExterno());

        LOG.info("Finalizando prueba del canal externo...");
    }

    @DisplayName("El alojamiento paga a la plataforma una comisión del 15 % de la estancia")
    @Test
    void testComision() {
        LOG.info("Iniciando prueba de la comisión...");

        assertEquals(150000, reserva.calcularComision());

        LOG.info("Finalizando prueba de la comisión...");
    }

    @DisplayName("La reserva externa no exige anticipo")
    @Test
    void testSinAnticipo() {
        LOG.info("Iniciando prueba de anticipo externo...");

        assertFalse(reserva.requiereAnticipo());
        assertEquals(0, reserva.calcularAnticipo());

        LOG.info("Finalizando prueba de anticipo externo...");
    }
}
