package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

//Prueba de todas las enumeraciones: cuántos valores tienen y que los esperados existan
class EnumsTest {

    private static final Logger LOG = Logger.getLogger(EnumsTest.class.getName());

    @DisplayName("Canal: Portal, Directa y Externa")
    @Test
    void testCanal() {
        LOG.info("Iniciando prueba de Canal...");

        assertEquals(3, Canal.values().length);
        assertEquals(Canal.PORTAL, Canal.valueOf("PORTAL"));
        assertEquals(Canal.DIRECTA, Canal.valueOf("DIRECTA"));
        assertEquals(Canal.EXTERNA, Canal.valueOf("EXTERNA"));

        LOG.info("Finalizando prueba de Canal...");
    }

    @DisplayName("EstadoReserva: los 6 estados del ciclo de vida")
    @Test
    void testEstadoReserva() {
        LOG.info("Iniciando prueba de EstadoReserva...");

        assertEquals(6, EstadoReserva.values().length);
        assertEquals(EstadoReserva.PENDIENTE, EstadoReserva.valueOf("PENDIENTE"));
        assertEquals(EstadoReserva.CONFIRMADA, EstadoReserva.valueOf("CONFIRMADA"));
        assertEquals(EstadoReserva.EN_CURSO, EstadoReserva.valueOf("EN_CURSO"));
        assertEquals(EstadoReserva.FINALIZADA, EstadoReserva.valueOf("FINALIZADA"));
        assertEquals(EstadoReserva.CANCELADA, EstadoReserva.valueOf("CANCELADA"));
        assertEquals(EstadoReserva.NO_SHOW, EstadoReserva.valueOf("NO_SHOW"));

        LOG.info("Finalizando prueba de EstadoReserva...");
    }

    @DisplayName("EstadoOperativo: los 5 estados de un apartamento")
    @Test
    void testEstadoOperativo() {
        LOG.info("Iniciando prueba de EstadoOperativo...");

        assertEquals(5, EstadoOperativo.values().length);
        assertEquals(EstadoOperativo.PREPARADO, EstadoOperativo.valueOf("PREPARADO"));
        assertEquals(EstadoOperativo.OCUPADO, EstadoOperativo.valueOf("OCUPADO"));
        assertEquals(EstadoOperativo.PENDIENTE_PREPARACION, EstadoOperativo.valueOf("PENDIENTE_PREPARACION"));
        assertEquals(EstadoOperativo.EN_PREPARACION, EstadoOperativo.valueOf("EN_PREPARACION"));
        assertEquals(EstadoOperativo.FUERA_DE_SERVICIO, EstadoOperativo.valueOf("FUERA_DE_SERVICIO"));

        LOG.info("Finalizando prueba de EstadoOperativo...");
    }

    @DisplayName("EstadoNoche: libre, ocupada y bloqueada")
    @Test
    void testEstadoNoche() {
        LOG.info("Iniciando prueba de EstadoNoche...");

        assertEquals(3, EstadoNoche.values().length);
        assertEquals(EstadoNoche.LIBRE, EstadoNoche.valueOf("LIBRE"));
        assertEquals(EstadoNoche.OCUPADA, EstadoNoche.valueOf("OCUPADA"));
        assertEquals(EstadoNoche.BLOQUEADA, EstadoNoche.valueOf("BLOQUEADA"));

        LOG.info("Finalizando prueba de EstadoNoche...");
    }

    @DisplayName("MedioPago: efectivo, tarjeta y transferencia")
    @Test
    void testMedioPago() {
        LOG.info("Iniciando prueba de MedioPago...");

        assertEquals(3, MedioPago.values().length);
        assertEquals(MedioPago.EFECTIVO, MedioPago.valueOf("EFECTIVO"));
        assertEquals(MedioPago.TARJETA, MedioPago.valueOf("TARJETA"));
        assertEquals(MedioPago.TRANSFERENCIA, MedioPago.valueOf("TRANSFERENCIA"));

        LOG.info("Finalizando prueba de MedioPago...");
    }

    @DisplayName("TemporadaAlojamiento: baja, media y alta")
    @Test
    void testTemporadaAlojamiento() {
        LOG.info("Iniciando prueba de TemporadaAlojamiento...");

        assertEquals(3, TemporadaAlojamiento.values().length);
        assertEquals(TemporadaAlojamiento.BAJA, TemporadaAlojamiento.valueOf("BAJA"));
        assertEquals(TemporadaAlojamiento.MEDIA, TemporadaAlojamiento.valueOf("MEDIA"));
        assertEquals(TemporadaAlojamiento.ALTA, TemporadaAlojamiento.valueOf("ALTA"));

        LOG.info("Finalizando prueba de TemporadaAlojamiento...");
    }

    @DisplayName("TipoCargo: alojamiento, servicio adicional, penalidad y ajuste")
    @Test
    void testTipoCargo() {
        LOG.info("Iniciando prueba de TipoCargo...");

        assertEquals(4, TipoCargo.values().length);
        assertEquals(TipoCargo.ALOJAMIENTO, TipoCargo.valueOf("ALOJAMIENTO"));
        assertEquals(TipoCargo.SERVICIO_ADICIONAL, TipoCargo.valueOf("SERVICIO_ADICIONAL"));
        assertEquals(TipoCargo.PENALIDAD, TipoCargo.valueOf("PENALIDAD"));
        assertEquals(TipoCargo.AJUSTE, TipoCargo.valueOf("AJUSTE"));

        LOG.info("Finalizando prueba de TipoCargo...");
    }

    @DisplayName("TipoNovedad: daño, faltante y otra")
    @Test
    void testTipoNovedad() {
        LOG.info("Iniciando prueba de TipoNovedad...");

        assertEquals(3, TipoNovedad.values().length);
        assertEquals(TipoNovedad.DANIO, TipoNovedad.valueOf("DANIO"));
        assertEquals(TipoNovedad.FALTANTE, TipoNovedad.valueOf("FALTANTE"));
        assertEquals(TipoNovedad.OTRA, TipoNovedad.valueOf("OTRA"));

        LOG.info("Finalizando prueba de TipoNovedad...");
    }
}
