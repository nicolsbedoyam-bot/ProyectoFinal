package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class FolioTest {

    private static final Logger LOG = Logger.getLogger(FolioTest.class.getName());

    private Folio folio;

    @BeforeEach
    void preparar() {
        folio = new Folio();
    }

    @DisplayName("Un folio nuevo está vacío")
    @Test
    void testFolioVacio() {
        LOG.info("Iniciando prueba de folio vacío...");

        assertEquals(0, folio.totalCargo());
        assertEquals(0, folio.totalPago());
        assertEquals(0, folio.calcularSaldo());
        assertTrue(folio.getCargos().isEmpty());
        assertTrue(folio.getPagos().isEmpty());

        LOG.info("Finalizando prueba de folio vacío...");
    }

    @DisplayName("El saldo es la diferencia entre cargos y pagos")
    @Test
    void testSaldo() {
        LOG.info("Iniciando prueba de saldo...");

        folio.agregarCargo(new Cargo("Alojamiento", 500000, TipoCargo.ALOJAMIENTO));
        folio.agregarCargo(new Cargo("Desayuno", 20000, TipoCargo.SERVICIO_ADICIONAL));
        folio.registrarPago(new Pago(150000, LocalDate.now(), MedioPago.EFECTIVO));
        folio.registrarPago(new Pago(100000, LocalDate.now(), MedioPago.TARJETA));

        assertEquals(520000, folio.totalCargo());
        assertEquals(520000, folio.calcularTotal());
        assertEquals(250000, folio.totalPago());
        assertEquals(270000, folio.calcularSaldo());
        assertEquals(2, folio.getCargos().size());
        assertEquals(2, folio.getPagos().size());

        LOG.info("Finalizando prueba de saldo...");
    }

    @DisplayName("Una corrección se registra como un movimiento contrario")
    @Test
    void testCorreccionComoMovimientoContrario() {
        LOG.info("Iniciando prueba de corrección...");

        folio.agregarCargo(new Cargo("Lavandería", 30000, TipoCargo.SERVICIO_ADICIONAL));
        folio.agregarCargo(new Cargo("Corrección de lavandería", -30000, TipoCargo.AJUSTE));

        assertEquals(2, folio.getCargos().size());
        assertEquals(0, folio.totalCargo());

        LOG.info("Finalizando prueba de corrección...");
    }

    @DisplayName("Un cargo o un pago nulo no se registra")
    @Test
    void testNulos() {
        LOG.info("Iniciando prueba de nulos en el folio...");

        folio.agregarCargo(null);
        folio.registrarPago(null);

        assertTrue(folio.getCargos().isEmpty());
        assertTrue(folio.getPagos().isEmpty());

        LOG.info("Finalizando prueba de nulos en el folio...");
    }

    @DisplayName("Las listas que devuelve el folio son copias: no se pueden alterar desde afuera")
    @Test
    void testListasSonCopias() {
        LOG.info("Iniciando prueba de copias...");

        folio.agregarCargo(new Cargo("Alojamiento", 100000, TipoCargo.ALOJAMIENTO));
        folio.registrarPago(new Pago(50000, LocalDate.now(), MedioPago.EFECTIVO));

        folio.getCargos().clear();
        folio.getPagos().clear();

        assertEquals(1, folio.getCargos().size());
        assertEquals(1, folio.getPagos().size());
        assertEquals(50000, folio.calcularSaldo());

        LOG.info("Finalizando prueba de copias...");
    }

    @DisplayName("El dinero se muestra en pesos colombianos sin decimales")
    @Test
    void testDinero() {
        LOG.info("Iniciando prueba de formato de dinero...");

        assertEquals("$0", Folio.dinero(0));
        assertEquals("$950", Folio.dinero(950));
        assertEquals("$1.500.000", Folio.dinero(1500000));
        assertEquals("$-20.000", Folio.dinero(-20000));

        LOG.info("Finalizando prueba de formato de dinero...");
    }
}
