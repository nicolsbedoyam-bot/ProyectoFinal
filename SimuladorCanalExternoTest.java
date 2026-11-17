package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorCanalExternoTest {

    private static final Logger LOG = Logger.getLogger(SimuladorCanalExternoTest.class.getName());

    private Empresa empresa;
    private SimuladorCanalExterno simulador;

    @BeforeEach
    void preparar() {
        empresa = new Empresa("900", "Aparta-Suites UQ", "Salento");
        simulador = new SimuladorCanalExterno();
        crearApartamento("A1", 2);
        crearApartamento("A2", 4);
    }

    private void crearApartamento(String codigo, int capacidad) {
        Apartamento apartamento = new Apartamento(codigo, "Suite " + codigo, 1, capacidad, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(apartamento);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 60000);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.MEDIA, 80000);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.ALTA, 110000);
    }

    @DisplayName("La reserva aleatoria es una reserva externa válida")
    @Test
    void testGenerarReservaAleatoria() {
        LOG.info("Iniciando prueba de reserva aleatoria...");

        for (int i = 0; i < 20; i++) {
            ReservaExterna reserva = simulador.generarReservaAleatoria(empresa);

            assertNotNull(reserva);
            assertEquals(Canal.EXTERNA, reserva.getCanal());
            assertTrue(reserva.getCodigoExterno().startsWith("EXT-"));
            assertFalse(reserva.getFechaEntrada().isBefore(LocalDate.now()));
            assertTrue(reserva.getFechaSalida().isAfter(reserva.getFechaEntrada()));
            assertTrue(reserva.getOcupantes().size() >= 1);
            assertTrue(reserva.getOcupantes().size() <= reserva.getApartamento().getCapacidadMaxima());
            assertNotNull(empresa.buscarApartamento(reserva.getApartamento().getCodigo()));
        }

        LOG.info("Finalizando prueba de reserva aleatoria...");
    }

    @DisplayName("Cada reserva aleatoria trae un código externo distinto")
    @Test
    void testCodigosDistintos() {
        LOG.info("Iniciando prueba de códigos externos...");

        ReservaExterna primera = simulador.generarReservaAleatoria(empresa);
        ReservaExterna segunda = simulador.generarReservaAleatoria(empresa);

        assertNotEquals(primera.getCodigoExterno(), segunda.getCodigoExterno());

        LOG.info("Finalizando prueba de códigos externos...");
    }

    @DisplayName("Sin apartamentos disponibles no se genera ninguna reserva")
    @Test
    void testSinApartamentos() {
        LOG.info("Iniciando prueba sin apartamentos...");

        Empresa vacia = new Empresa("1", "Vacía", "Salento");
        assertNull(simulador.generarReservaAleatoria(vacia));
        assertTrue(simulador.simular(vacia, 10).isEmpty());

        //Con todos los apartamentos fuera de servicio tampoco
        for (Apartamento a : empresa.getApartamentos()) {
            a.registrarMotivoFueraDeServicio("Mantenimiento");
        }
        assertNull(simulador.generarReservaAleatoria(empresa));
        simulador.simular(empresa, 10);
        assertTrue(empresa.getReservas().isEmpty());

        LOG.info("Finalizando prueba sin apartamentos...");
    }

    @DisplayName("Simular devuelve los conflictos de canal y nunca crea doble venta")
    @Test
    void testSimular() {
        LOG.info("Iniciando prueba de simulación...");

        LinkedList<ConflictoCanal> conflictos = simulador.simular(empresa, 60);

        //Los conflictos devueltos son los que quedaron registrados en la empresa
        assertEquals(empresa.getConflictoCanales().size(), conflictos.size());
        //Cada intento fue aceptado o quedó como conflicto
        assertEquals(60, empresa.getReservas().size() + conflictos.size());
        //Con 2 apartamentos y 60 intentos tiene que haber choques
        assertTrue(conflictos.size() > 0);

        //Sin doble venta: dos reservas del mismo apartamento nunca comparten una noche
        LinkedList<Reserva> reservas = empresa.getReservas();
        for (int i = 0; i < reservas.size(); i++) {
            for (int j = i + 1; j < reservas.size(); j++) {
                Reserva a = reservas.get(i);
                Reserva b = reservas.get(j);
                if (a.getApartamento().getCodigo().equals(b.getApartamento().getCodigo())) {
                    boolean secruzan = a.getFechaEntrada().isBefore(b.getFechaSalida()) && b.getFechaEntrada().isBefore(a.getFechaSalida());
                    assertFalse(secruzan);
                }
            }
        }

        LOG.info("Finalizando prueba de simulación...");
    }

    @DisplayName("Simular cero reservas no cambia nada")
    @Test
    void testSimularCero() {
        LOG.info("Iniciando prueba de simulación en cero...");

        LinkedList<ConflictoCanal> conflictos = simulador.simular(empresa, 0);

        assertTrue(conflictos.isEmpty());
        assertTrue(empresa.getReservas().isEmpty());

        LOG.info("Finalizando prueba de simulación en cero...");
    }
}
