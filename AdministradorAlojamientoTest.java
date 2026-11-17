package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorAlojamientoTest {

    private static final Logger LOG = Logger.getLogger(AdministradorAlojamientoTest.class.getName());

    private AdministradorAlojamiento administrador;

    @BeforeEach
    void preparar() {
        administrador = new AdministradorAlojamiento("Admin", "1", "3000000000", "admin@correo.com", "admin", "admin123");
    }

    @DisplayName("El administrador tiene los 7 permisos de su rol")
    @Test
    void testPermisos() {
        LOG.info("Iniciando prueba de permisos del administrador...");

        LinkedList<String> permisos = administrador.getPermisos();

        assertEquals(7, permisos.size());

        LOG.info("Finalizando prueba de permisos del administrador...");
    }

    @DisplayName("El administrador se autentica con su usuario y contraseña")
    @Test
    void testAutenticar() {
        LOG.info("Iniciando prueba de autenticación del administrador...");

        assertTrue(administrador.autenticar("admin", "admin123"));
        assertFalse(administrador.autenticar("admin", "otra"));
        assertEquals("Admin", administrador.getNombre());

        LOG.info("Finalizando prueba de autenticación del administrador...");
    }

    @DisplayName("El texto del administrador incluye su nombre")
    @Test
    void testToString() {
        LOG.info("Iniciando prueba de texto del administrador...");

        assertTrue(administrador.toString().contains("Admin"));

        LOG.info("Finalizando prueba de texto del administrador...");
    }
}
