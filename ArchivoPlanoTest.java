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

//El archivo plano guarda la empresa, los apartamentos con sus tarifas, las temporadas y los bloqueos
class ArchivoPlanoTest {

    private static final Logger LOG = Logger.getLogger(ArchivoPlanoTest.class.getName());

    private File archivo;
    private ArchivoPlano persistencia;
    private Empresa empresa;

    @BeforeEach
    void preparar() throws IOException {
        archivo = File.createTempFile("sga_plano", ".txt");
        persistencia = new ArchivoPlano(archivo.getPath());

        empresa = new Empresa("900.123", "Aparta-Suites UQ", "Salento, Quindío");
        LinkedList<String> caracteristicas = new LinkedList<>();
        caracteristicas.add("Balcón");
        caracteristicas.add("Jacuzzi");
        Apartamento a1 = new Apartamento("A1", "Suite Montañas", 2, 4, caracteristicas, new LinkedList<>());
        empresa.registrarApartamento(a1);
        empresa.definirTarifa(a1, TemporadaAlojamiento.BAJA, 60000);
        empresa.definirTarifa(a1, TemporadaAlojamiento.ALTA, 110000);
        Apartamento a2 = new Apartamento("A2", "Suite Cafetal", 1, 2, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(a2);
        a2.registrarMotivoFueraDeServicio("Daño en la tubería");
        empresa.registrarTemporada(new Temporada(LocalDate.of(2026, 12, 15), LocalDate.of(2027, 1, 15), TemporadaAlojamiento.ALTA));
        empresa.registrarBloqueo(new Bloqueo(a1, LocalDate.of(2027, 3, 10), LocalDate.of(2027, 3, 12), "Pintura"));
    }

    @AfterEach
    void limpiar() {
        archivo.delete();
    }

    @DisplayName("Guardar y cargar conserva la empresa y sus apartamentos")
    @Test
    void testEmpresaYApartamentos() {
        LOG.info("Iniciando prueba de archivo plano (apartamentos)...");

        persistencia.guardar(empresa);
        Empresa cargada = persistencia.cargar();

        assertEquals("900.123", cargada.getNit());
        assertEquals("Aparta-Suites UQ", cargada.getNombre());
        assertEquals("Salento, Quindío", cargada.getUbicacion());
        assertEquals(2, cargada.getApartamentos().size());

        Apartamento a1 = cargada.buscarApartamento("A1");
        assertEquals("Suite Montañas", a1.getNombre());
        assertEquals(2, a1.getNumeroDormitorios());
        assertEquals(4, a1.getCapacidadMaxima());
        assertEquals(2, a1.getCaracteristicas().size());
        assertEquals("Jacuzzi", a1.getCaracteristicas().get(1));
        assertTrue(a1.isActivo());

        LOG.info("Finalizando prueba de archivo plano (apartamentos)...");
    }

    @DisplayName("Guardar y cargar conserva el estado fuera de servicio con su motivo")
    @Test
    void testFueraDeServicio() {
        LOG.info("Iniciando prueba de archivo plano (fuera de servicio)...");

        persistencia.guardar(empresa);
        Apartamento a2 = persistencia.cargar().buscarApartamento("A2");

        assertEquals(EstadoOperativo.FUERA_DE_SERVICIO, a2.getEstadoOperativo());
        assertEquals("Daño en la tubería", a2.getFueraDeServicio());

        LOG.info("Finalizando prueba de archivo plano (fuera de servicio)...");
    }

    @DisplayName("Guardar y cargar conserva las tarifas, las temporadas y los bloqueos")
    @Test
    void testTarifasTemporadasYBloqueos() {
        LOG.info("Iniciando prueba de archivo plano (tarifas, temporadas y bloqueos)...");

        persistencia.guardar(empresa);
        Empresa cargada = persistencia.cargar();

        Apartamento a1 = cargada.buscarApartamento("A1");
        assertEquals(60000, a1.getTarifa(TemporadaAlojamiento.BAJA));
        assertEquals(110000, a1.getTarifa(TemporadaAlojamiento.ALTA));
        assertEquals(0, a1.getTarifa(TemporadaAlojamiento.MEDIA));

        assertEquals(1, cargada.getTemporadas().size());
        assertEquals(TemporadaAlojamiento.ALTA, cargada.tipoTemporada(LocalDate.of(2026, 12, 31)));

        assertEquals(1, cargada.getBloqueos().size());
        assertEquals("Pintura", cargada.getBloqueos().get(0).getMotivo());
        assertEquals("A1", cargada.getBloqueos().get(0).getApartamento().getCodigo());

        LOG.info("Finalizando prueba de archivo plano (tarifas, temporadas y bloqueos)...");
    }

    @DisplayName("Si el archivo no existe se devuelve una empresa vacía")
    @Test
    void testArchivoInexistente() {
        LOG.info("Iniciando prueba de archivo plano inexistente...");

        archivo.delete();
        Empresa cargada = persistencia.cargar();

        assertEquals("Sin informacion", cargada.getNombre());
        assertTrue(cargada.getApartamentos().isEmpty());

        LOG.info("Finalizando prueba de archivo plano inexistente...");
    }

    @DisplayName("La ruta del archivo se puede cambiar")
    @Test
    void testRuta() {
        LOG.info("Iniciando prueba de ruta del archivo plano...");

        assertEquals(archivo.getPath(), persistencia.getRutaArchivo());

        persistencia.setRutaArchivo("otra.txt");
        assertEquals("otra.txt", persistencia.getRutaArchivo());

        LOG.info("Finalizando prueba de ruta del archivo plano...");
    }
}
