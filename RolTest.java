package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

//Rol es abstracta: se prueba con un Huesped, que hereda todo lo de Rol
class RolTest {

    private static final Logger LOG = Logger.getLogger(RolTest.class.getName());

    private Rol rol;

    @BeforeEach
    void preparar() {
        rol = new Huesped("Ana Pérez", "123", "3001234567", "ana@correo.com", "ana", "clave1", LocalDate.of(1990, 5, 20), null);
    }

    @DisplayName("Una persona guarda sus datos comunes")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos del rol...");

        assertEquals("Ana Pérez", rol.getNombre());
        assertEquals("123", rol.getCedula());
        assertEquals("3001234567", rol.getTelefono());
        assertEquals("ana@correo.com", rol.getCorreo());
        assertEquals("ana", rol.getUsuario());
        assertEquals("clave1", rol.getContrasenia());

        LOG.info("Finalizando prueba de datos del rol...");
    }

    @DisplayName("La autenticación funciona solo con el usuario y la contraseña correctos")
    @Test
    void testAutenticar() {
        LOG.info("Iniciando prueba de autenticación...");

        assertTrue(rol.autenticar("ana", "clave1"));
        assertFalse(rol.autenticar("ana", "otra"));
        assertFalse(rol.autenticar("otro", "clave1"));

        IAutenticable autenticable = rol;
        assertTrue(autenticable.autenticar("ana", "clave1"));

        LOG.info("Finalizando prueba de autenticación...");
    }

    @DisplayName("Los datos se pueden modificar y la nueva contraseña reemplaza a la anterior")
    @Test
    void testCambiarDatos() {
        LOG.info("Iniciando prueba de modificación de datos...");

        rol.setNombre("Ana María Pérez");
        rol.setTelefono("3109999999");
        rol.setCorreo("nueva@correo.com");
        rol.setContrasenia("claveNueva");

        assertEquals("Ana María Pérez", rol.getNombre());
        assertEquals("3109999999", rol.getTelefono());
        assertEquals("nueva@correo.com", rol.getCorreo());
        assertFalse(rol.autenticar("ana", "clave1"));
        assertTrue(rol.autenticar("ana", "claveNueva"));

        LOG.info("Finalizando prueba de modificación de datos...");
    }
}
