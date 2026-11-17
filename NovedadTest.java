package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class NovedadTest {

    private static final Logger LOG = Logger.getLogger(NovedadTest.class.getName());

    @DisplayName("La novedad guarda su descripción, fecha, tipo, apartamento y quién la registró")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de novedad...");

        Apartamento apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        Recepcionista recepcionista = new Recepcionista("Rita", "9", "300", "r@r.com", "rita", "clave", LocalDate.now(), true);
        Novedad novedad = new Novedad("Llave del baño dañada", LocalDate.of(2026, 11, 15), TipoNovedad.DANIO, apartamento, recepcionista);

        assertEquals("Llave del baño dañada", novedad.getDescripcion());
        assertEquals(LocalDate.of(2026, 11, 15), novedad.getFecha());
        assertEquals(TipoNovedad.DANIO, novedad.getTipo());
        assertEquals(apartamento, novedad.getApartamento());
        assertEquals(recepcionista, novedad.getRegistradaPor());

        LOG.info("Finalizando prueba de novedad...");
    }

    @DisplayName("La novedad se puede modificar")
    @Test
    void testCambiarDatos() {
        LOG.info("Iniciando prueba de cambio de la novedad...");

        Apartamento apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        Novedad novedad = new Novedad("Falta una toalla", LocalDate.now(), TipoNovedad.FALTANTE, apartamento, null);
        novedad.setDescripcion("Faltan dos toallas");
        novedad.setTipo(TipoNovedad.OTRA);

        assertEquals("Faltan dos toallas", novedad.getDescripcion());
        assertEquals(TipoNovedad.OTRA, novedad.getTipo());

        LOG.info("Finalizando prueba de cambio de la novedad...");
    }
}
