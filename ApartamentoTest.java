package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ApartamentoTest {

    private static final Logger LOG = Logger.getLogger(ApartamentoTest.class.getName());

    private Apartamento apartamento;

    @BeforeEach
    void preparar() {
        LinkedList<String> caracteristicas = new LinkedList<>();
        caracteristicas.add("Balcón");
        caracteristicas.add("Jacuzzi");
        apartamento = new Apartamento("A1", "Suite Montañas", 2, 4, caracteristicas, new LinkedList<>());
    }

    @DisplayName("El apartamento guarda sus datos y nace Preparado y activo")
    @Test
    void testDatosIniciales() {
        LOG.info("Iniciando prueba de datos del apartamento...");

        assertEquals("A1", apartamento.getCodigo());
        assertEquals("Suite Montañas", apartamento.getNombre());
        assertEquals(2, apartamento.getNumeroDormitorios());
        assertEquals(4, apartamento.getCapacidadMaxima());
        assertEquals(2, apartamento.getCaracteristicas().size());
        assertEquals(EstadoOperativo.PREPARADO, apartamento.getEstadoOperativo());
        assertTrue(apartamento.isActivo());
        assertTrue(apartamento.estaPreparado());
        assertEquals("A1 - Suite Montañas", apartamento.toString());

        LOG.info("Finalizando prueba de datos del apartamento...");
    }

    @DisplayName("El estado operativo cambia entre los estados normales")
    @Test
    void testCambiarEstadoOperativo() {
        LOG.info("Iniciando prueba de cambio de estado...");

        apartamento.actualizarEstadoOperativo(EstadoOperativo.OCUPADO);
        assertEquals(EstadoOperativo.OCUPADO, apartamento.getEstadoOperativo());
        assertFalse(apartamento.estaPreparado());

        apartamento.actualizarEstadoOperativo(EstadoOperativo.PENDIENTE_PREPARACION);
        apartamento.actualizarEstadoOperativo(EstadoOperativo.EN_PREPARACION);
        apartamento.actualizarEstadoOperativo(EstadoOperativo.PREPARADO);
        assertTrue(apartamento.estaPreparado());

        LOG.info("Finalizando prueba de cambio de estado...");
    }

    @DisplayName("Fuera de servicio solo se registra con un motivo y deja de estar disponible")
    @Test
    void testFueraDeServicio() {
        LOG.info("Iniciando prueba de fuera de servicio...");

        //Por este método no se puede pasar a Fuera de servicio (hace falta el motivo)
        apartamento.actualizarEstadoOperativo(EstadoOperativo.FUERA_DE_SERVICIO);
        assertEquals(EstadoOperativo.PREPARADO, apartamento.getEstadoOperativo());

        apartamento.registrarMotivoFueraDeServicio("Daño en la tubería");
        assertEquals(EstadoOperativo.FUERA_DE_SERVICIO, apartamento.getEstadoOperativo());
        assertEquals("Daño en la tubería", apartamento.getFueraDeServicio());
        assertFalse(apartamento.verificarDisponibilidad());

        //Al volver a un estado normal el motivo se borra
        apartamento.actualizarEstadoOperativo(EstadoOperativo.PREPARADO);
        assertEquals("", apartamento.getFueraDeServicio());
        assertTrue(apartamento.verificarDisponibilidad());

        LOG.info("Finalizando prueba de fuera de servicio...");
    }

    @DisplayName("Un apartamento inactivo no está disponible")
    @Test
    void testInactivo() {
        LOG.info("Iniciando prueba de apartamento inactivo...");

        apartamento.setActivo(false);

        assertFalse(apartamento.verificarDisponibilidad());

        LOG.info("Finalizando prueba de apartamento inactivo...");
    }

    @DisplayName("Tarifas por temporada: sin tarifa vale cero")
    @Test
    void testTarifas() {
        LOG.info("Iniciando prueba de tarifas del apartamento...");

        assertEquals(0, apartamento.getTarifa(TemporadaAlojamiento.BAJA));

        apartamento.agregarTarifa(new Tarifa(60000, TemporadaAlojamiento.BAJA));
        apartamento.agregarTarifa(new Tarifa(110000, TemporadaAlojamiento.ALTA));

        assertEquals(60000, apartamento.getTarifa(TemporadaAlojamiento.BAJA));
        assertEquals(110000, apartamento.getTarifa(TemporadaAlojamiento.ALTA));
        assertEquals(0, apartamento.getTarifa(TemporadaAlojamiento.MEDIA));
        assertEquals(2, apartamento.getTarifas().size());

        LOG.info("Finalizando prueba de tarifas del apartamento...");
    }

    @DisplayName("La lista de tarifas que se devuelve es una copia")
    @Test
    void testTarifasCopia() {
        LOG.info("Iniciando prueba de copia de tarifas...");

        apartamento.agregarTarifa(new Tarifa(60000, TemporadaAlojamiento.BAJA));
        apartamento.getTarifas().clear();

        assertEquals(1, apartamento.getTarifas().size());

        LOG.info("Finalizando prueba de copia de tarifas...");
    }

    @DisplayName("Los datos del apartamento se pueden modificar")
    @Test
    void testCambiarDatos() {
        LOG.info("Iniciando prueba de modificación del apartamento...");

        apartamento.setNombre("Suite Renovada");
        apartamento.setNumeroDormitorios(3);
        apartamento.setCapacidadMaxima(6);

        assertEquals("Suite Renovada", apartamento.getNombre());
        assertEquals(3, apartamento.getNumeroDormitorios());
        assertEquals(6, apartamento.getCapacidadMaxima());

        LOG.info("Finalizando prueba de modificación del apartamento...");
    }
}
