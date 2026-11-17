package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class HuespedTest {

    private static final Logger LOG = Logger.getLogger(HuespedTest.class.getName());

    private Huesped huesped;

    @BeforeEach
    void preparar() {
        huesped = new Huesped("Ana Pérez", "123", "3001234567", "ana@correo.com", "ana", "clave1", LocalDate.of(1990, 5, 20), null);
    }

    @DisplayName("El huésped guarda su fecha de nacimiento y su fotografía (opcional)")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos del huésped...");

        assertEquals(LocalDate.of(1990, 5, 20), huesped.getFechaNacimiento());
        assertNull(huesped.getRutaFotografia());

        huesped.setRutaFotografia("fotos/huespedes/123.png");
        assertEquals("fotos/huespedes/123.png", huesped.getRutaFotografia());

        huesped.setFechaNacimiento(LocalDate.of(1991, 1, 1));
        assertEquals(LocalDate.of(1991, 1, 1), huesped.getFechaNacimiento());

        LOG.info("Finalizando prueba de datos del huésped...");
    }

    @DisplayName("El huésped tiene los 6 permisos de su rol")
    @Test
    void testPermisos() {
        LOG.info("Iniciando prueba de permisos del huésped...");

        LinkedList<String> permisos = huesped.getPermisos();

        assertEquals(6, permisos.size());

        LOG.info("Finalizando prueba de permisos del huésped...");
    }

    @DisplayName("El texto del huésped incluye su nombre y su documento")
    @Test
    void testToString() {
        LOG.info("Iniciando prueba de texto del huésped...");

        assertTrue(huesped.toString().contains("Ana Pérez"));
        assertTrue(huesped.toString().contains("123"));

        LOG.info("Finalizando prueba de texto del huésped...");
    }
}
