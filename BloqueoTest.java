package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class BloqueoTest {

    private static final Logger LOG = Logger.getLogger(BloqueoTest.class.getName());

    private Apartamento apartamento;
    private Bloqueo bloqueo;

    @BeforeEach
    void preparar() {
        apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        bloqueo = new Bloqueo(apartamento, LocalDate.of(2027, 3, 10), LocalDate.of(2027, 3, 12), "Daño en la tubería");
    }

    @DisplayName("El bloqueo guarda el apartamento, las fechas y el motivo")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos del bloqueo...");

        assertEquals(apartamento, bloqueo.getApartamento());
        assertEquals(LocalDate.of(2027, 3, 10), bloqueo.getFechaInicio());
        assertEquals(LocalDate.of(2027, 3, 12), bloqueo.getFechaFin());
        assertEquals("Daño en la tubería", bloqueo.getMotivo());

        LOG.info("Finalizando prueba de datos del bloqueo...");
    }

    @DisplayName("El bloqueo cubre las noches del rango, incluidos el inicio y el fin")
    @Test
    void testCubre() {
        LOG.info("Iniciando prueba de cubre...");

        assertTrue(bloqueo.cubre(LocalDate.of(2027, 3, 10)));
        assertTrue(bloqueo.cubre(LocalDate.of(2027, 3, 11)));
        assertTrue(bloqueo.cubre(LocalDate.of(2027, 3, 12)));
        assertFalse(bloqueo.cubre(LocalDate.of(2027, 3, 9)));
        assertFalse(bloqueo.cubre(LocalDate.of(2027, 3, 13)));
        assertFalse(bloqueo.cubre(null));

        LOG.info("Finalizando prueba de cubre...");
    }

    @DisplayName("El bloqueo se puede modificar")
    @Test
    void testCambiarDatos() {
        LOG.info("Iniciando prueba de cambio del bloqueo...");

        bloqueo.setMotivo("Mantenimiento eléctrico");
        bloqueo.setFechaInicio(LocalDate.of(2027, 4, 1));
        bloqueo.setFechaFin(LocalDate.of(2027, 4, 2));

        assertEquals("Mantenimiento eléctrico", bloqueo.getMotivo());
        assertTrue(bloqueo.cubre(LocalDate.of(2027, 4, 1)));
        assertFalse(bloqueo.cubre(LocalDate.of(2027, 3, 11)));

        LOG.info("Finalizando prueba de cambio del bloqueo...");
    }
}
