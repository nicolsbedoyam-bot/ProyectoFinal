package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class OcupanteTest {

    private static final Logger LOG = Logger.getLogger(OcupanteTest.class.getName());

    @DisplayName("El ocupante guarda su nombre y su fecha de nacimiento")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos del ocupante...");

        Ocupante ocupante = new Ocupante("Luisa", LocalDate.of(2020, 12, 15));
        assertEquals("Luisa", ocupante.getNombre());
        assertEquals(LocalDate.of(2020, 12, 15), ocupante.getFechaNacimiento());

        ocupante.setNombre("Luisa Pérez");
        ocupante.setFechaNacimiento(LocalDate.of(2019, 1, 1));
        assertEquals("Luisa Pérez", ocupante.getNombre());
        assertEquals(LocalDate.of(2019, 1, 1), ocupante.getFechaNacimiento());

        LOG.info("Finalizando prueba de datos del ocupante...");
    }

    @DisplayName("La edad se calcula a la fecha indicada y nunca se guarda")
    @Test
    void testCalcularEdad() {
        LOG.info("Iniciando prueba de cálculo de edad...");

        Ocupante ocupante = new Ocupante("Luisa", LocalDate.of(2020, 12, 15));

        assertEquals(5, ocupante.calcularEdad(LocalDate.of(2026, 12, 14)));
        assertEquals(6, ocupante.calcularEdad(LocalDate.of(2026, 12, 15)));
        assertEquals(0, ocupante.calcularEdad(LocalDate.of(2020, 12, 15)));

        LOG.info("Finalizando prueba de cálculo de edad...");
    }

    @DisplayName("Una fecha anterior al nacimiento o nula da edad cero")
    @Test
    void testEdadInvalida() {
        LOG.info("Iniciando prueba de edad inválida...");

        Ocupante ocupante = new Ocupante("Luisa", LocalDate.of(2020, 12, 15));

        assertEquals(0, ocupante.calcularEdad(LocalDate.of(2019, 1, 1)));
        assertEquals(0, ocupante.calcularEdad(null));

        LOG.info("Finalizando prueba de edad inválida...");
    }

    @DisplayName("Los menores de 6 años no pagan; desde los 6 años sí")
    @Test
    void testPaga() {
        LOG.info("Iniciando prueba de quién paga...");

        Ocupante ocupante = new Ocupante("Luisa", LocalDate.of(2020, 12, 15));

        assertFalse(ocupante.paga(LocalDate.of(2026, 12, 14)));
        assertTrue(ocupante.paga(LocalDate.of(2026, 12, 15)));
        assertTrue(ocupante.paga(LocalDate.of(2030, 1, 1)));

        LOG.info("Finalizando prueba de quién paga...");
    }
}
