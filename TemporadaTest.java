package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class TemporadaTest {

    private static final Logger LOG = Logger.getLogger(TemporadaTest.class.getName());

    @DisplayName("Una temporada guarda sus fechas y su tipo")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos de temporada...");

        Temporada temporada = new Temporada(LocalDate.of(2026, 6, 15), LocalDate.of(2026, 7, 15), TemporadaAlojamiento.ALTA);

        assertEquals(LocalDate.of(2026, 6, 15), temporada.getFechaInicio());
        assertEquals(LocalDate.of(2026, 7, 15), temporada.getFechaFin());
        assertEquals(TemporadaAlojamiento.ALTA, temporada.getTipo());

        LOG.info("Finalizando prueba de datos de temporada...");
    }

    @DisplayName("Una noche dentro del rango pertenece a la temporada (los extremos incluidos)")
    @Test
    void testContiene() {
        LOG.info("Iniciando prueba de contiene...");

        Temporada temporada = new Temporada(LocalDate.of(2026, 6, 15), LocalDate.of(2026, 7, 15), TemporadaAlojamiento.ALTA);

        assertTrue(temporada.contiene(LocalDate.of(2026, 6, 15)));
        assertTrue(temporada.contiene(LocalDate.of(2026, 7, 1)));
        assertTrue(temporada.contiene(LocalDate.of(2026, 7, 15)));
        assertFalse(temporada.contiene(LocalDate.of(2026, 6, 14)));
        assertFalse(temporada.contiene(LocalDate.of(2026, 7, 16)));

        LOG.info("Finalizando prueba de contiene...");
    }

    @DisplayName("Una temporada de diciembre a enero cruza el cambio de año")
    @Test
    void testCruceDeAnio() {
        LOG.info("Iniciando prueba de cruce de año...");

        Temporada temporada = new Temporada(LocalDate.of(2026, 12, 15), LocalDate.of(2027, 1, 15), TemporadaAlojamiento.ALTA);

        assertTrue(temporada.contiene(LocalDate.of(2026, 12, 31)));
        assertTrue(temporada.contiene(LocalDate.of(2027, 1, 1)));
        assertFalse(temporada.contiene(LocalDate.of(2027, 1, 16)));
        assertFalse(temporada.contiene(LocalDate.of(2026, 12, 14)));

        LOG.info("Finalizando prueba de cruce de año...");
    }
}
