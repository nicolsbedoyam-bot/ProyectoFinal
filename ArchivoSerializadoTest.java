package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

//El archivo serializado guarda TODO lo de la empresa: personas, reservas, folios, conflictos, etc.
class ArchivoSerializadoTest {

    private static final Logger LOG = Logger.getLogger(ArchivoSerializadoTest.class.getName());

    private File archivo;
    private ArchivoSerializado persistencia;
    private Empresa empresa;

    @BeforeEach
    void preparar() throws IOException {
        archivo = File.createTempFile("sga_serializado", ".dat");
        persistencia = new ArchivoSerializado(archivo.getPath());

        empresa = new Empresa("900.123", "Aparta-Suites UQ", "Salento");
        Apartamento apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(apartamento);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 100000);
        Huesped titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        empresa.registrarHuesped(titular);
        empresa.registrarRecepcionista(new Recepcionista("Rita", "9", "3", "r@r.com", "rita", "pw", LocalDate.now(), true));

        LinkedList<Ocupante> ocupantes = new LinkedList<>();
        ocupantes.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        LocalDate entrada = LocalDate.now().plusDays(20);
        Reserva reserva = empresa.crearReservaPortal(titular, apartamento, entrada, entrada.plusDays(3), ocupantes);
        empresa.registrarPago(reserva, 100000, MedioPago.TARJETA);
        empresa.registrarCargo(reserva, "Desayuno", 20000, TipoCargo.SERVICIO_ADICIONAL);

        //Una reserva externa que choca para que quede un conflicto de canal
        empresa.registrarReservaExterna(new ReservaExterna("X1", entrada, entrada.plusDays(2), apartamento, titular, ocupantes, "BKG-1"));
        empresa.registrarNovedad(new Novedad("Llave dañada", LocalDate.now(), TipoNovedad.DANIO, apartamento, null));
    }

    @AfterEach
    void limpiar() {
        archivo.delete();
    }

    @DisplayName("Guardar y cargar conserva las personas, apartamentos y novedades")
    @Test
    void testDatosGenerales() {
        LOG.info("Iniciando prueba de archivo serializado (datos generales)...");

        persistencia.guardar(empresa);
        Empresa cargada = persistencia.cargar();

        assertEquals("Aparta-Suites UQ", cargada.getNombre());
        assertEquals(2, cargada.getRoles().size());
        assertEquals(1, cargada.getApartamentos().size());
        assertEquals(100000, cargada.buscarApartamento("A1").getTarifa(TemporadaAlojamiento.BAJA));
        assertEquals(1, cargada.getNovedades().size());
        assertNotNull(cargada.autenticar("rita", "pw"));

        LOG.info("Finalizando prueba de archivo serializado (datos generales)...");
    }

    @DisplayName("Guardar y cargar conserva las reservas con su folio, valor y estado")
    @Test
    void testReservasYFolio() {
        LOG.info("Iniciando prueba de archivo serializado (reservas)...");

        persistencia.guardar(empresa);
        Empresa cargada = persistencia.cargar();

        assertEquals(1, cargada.getReservas().size());
        Reserva reserva = cargada.getReservas().get(0);
        assertEquals(Canal.PORTAL, reserva.getCanal());
        //El pago de 100000 cubre el anticipo del 30 % (90000), así que la reserva del portal quedó Confirmada
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertEquals(300000, reserva.getValorEstancia());
        assertEquals(320000, reserva.getFolio().totalCargo());
        assertEquals(100000, reserva.getFolio().totalPago());
        assertEquals(220000, reserva.getFolio().calcularSaldo());
        assertEquals("Ana", reserva.getTitular().getNombre());

        LOG.info("Finalizando prueba de archivo serializado (reservas)...");
    }

    @DisplayName("Guardar y cargar conserva los conflictos de canal y los códigos externos")
    @Test
    void testConflictosYCodigos() {
        LOG.info("Iniciando prueba de archivo serializado (conflictos)...");

        persistencia.guardar(empresa);
        Empresa cargada = persistencia.cargar();

        assertEquals(1, cargada.getConflictoCanales().size());
        assertEquals("BKG-1", cargada.getConflictoCanales().get(0).getCodigoExterno());

        LOG.info("Finalizando prueba de archivo serializado (conflictos)...");
    }

    @DisplayName("Después de cargar, el sistema sigue aplicando las reglas (no hay doble venta)")
    @Test
    void testReglasDespuesDeCargar() {
        LOG.info("Iniciando prueba de reglas después de cargar...");

        persistencia.guardar(empresa);
        Empresa cargada = persistencia.cargar();
        Reserva existente = cargada.getReservas().get(0);

        try {
            cargada.crearReservaDirecta(existente.getTitular(), existente.getApartamento(), existente.getFechaEntrada(), existente.getFechaSalida(), existente.getOcupantes());
            fail("Las noches ya están reservadas: debía rechazarse");
        } catch (IllegalStateException e) {
            assertEquals(1, cargada.getReservas().size());
        }

        //El consecutivo de códigos continúa donde iba
        assertEquals("RES-0002", cargada.generarCodigoReserva());

        LOG.info("Finalizando prueba de reglas después de cargar...");
    }

    @DisplayName("Si el archivo no existe se devuelve una empresa vacía")
    @Test
    void testArchivoInexistente() {
        LOG.info("Iniciando prueba de archivo serializado inexistente...");

        archivo.delete();
        Empresa cargada = persistencia.cargar();

        assertEquals("Sin informacion", cargada.getNombre());
        assertTrue(cargada.getReservas().isEmpty());

        LOG.info("Finalizando prueba de archivo serializado inexistente...");
    }

    @DisplayName("La ruta del archivo se puede cambiar")
    @Test
    void testRuta() {
        LOG.info("Iniciando prueba de ruta del archivo serializado...");

        assertEquals(archivo.getPath(), persistencia.getRutaArchivo());

        persistencia.setRutaArchivo("otro.dat");
        assertEquals("otro.dat", persistencia.getRutaArchivo());

        LOG.info("Finalizando prueba de ruta del archivo serializado...");
    }
}
