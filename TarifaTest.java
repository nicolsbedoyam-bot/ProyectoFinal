package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class TarifaTest {

    private static final Logger LOG = Logger.getLogger(TarifaTest.class.getName());

    @DisplayName("La tarifa guarda el valor por persona por noche y su temporada")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de tarifa...");

        Tarifa tarifa = new Tarifa(60000, TemporadaAlojamiento.BAJA);

        assertEquals(60000, tarifa.getValorPersonaNoche());
        assertEquals(TemporadaAlojamiento.BAJA, tarifa.getTemporada());

        LOG.info("Finalizando prueba de tarifa...");
    }

    @DisplayName("La tarifa se puede modificar")
    @Test
    void testCambiarDatos() {
        LOG.info("Iniciando prueba de cambio de tarifa...");

        Tarifa tarifa = new Tarifa(60000, TemporadaAlojamiento.BAJA);
        tarifa.setValorPersonaNoche(75000);
        tarifa.setTemporada(TemporadaAlojamiento.ALTA);

        assertEquals(75000, tarifa.getValorPersonaNoche());
        assertEquals(TemporadaAlojamiento.ALTA, tarifa.getTemporada());

        LOG.info("Finalizando prueba de cambio de tarifa...");
    }
}
