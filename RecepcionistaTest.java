package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class RecepcionistaTest {

    private static final Logger LOG = Logger.getLogger(RecepcionistaTest.class.getName());

    private Recepcionista recepcionista;

    @BeforeEach
    void preparar() {
        recepcionista = new Recepcionista("Rita Gómez", "456", "3007654321", "rita@correo.com", "rita", "clave2", LocalDate.of(2026, 1, 10), true);
    }

    @DisplayName("El recepcionista guarda su fecha de contratación y si está activo")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos del recepcionista...");

        assertEquals(LocalDate.of(2026, 1, 10), recepcionista.getFechaContratacion());
        assertTrue(recepcionista.getActivo());

        recepcionista.setActivo(false);
        assertFalse(recepcionista.getActivo());

        LOG.info("Finalizando prueba de datos del recepcionista...");
    }

    @DisplayName("El recepcionista tiene los 8 permisos de su rol")
    @Test
    void testPermisos() {
        LOG.info("Iniciando prueba de permisos del recepcionista...");

        LinkedList<String> permisos = recepcionista.getPermisos();

        assertEquals(8, permisos.size());

        LOG.info("Finalizando prueba de permisos del recepcionista...");
    }

    @DisplayName("El recepcionista se autentica con su usuario y contraseña")
    @Test
    void testAutenticar() {
        LOG.info("Iniciando prueba de autenticación del recepcionista...");

        assertTrue(recepcionista.autenticar("rita", "clave2"));
        assertFalse(recepcionista.autenticar("rita", "mala"));

        LOG.info("Finalizando prueba de autenticación del recepcionista...");
    }
}
