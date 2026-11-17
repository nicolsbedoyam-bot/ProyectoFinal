package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ManejoImagenTest {

    private static final Logger LOG = Logger.getLogger(ManejoImagenTest.class.getName());

    private File carpeta;
    private ManejoImagen manejo;
    private File imagenOrigen;

    @BeforeEach
    void preparar() throws IOException {
        carpeta = new File(System.getProperty("java.io.tmpdir"), "sga_fotos_prueba_" + System.nanoTime());
        manejo = new ManejoImagen(carpeta.getPath());
        imagenOrigen = File.createTempFile("foto", ".png");
    }

    //Borra los archivos que creó la prueba
    @AfterEach
    void limpiar() {
        imagenOrigen.delete();
        File[] archivos = carpeta.listFiles();
        if (archivos != null) {
            for (int i = 0; i < archivos.length; i++) {
                archivos[i].delete();
            }
        }
        carpeta.delete();
    }

    @DisplayName("Solo se aceptan imágenes png, jpg y jpeg")
    @Test
    void testFormatoValido() {
        LOG.info("Iniciando prueba de formatos...");

        assertTrue(manejo.esFormatoValido(new File("foto.png")));
        assertTrue(manejo.esFormatoValido(new File("foto.jpg")));
        assertTrue(manejo.esFormatoValido(new File("foto.jpeg")));
        assertTrue(manejo.esFormatoValido(new File("FOTO.PNG")));
        assertFalse(manejo.esFormatoValido(new File("foto.gif")));
        assertFalse(manejo.esFormatoValido(new File("documento.pdf")));
        assertFalse(manejo.esFormatoValido(new File("sinextension")));
        assertFalse(manejo.esFormatoValido(null));

        LOG.info("Finalizando prueba de formatos...");
    }

    @DisplayName("Guardar una imagen la copia a la carpeta de destino")
    @Test
    void testGuardarImagen() {
        LOG.info("Iniciando prueba de guardar imagen...");

        String ruta = manejo.guardarImagen(imagenOrigen, "123.png");

        assertNotNull(ruta);
        assertTrue(new File(ruta).exists());
        assertEquals("123.png", new File(ruta).getName());
        assertEquals(carpeta.getPath(), new File(ruta).getParent());

        LOG.info("Finalizando prueba de guardar imagen...");
    }

    @DisplayName("Una imagen inválida o datos vacíos no se guardan")
    @Test
    void testGuardarInvalido() throws IOException {
        LOG.info("Iniciando prueba de guardar inválido...");

        File texto = File.createTempFile("notas", ".txt");

        assertNull(manejo.guardarImagen(texto, "notas.png"));
        assertNull(manejo.guardarImagen(null, "123.png"));
        assertNull(manejo.guardarImagen(imagenOrigen, null));
        assertNull(manejo.guardarImagen(imagenOrigen, ""));
        assertNull(manejo.guardarImagen(new File("no_existe.png"), "123.png"));

        texto.delete();

        LOG.info("Finalizando prueba de guardar inválido...");
    }

    @DisplayName("El nombre no puede sacar el archivo de la carpeta de destino")
    @Test
    void testNombreNoEscapaDeLaCarpeta() {
        LOG.info("Iniciando prueba de seguridad del nombre...");

        String ruta = manejo.guardarImagen(imagenOrigen, "../../escapado.png");

        assertNotNull(ruta);
        assertEquals(carpeta.getPath(), new File(ruta).getParent());
        assertEquals("escapado.png", new File(ruta).getName());

        LOG.info("Finalizando prueba de seguridad del nombre...");
    }

    @DisplayName("Cargar una imagen devuelve el archivo si existe y null si no")
    @Test
    void testCargarImagen() {
        LOG.info("Iniciando prueba de cargar imagen...");

        String ruta = manejo.guardarImagen(imagenOrigen, "123.png");

        assertNotNull(manejo.cargarImagen(ruta));
        assertNull(manejo.cargarImagen("no_existe.png"));
        assertNull(manejo.cargarImagen(null));
        assertNull(manejo.cargarImagen(""));

        LOG.info("Finalizando prueba de cargar imagen...");
    }

    @DisplayName("La carpeta de destino se puede cambiar")
    @Test
    void testCarpetaDestino() {
        LOG.info("Iniciando prueba de carpeta de destino...");

        assertEquals(carpeta.getPath(), manejo.getCarpetaDestino());

        manejo.setCarpetaDestino("otra_carpeta");
        assertEquals("otra_carpeta", manejo.getCarpetaDestino());

        LOG.info("Finalizando prueba de carpeta de destino...");
    }
}
