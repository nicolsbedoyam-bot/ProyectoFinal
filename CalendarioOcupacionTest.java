package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class CalendarioOcupacionTest {

    private static final Logger LOG = Logger.getLogger(CalendarioOcupacionTest.class.getName());

    private Empresa empresa;
    private Apartamento apartamento;
    private CalendarioOcupacion calendario;
    private LocalDate entrada;

    @BeforeEach
    void preparar() {
        empresa = new Empresa("900", "Aparta-Suites UQ", "Salento");
        apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(apartamento);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 100000);
        calendario = new CalendarioOcupacion(empresa);
        entrada = LocalDate.now().plusDays(10);
    }

    private Huesped crearTitular() {
        Huesped titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        empresa.registrarHuesped(titular);
        return titular;
    }

    private LinkedList<Ocupante> unAdulto() {
        LinkedList<Ocupante> lista = new LinkedList<>();
        lista.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        return lista;
    }

    @DisplayName("Una noche sin reservas ni bloqueos está libre")
    @Test
    void testNocheLibre() {
        LOG.info("Iniciando prueba de noche libre...");

        assertEquals(EstadoNoche.LIBRE, calendario.estadoNoche(apartamento, entrada));

        LOG.info("Finalizando prueba de noche libre...");
    }

    @DisplayName("Las noches de una reserva están ocupadas, pero la noche de la salida no")
    @Test
    void testNocheOcupada() {
        LOG.info("Iniciando prueba de noche ocupada...");

        empresa.crearReservaDirecta(crearTitular(), apartamento, entrada, entrada.plusDays(2), unAdulto());

        assertEquals(EstadoNoche.OCUPADA, calendario.estadoNoche(apartamento, entrada));
        assertEquals(EstadoNoche.OCUPADA, calendario.estadoNoche(apartamento, entrada.plusDays(1)));
        assertEquals(EstadoNoche.LIBRE, calendario.estadoNoche(apartamento, entrada.plusDays(2)));
        assertEquals(EstadoNoche.LIBRE, calendario.estadoNoche(apartamento, entrada.minusDays(1)));

        LOG.info("Finalizando prueba de noche ocupada...");
    }

    @DisplayName("Una reserva cancelada deja las noches libres")
    @Test
    void testReservaCancelada() {
        LOG.info("Iniciando prueba de reserva cancelada en el calendario...");

        Reserva reserva = empresa.crearReservaDirecta(crearTitular(), apartamento, entrada, entrada.plusDays(2), unAdulto());
        reserva.cancelar();

        assertEquals(EstadoNoche.LIBRE, calendario.estadoNoche(apartamento, entrada));

        LOG.info("Finalizando prueba de reserva cancelada en el calendario...");
    }

    @DisplayName("Las noches de un bloqueo aparecen bloqueadas")
    @Test
    void testNocheBloqueada() {
        LOG.info("Iniciando prueba de noche bloqueada...");

        empresa.registrarBloqueo(new Bloqueo(apartamento, entrada, entrada.plusDays(1), "Plomería"));

        assertEquals(EstadoNoche.BLOQUEADA, calendario.estadoNoche(apartamento, entrada));
        assertEquals(EstadoNoche.BLOQUEADA, calendario.estadoNoche(apartamento, entrada.plusDays(1)));
        assertEquals(EstadoNoche.LIBRE, calendario.estadoNoche(apartamento, entrada.plusDays(2)));

        LOG.info("Finalizando prueba de noche bloqueada...");
    }

    @DisplayName("La vista mensual tiene una posición por cada día del mes")
    @Test
    void testEstadosDelMes() {
        LOG.info("Iniciando prueba de la vista mensual...");

        YearMonth mes = YearMonth.from(entrada);
        empresa.registrarBloqueo(new Bloqueo(apartamento, entrada, entrada, "Mantenimiento"));

        LinkedList<EstadoNoche> estados = calendario.estadosDelMes(apartamento, mes);

        assertEquals(mes.lengthOfMonth(), estados.size());
        assertEquals(EstadoNoche.BLOQUEADA, estados.get(entrada.getDayOfMonth() - 1));

        LOG.info("Finalizando prueba de la vista mensual...");
    }

    @DisplayName("El apartamento y la fecha son obligatorios")
    @Test
    void testDatosNulos() {
        LOG.info("Iniciando prueba de datos nulos en el calendario...");

        try {
            calendario.estadoNoche(null, entrada);
            fail("El apartamento nulo debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("nulos"));
        }

        try {
            calendario.estadoNoche(apartamento, null);
            fail("La fecha nula debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("nulos"));
        }

        LOG.info("Finalizando prueba de datos nulos en el calendario...");
    }
}
