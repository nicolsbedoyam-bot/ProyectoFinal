package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ConflictoCanalTest {

    private static final Logger LOG = Logger.getLogger(ConflictoCanalTest.class.getName());

    @DisplayName("El conflicto de canal guarda los datos de la reserva externa rechazada")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de conflicto de canal...");

        LocalDateTime registro = LocalDateTime.of(2026, 11, 15, 10, 30);
        ConflictoCanal conflicto = new ConflictoCanal("EXT-1", "A1", LocalDate.of(2027, 3, 10), LocalDate.of(2027, 3, 12), "Choca con una reserva", registro);

        assertEquals("EXT-1", conflicto.getCodigoExterno());
        assertEquals("A1", conflicto.getCodigoApartamento());
        assertEquals(LocalDate.of(2027, 3, 10), conflicto.getFechaEntrada());
        assertEquals(LocalDate.of(2027, 3, 12), conflicto.getFechaSalida());
        assertEquals("Choca con una reserva", conflicto.getMotivo());
        assertEquals(registro, conflicto.getFechaRegistro());

        LOG.info("Finalizando prueba de conflicto de canal...");
    }

    @DisplayName("El conflicto de canal se puede modificar")
    @Test
    void testCambiarDatos() {
        LOG.info("Iniciando prueba de cambio del conflicto...");

        ConflictoCanal conflicto = new ConflictoCanal("EXT-1", "A1", LocalDate.of(2027, 3, 10), LocalDate.of(2027, 3, 12), "Choca", LocalDateTime.now());
        conflicto.setCodigoExterno("EXT-2");
        conflicto.setCodigoApartamento("A2");
        conflicto.setMotivo("Otro motivo");

        assertEquals("EXT-2", conflicto.getCodigoExterno());
        assertEquals("A2", conflicto.getCodigoApartamento());
        assertEquals("Otro motivo", conflicto.getMotivo());

        LOG.info("Finalizando prueba de cambio del conflicto...");
    }
}
