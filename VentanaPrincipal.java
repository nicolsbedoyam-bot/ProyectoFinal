package co.edu.uniquindio.ProjectoFinal;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedList;

/*
 * Parte gráfica del programa (JavaFX). Todas las pantallas están en esta clase.
 *
 * Cómo leerla:
 *  - Cada pantalla o pestaña se crea en un método "crear...", "mostrar..." (los campos y botones).
 *  - Cada botón llama a un método con "boton.setOnAction(e -> metodo());". Eso solo significa:
 *    "cuando hagan clic, ejecuta metodo()".
 *  - Las listas que se ven en pantalla (ListView) tienen una LinkedList "paralela" con los objetos reales:
 *    la posición que se elige en la lista es la posición del objeto en la LinkedList.
 *  - Las reglas del negocio NO están aquí: están en Empresa, Reserva, etc. Si una regla se incumple,
 *    esas clases lanzan una excepción y aquí se muestra el mensaje en una ventana de error.
 */
public class VentanaPrincipal extends Application {

    //Datos principales del programa
    private Stage ventana;
    private Empresa empresa;
    private ArchivoSerializado archivo = new ArchivoSerializado("datos/sga.dat");
    private Rol usuarioActual;

    //Inicio de sesión
    private TextField txtUsuario;
    private PasswordField txtContrasenia;

    //Registro de huésped
    private TextField txtRegNombre;
    private TextField txtRegCedula;
    private TextField txtRegTelefono;
    private TextField txtRegCorreo;
    private TextField txtRegUsuario;
    private PasswordField txtRegContrasenia;
    private DatePicker dpRegNacimiento;
    private Label lblRegFoto;
    private File fotoRegistro;

    //Perfil del huésped
    private TextField txtPerfilTelefono;
    private TextField txtPerfilCorreo;
    private PasswordField txtPerfilContrasenia;
    private Label lblPerfilFoto;

    //Nueva reserva
    private boolean nuevaEsPortal;
    private ComboBox<String> cmbCanal;
    private ComboBox<String> cmbTitular;
    private LinkedList<Huesped> titulares;
    private TextField txtCodigoExterno;
    private TextField txtOcNombre;
    private DatePicker dpOcNacimiento;
    private CheckBox chkTitularSeAloja;
    private ListView<String> listaOcupantes;
    private LinkedList<Ocupante> ocupantes;
    private DatePicker dpEntrada;
    private DatePicker dpSalida;
    private ListView<String> listaDisponibles;
    private LinkedList<Apartamento> disponibles;
    private TextArea txtCotizacion;

    //Lista de reservas, pagos y folio
    private ListView<String> listaReservas;
    private LinkedList<Reserva> reservasMostradas;
    private TextField txtMonto;
    private ComboBox<MedioPago> cmbMedio;
    private TextField txtConceptoCargo;
    private TextField txtMontoCargo;
    private ComboBox<TipoCargo> cmbTipoCargo;
    private TextArea txtFolio;

    //Recepción: apartamentos y novedades
    private ListView<String> listaAptosRecepcion;
    private LinkedList<Apartamento> aptosRecepcion;
    private ComboBox<EstadoOperativo> cmbEstado;
    private TextField txtMotivoFuera;
    private ComboBox<TipoNovedad> cmbTipoNovedad;
    private TextField txtDescNovedad;
    private TextArea txtNovedades;

    //Recepción: huéspedes y canal externo
    private ListView<String> listaHuespedes;
    private LinkedList<Huesped> huespedesLista;
    private TextArea txtHistorial;
    private TextField txtCantidadSim;
    private TextArea txtConflictos;

    //Calendario
    private ComboBox<String> cmbAptoCalendario;
    private LinkedList<Apartamento> aptosCalendario;
    private TextField txtAnio;
    private TextField txtMes;
    private TextArea txtCalendario;

    //Administrador: apartamentos
    private ListView<String> listaAptosAdmin;
    private LinkedList<Apartamento> aptosAdmin;
    private TextField txtAptCodigo;
    private TextField txtAptNombre;
    private TextField txtAptDormitorios;
    private TextField txtAptCapacidad;
    private TextField txtAptCaracteristicas;
    private Label lblAptFoto;
    private File fotoApto;
    private TextField txtTarifaBaja;
    private TextField txtTarifaMedia;
    private TextField txtTarifaAlta;

    //Administrador: temporadas
    private ListView<String> listaTemporadas;
    private LinkedList<Temporada> temporadasLista;
    private DatePicker dpTempInicio;
    private DatePicker dpTempFin;
    private ComboBox<TemporadaAlojamiento> cmbTempTipo;

    //Administrador: política de cancelación
    private TextField txtPolDevolucion;
    private TextField txtPolRetencion;
    private TextField txtPolPorcentaje;
    private Label lblPolResumen;

    //Administrador: bloqueos
    private ListView<String> listaBloqueos;
    private LinkedList<Bloqueo> bloqueosLista;
    private ComboBox<String> cmbBloqApto;
    private LinkedList<Apartamento> aptosBloqueo;
    private DatePicker dpBloqInicio;
    private DatePicker dpBloqFin;
    private TextField txtBloqMotivo;

    //Administrador: empleados
    private ListView<String> listaEmpleados;
    private LinkedList<Recepcionista> empleadosLista;
    private TextField txtEmpNombre;
    private TextField txtEmpCedula;
    private TextField txtEmpTelefono;
    private TextField txtEmpCorreo;
    private TextField txtEmpUsuario;
    private PasswordField txtEmpContrasenia;

    //Administrador: reportes
    private ComboBox<String> cmbReporte;
    private DatePicker dpRepDesde;
    private DatePicker dpRepHasta;
    private TextArea txtReporte;

    // =====================================================================
    // INICIO DEL PROGRAMA
    // =====================================================================

    @Override
    public void start(Stage escenario) {
        ventana = escenario;
        cargarDatos();
        mostrarLogin();
        ventana.show();
    }

    @Override
    public void stop() {
        guardar();
    }

    //Carga los datos guardados; si no hay (o no se pueden leer) crea datos de prueba
    private void cargarDatos() {
        File fichero = new File("datos/sga.dat");
        if (fichero.exists()) {
            try {
                empresa = archivo.cargar();
                return;
            } catch (Exception ex) {
                //El archivo no sirve (por ejemplo, es de una versión anterior): se respalda y se empieza de nuevo
                fichero.renameTo(new File("datos/sga.dat.bak"));
            }
        }
        empresa = crearDatosIniciales();
        guardar();
    }

    private void guardar() {
        try {
            new File("datos").mkdirs();
            archivo.guardar(empresa);
        } catch (Exception ex) {
            System.out.println("No se pudo guardar: " + ex.getMessage());
        }
    }

    //Datos de prueba para la primera vez que se abre el programa
    private Empresa crearDatosIniciales() {
        Empresa e = new Empresa("900.123.456-7", "Aparta-Suites UQ", "Salento, Quindío");
        LocalDate hoy = LocalDate.now();

        e.registrarAdministrador(new AdministradorAlojamiento("Administrador", "1000000001", "3000000001", "admin@apartasuitesuq.com", "admin", "admin123"));
        e.registrarRecepcionista(new Recepcionista("Recepción Principal", "1000000002", "3000000002", "recepcion@apartasuitesuq.com", "recepcion", "recepcion123", hoy, true));
        e.registrarHuesped(new Huesped("Huésped de Prueba", "1000000003", "3000000003", "huesped@correo.com", "huesped", "huesped123", LocalDate.of(1995, 5, 10), null));

        LinkedList<String> caracteristicas1 = new LinkedList<>();
        caracteristicas1.add("Balcón");
        caracteristicas1.add("Vista a las montañas");
        crearApartamentoDePrueba(e, "AP-101", "Suite Montañas", 2, 4, caracteristicas1, 60000, 80000, 110000);

        LinkedList<String> caracteristicas2 = new LinkedList<>();
        caracteristicas2.add("Jacuzzi");
        caracteristicas2.add("Chimenea");
        crearApartamentoDePrueba(e, "AP-102", "Suite Cafetal", 1, 2, caracteristicas2, 70000, 90000, 120000);

        LinkedList<String> caracteristicas3 = new LinkedList<>();
        caracteristicas3.add("Terraza");
        caracteristicas3.add("Cocina completa");
        crearApartamentoDePrueba(e, "AP-201", "Apartamento Los Nevados", 3, 6, caracteristicas3, 55000, 75000, 100000);

        LinkedList<String> caracteristicas4 = new LinkedList<>();
        caracteristicas4.add("Balcón");
        caracteristicas4.add("Chimenea");
        crearApartamentoDePrueba(e, "AP-202", "Apartamento Cocora", 2, 5, caracteristicas4, 58000, 78000, 105000);

        //Temporadas. La baja no se define porque cubre todas las fechas que no pertenezcan a otra temporada
        int anio = hoy.getYear();
        for (int y = anio - 1; y <= anio + 1; y++) {
            //Alta: diciembre y enero
            e.registrarTemporada(new Temporada(LocalDate.of(y, 12, 15), LocalDate.of(y + 1, 1, 15), TemporadaAlojamiento.ALTA));
        }
        for (int y = anio; y <= anio + 1; y++) {
            //Alta: vacaciones de mitad de año
            e.registrarTemporada(new Temporada(LocalDate.of(y, 6, 15), LocalDate.of(y, 7, 15), TemporadaAlojamiento.ALTA));
            //Media: puentes festivos
            e.registrarTemporada(new Temporada(LocalDate.of(y, 10, 10), LocalDate.of(y, 10, 13), TemporadaAlojamiento.MEDIA));
            e.registrarTemporada(new Temporada(LocalDate.of(y, 11, 1), LocalDate.of(y, 11, 3), TemporadaAlojamiento.MEDIA));
        }
        //Alta: Semana Santa (del Domingo de Ramos al Domingo de Pascua)
        LocalDate[] pascuas = {LocalDate.of(2025, 4, 20), LocalDate.of(2026, 4, 5), LocalDate.of(2027, 3, 28), LocalDate.of(2028, 4, 16)};
        for (int i = 0; i < pascuas.length; i++) {
            e.registrarTemporada(new Temporada(pascuas[i].minusDays(7), pascuas[i], TemporadaAlojamiento.ALTA));
        }
        return e;
    }

    private void crearApartamentoDePrueba(Empresa e, String codigo, String nombre, int dormitorios, int capacidad, LinkedList<String> caracteristicas, int baja, int media, int alta) {
        Apartamento a = new Apartamento(codigo, nombre, dormitorios, capacidad, caracteristicas, new LinkedList<String>());
        e.registrarApartamento(a);
        e.definirTarifa(a, TemporadaAlojamiento.BAJA, baja);
        e.definirTarifa(a, TemporadaAlojamiento.MEDIA, media);
        e.definirTarifa(a, TemporadaAlojamiento.ALTA, alta);
    }

    // =====================================================================
    // AYUDAS PARA CONSTRUIR PANTALLAS Y LEER CAMPOS
    // =====================================================================

    private void mostrarPantalla(Parent raiz, int ancho, int alto, String titulo) {
        ventana.setTitle(titulo);
        ventana.setScene(new Scene(raiz, ancho, alto));
        ventana.centerOnScreen();
    }

    private VBox nuevaCaja() {
        VBox caja = new VBox(8);
        caja.setPadding(new Insets(10));
        return caja;
    }

    //Una fila con una etiqueta y un campo
    private HBox fila(String etiqueta, Node campo) {
        HBox fila = new HBox(8);
        fila.getChildren().addAll(new Label(etiqueta), campo);
        return fila;
    }

    private ScrollPane conScroll(VBox caja) {
        ScrollPane scroll = new ScrollPane(caja);
        scroll.setFitToWidth(true);
        return scroll;
    }

    //Pantalla con barra superior (nombre y "Cerrar sesión") y pestañas
    private void mostrarConPestanas(TabPane pestanas, String saludo, String titulo) {
        Label lblSaludo = new Label(saludo);
        Button btnSalir = new Button("Cerrar sesión");
        btnSalir.setOnAction(e -> cerrarSesion());
        HBox barra = new HBox(15);
        barra.setPadding(new Insets(8));
        barra.getChildren().addAll(lblSaludo, btnSalir);

        BorderPane raiz = new BorderPane();
        raiz.setTop(barra);
        raiz.setCenter(pestanas);
        mostrarPantalla(raiz, 1050, 720, titulo);
    }

    private void agregarPestana(TabPane pestanas, String titulo, Node contenido) {
        Tab pestana = new Tab(titulo, contenido);
        pestana.setClosable(false);
        pestana.setOnSelectionChanged(e -> actualizarListas());
        pestanas.getTabs().add(pestana);
    }

    private void cerrarSesion() {
        guardar();
        mostrarLogin();
    }

    private void mensaje(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    private void error(String texto) {
        if (texto == null) {
            texto = "Ocurrió un error inesperado";
        }
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    private boolean confirmar(String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        //showAndWait() muestra la ventana y devuelve el botón que se presionó (si la cierran sin elegir, se toma como Cancelar)
        ButtonType respuesta = alerta.showAndWait().orElse(ButtonType.CANCEL);
        return respuesta == ButtonType.OK;
    }

    //Lectura de campos: si el dato no sirve se lanza una excepción con un mensaje claro
    private String leerTexto(TextField campo, String nombre) {
        String valor = campo.getText().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + nombre + "\" es obligatorio");
        }
        return valor;
    }

    private int leerEntero(TextField campo, String nombre) {
        String valor = leerTexto(campo, nombre).replace(".", "");
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo \"" + nombre + "\" debe ser un número entero");
        }
    }

    private double leerDecimal(TextField campo, String nombre) {
        String valor = leerTexto(campo, nombre).replace(",", ".");
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo \"" + nombre + "\" debe ser un número");
        }
    }

    private LocalDate leerFecha(DatePicker selector, String nombre) {
        if (selector.getValue() == null) {
            throw new IllegalArgumentException("Debes indicar " + nombre);
        }
        return selector.getValue();
    }

    //Posición elegida en una lista o en una lista desplegable
    private int posicionElegida(ListView<String> lista, String nombre) {
        int posicion = lista.getSelectionModel().getSelectedIndex();
        if (posicion < 0) {
            throw new IllegalArgumentException("Selecciona " + nombre + " de la lista");
        }
        return posicion;
    }

    private int posicionCombo(ComboBox<String> combo, String nombre) {
        int posicion = combo.getSelectionModel().getSelectedIndex();
        if (posicion < 0) {
            throw new IllegalArgumentException("Selecciona " + nombre);
        }
        return posicion;
    }

    //Llenan una lista o un desplegable con textos, conservando lo que estaba seleccionado
    private void llenarLista(ListView<String> lista, LinkedList<String> textos) {
        int seleccionado = lista.getSelectionModel().getSelectedIndex();
        lista.getItems().clear();
        for (String texto : textos) {
            lista.getItems().add(texto);
        }
        if (seleccionado >= 0 && seleccionado < textos.size()) {
            lista.getSelectionModel().select(seleccionado);
        }
    }

    private void llenarCombo(ComboBox<String> combo, LinkedList<String> textos) {
        int seleccionado = combo.getSelectionModel().getSelectedIndex();
        combo.getItems().clear();
        for (String texto : textos) {
            combo.getItems().add(texto);
        }
        if (seleccionado >= 0 && seleccionado < textos.size()) {
            combo.getSelectionModel().select(seleccionado);
        }
    }

    //Fotografías: se elige un archivo y ManejoImagen lo copia a una carpeta del programa
    private File elegirImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Elegir fotografía (png, jpg o jpeg)");
        return selector.showOpenDialog(ventana);
    }

    private String extension(File archivoImagen) {
        String nombre = archivoImagen.getName();
        int punto = nombre.lastIndexOf('.');
        if (punto < 0) {
            return "";
        }
        return nombre.substring(punto).toLowerCase();
    }

    // =====================================================================
    // INICIO DE SESIÓN Y REGISTRO DE HUÉSPEDES
    // =====================================================================

    private void mostrarLogin() {
        usuarioActual = null;
        txtUsuario = new TextField();
        txtUsuario.setPromptText("Usuario");
        txtContrasenia = new PasswordField();
        txtContrasenia.setPromptText("Contraseña");

        Button btnIngresar = new Button("Ingresar");
        btnIngresar.setOnAction(e -> ingresar());
        Button btnRegistro = new Button("Registrarme como huésped");
        btnRegistro.setOnAction(e -> mostrarRegistro());
        Label ayuda = new Label("Datos de prueba: admin / admin123, recepcion / recepcion123, huesped / huesped123");
        ayuda.setWrapText(true);

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label(empresa.getNombre()), new Label("Sistema de Gestión de Alojamiento"),
                txtUsuario, txtContrasenia, btnIngresar, btnRegistro, ayuda);
        mostrarPantalla(caja, 420, 330, "SGA - Inicio de sesión");
    }

    private void ingresar() {
        try {
            Rol rol = empresa.autenticar(txtUsuario.getText().trim(), txtContrasenia.getText());
            if (rol == null) {
                error("Usuario o contraseña incorrectos (o la cuenta está desvinculada)");
                return;
            }
            //Las reservas Pendientes con más de 24 horas sin confirmar se cancelan
            empresa.cancelarPendientesVencidas();
            guardar();
            usuarioActual = rol;
            if (rol instanceof Huesped) {
                mostrarHuesped();
            } else if (rol instanceof Recepcionista) {
                mostrarRecepcion();
            } else {
                mostrarAdministrador();
            }
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void mostrarRegistro() {
        Button btnVolver = new Button("Volver al inicio de sesión");
        btnVolver.setOnAction(e -> mostrarLogin());
        VBox caja = crearFormularioHuesped();
        caja.getChildren().add(btnVolver);
        mostrarPantalla(conScroll(caja), 460, 520, "SGA - Registro de huésped");
    }

    //Formulario para registrar un huésped (lo usan el propio huésped y la recepción)
    private VBox crearFormularioHuesped() {
        txtRegNombre = new TextField();
        txtRegCedula = new TextField();
        txtRegTelefono = new TextField();
        txtRegCorreo = new TextField();
        dpRegNacimiento = new DatePicker();
        txtRegUsuario = new TextField();
        txtRegContrasenia = new PasswordField();
        lblRegFoto = new Label("Sin fotografía (opcional)");
        fotoRegistro = null;

        Button btnFoto = new Button("Elegir fotografía");
        btnFoto.setOnAction(e -> elegirFotoRegistro());
        Button btnRegistrar = new Button("Registrar huésped");
        btnRegistrar.setOnAction(e -> registrarHuesped());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("REGISTRO DE HUÉSPED"),
                fila("Nombre completo", txtRegNombre), fila("Documento", txtRegCedula),
                fila("Teléfono", txtRegTelefono), fila("Correo", txtRegCorreo),
                fila("Fecha de nacimiento", dpRegNacimiento), fila("Usuario", txtRegUsuario),
                fila("Contraseña", txtRegContrasenia), fila("Fotografía", btnFoto), lblRegFoto, btnRegistrar);
        return caja;
    }

    private void elegirFotoRegistro() {
        File elegida = elegirImagen();
        if (elegida != null) {
            fotoRegistro = elegida;
            lblRegFoto.setText(elegida.getName());
        }
    }

    private void registrarHuesped() {
        try {
            String cedula = leerTexto(txtRegCedula, "Documento");
            LocalDate nacimiento = leerFecha(dpRegNacimiento, "la fecha de nacimiento");
            if (nacimiento.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            Huesped nuevo = new Huesped(leerTexto(txtRegNombre, "Nombre"), cedula, leerTexto(txtRegTelefono, "Teléfono"),
                    leerTexto(txtRegCorreo, "Correo"), leerTexto(txtRegUsuario, "Usuario"),
                    leerTexto(txtRegContrasenia, "Contraseña"), nacimiento, null);
            empresa.registrarHuesped(nuevo);

            //La fotografía es opcional: sirve para la verificación de identidad
            if (fotoRegistro != null) {
                ManejoImagen manejo = new ManejoImagen("fotos/huespedes");
                String ruta = manejo.guardarImagen(fotoRegistro, cedula + extension(fotoRegistro));
                if (ruta == null) {
                    mensaje("El huésped quedó registrado, pero la fotografía no es válida (usa png, jpg o jpeg).");
                } else {
                    nuevo.setRutaFotografia(ruta);
                }
            }
            guardar();
            txtRegNombre.clear();
            txtRegCedula.clear();
            txtRegTelefono.clear();
            txtRegCorreo.clear();
            txtRegUsuario.clear();
            txtRegContrasenia.clear();
            dpRegNacimiento.setValue(null);
            fotoRegistro = null;
            lblRegFoto.setText("Sin fotografía (opcional)");
            actualizarListas();
            mensaje("El huésped quedó registrado.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // =====================================================================
    // PANTALLA DEL HUÉSPED
    // =====================================================================

    private void mostrarHuesped() {
        TabPane pestanas = new TabPane();
        agregarPestana(pestanas, "Mi perfil", crearPestanaPerfil());
        agregarPestana(pestanas, "Nueva reserva", crearPestanaNuevaReserva(true));
        agregarPestana(pestanas, "Mis reservas", crearPestanaReservas(false));
        mostrarConPestanas(pestanas, "Bienvenido, " + usuarioActual.getNombre() + " (Huésped)", "SGA - Huésped");
        actualizarListas();
    }

    private Node crearPestanaPerfil() {
        Huesped h = (Huesped) usuarioActual;
        txtPerfilTelefono = new TextField(h.getTelefono());
        txtPerfilCorreo = new TextField(h.getCorreo());
        txtPerfilContrasenia = new PasswordField();
        txtPerfilContrasenia.setPromptText("Vacía para no cambiarla");
        lblPerfilFoto = new Label();
        textoFotoPerfil();

        Button btnGuardar = new Button("Guardar cambios");
        btnGuardar.setOnAction(e -> guardarPerfil());
        Button btnFoto = new Button("Cambiar fotografía");
        btnFoto.setOnAction(e -> cambiarFotoPerfil());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("MI PERFIL"), new Label("Nombre: " + h.getNombre()),
                new Label("Documento: " + h.getCedula()), new Label("Fecha de nacimiento: " + h.getFechaNacimiento()),
                new Label("Usuario: " + h.getUsuario()), fila("Teléfono", txtPerfilTelefono), fila("Correo", txtPerfilCorreo),
                fila("Nueva contraseña", txtPerfilContrasenia), btnGuardar, lblPerfilFoto, btnFoto);
        return conScroll(caja);
    }

    private void textoFotoPerfil() {
        Huesped h = (Huesped) usuarioActual;
        if (h.getRutaFotografia() == null) {
            lblPerfilFoto.setText("Fotografía: sin fotografía");
        } else {
            lblPerfilFoto.setText("Fotografía guardada en: " + h.getRutaFotografia());
        }
    }

    private void guardarPerfil() {
        try {
            Huesped h = (Huesped) usuarioActual;
            String telefono = leerTexto(txtPerfilTelefono, "Teléfono");
            String correo = leerTexto(txtPerfilCorreo, "Correo");
            h.setTelefono(telefono);
            h.setCorreo(correo);
            if (!txtPerfilContrasenia.getText().isBlank()) {
                h.setContrasenia(txtPerfilContrasenia.getText());
                txtPerfilContrasenia.clear();
            }
            guardar();
            mensaje("Tus datos se actualizaron.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void cambiarFotoPerfil() {
        try {
            File elegida = elegirImagen();
            if (elegida == null) {
                return;
            }
            Huesped h = (Huesped) usuarioActual;
            ManejoImagen manejo = new ManejoImagen("fotos/huespedes");
            String ruta = manejo.guardarImagen(elegida, h.getCedula() + extension(elegida));
            if (ruta == null) {
                throw new IllegalArgumentException("La imagen no es válida (usa png, jpg o jpeg)");
            }
            h.setRutaFotografia(ruta);
            guardar();
            textoFotoPerfil();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // =====================================================================
    // NUEVA RESERVA (la usa el huésped por el Portal y la recepción por los canales Directo y Externo)
    // =====================================================================

    private Node crearPestanaNuevaReserva(boolean esPortal) {
        nuevaEsPortal = esPortal;
        ocupantes = new LinkedList<>();
        disponibles = new LinkedList<>();
        titulares = new LinkedList<>();

        VBox caja = nuevaCaja();
        if (esPortal) {
            caja.getChildren().add(new Label("NUEVA RESERVA (canal Portal). Titular: " + usuarioActual.getNombre()));
        } else {
            cmbCanal = new ComboBox<>();
            cmbCanal.getItems().add("DIRECTA");
            cmbCanal.getItems().add("EXTERNA");
            cmbCanal.setValue("DIRECTA");
            cmbTitular = new ComboBox<>();
            txtCodigoExterno = new TextField();
            txtCodigoExterno.setPromptText("Solo para reservas externas");
            Button btnActualizar = new Button("Actualizar lista de huéspedes");
            btnActualizar.setOnAction(e -> llenarTitulares());
            caja.getChildren().addAll(new Label("NUEVA RESERVA (recepción)"), fila("Canal", cmbCanal),
                    fila("Titular", cmbTitular), btnActualizar, fila("Código externo", txtCodigoExterno));
        }

        //1. Ocupantes
        txtOcNombre = new TextField();
        txtOcNombre.setPromptText("Nombre del ocupante");
        dpOcNacimiento = new DatePicker();
        chkTitularSeAloja = new CheckBox("El titular también se aloja");
        chkTitularSeAloja.setSelected(true);
        listaOcupantes = new ListView<>();
        listaOcupantes.setPrefHeight(110);
        Button btnAgregarOcupante = new Button("Agregar ocupante");
        btnAgregarOcupante.setOnAction(e -> agregarOcupante());
        Button btnQuitarOcupante = new Button("Quitar ocupante seleccionado");
        btnQuitarOcupante.setOnAction(e -> quitarOcupante());
        caja.getChildren().addAll(new Label("1. Ocupantes (todos cuentan para la capacidad; los menores de 6 años no pagan)"),
                fila("Nombre", txtOcNombre), fila("Fecha de nacimiento", dpOcNacimiento), btnAgregarOcupante,
                listaOcupantes, btnQuitarOcupante, chkTitularSeAloja);

        //2. Fechas y apartamentos disponibles
        dpEntrada = new DatePicker();
        dpSalida = new DatePicker();
        listaDisponibles = new ListView<>();
        listaDisponibles.setPrefHeight(120);
        Button btnBuscar = new Button("Buscar apartamentos disponibles");
        btnBuscar.setOnAction(e -> buscarDisponibles());
        caja.getChildren().addAll(new Label("2. Fechas (la noche de la fecha de salida no se cobra)"),
                fila("Entrada", dpEntrada), fila("Salida", dpSalida), btnBuscar, listaDisponibles);

        //3. Cotización y reserva
        txtCotizacion = new TextArea();
        txtCotizacion.setEditable(false);
        txtCotizacion.setPrefRowCount(9);
        Button btnCotizar = new Button("Cotizar el apartamento seleccionado");
        btnCotizar.setOnAction(e -> cotizarEstancia());
        Button btnReservar = new Button("Crear la reserva");
        btnReservar.setOnAction(e -> crearReservaNueva());
        caja.getChildren().addAll(new Label("3. Cotización y reserva"), btnCotizar, txtCotizacion, btnReservar);
        return conScroll(caja);
    }

    private void llenarTitulares() {
        if (cmbTitular == null) {
            return;
        }
        titulares = empresa.getHuespedes();
        LinkedList<String> textos = new LinkedList<>();
        for (Huesped h : titulares) {
            textos.add(h.getCedula() + " - " + h.getNombre());
        }
        llenarCombo(cmbTitular, textos);
    }

    private Huesped titularElegido() {
        if (nuevaEsPortal) {
            return (Huesped) usuarioActual;
        }
        return titulares.get(posicionCombo(cmbTitular, "el titular de la reserva"));
    }

    //Los ocupantes agregados y, si se marcó, el titular
    private LinkedList<Ocupante> armarOcupantes() {
        LinkedList<Ocupante> lista = new LinkedList<>(ocupantes);
        if (chkTitularSeAloja.isSelected()) {
            Huesped titular = titularElegido();
            lista.addFirst(new Ocupante(titular.getNombre(), titular.getFechaNacimiento()));
        }
        if (lista.isEmpty()) {
            throw new IllegalArgumentException("Registra al menos un ocupante");
        }
        return lista;
    }

    private void agregarOcupante() {
        try {
            String nombre = leerTexto(txtOcNombre, "Nombre del ocupante");
            LocalDate nacimiento = leerFecha(dpOcNacimiento, "la fecha de nacimiento del ocupante");
            if (nacimiento.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            ocupantes.add(new Ocupante(nombre, nacimiento));
            txtOcNombre.clear();
            dpOcNacimiento.setValue(null);
            llenarOcupantes();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void quitarOcupante() {
        try {
            ocupantes.remove(posicionElegida(listaOcupantes, "un ocupante"));
            llenarOcupantes();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void llenarOcupantes() {
        LinkedList<String> textos = new LinkedList<>();
        for (Ocupante o : ocupantes) {
            textos.add(o.getNombre() + " | nació el " + o.getFechaNacimiento() + " | edad hoy: " + o.calcularEdad(LocalDate.now()));
        }
        llenarLista(listaOcupantes, textos);
    }

    private void buscarDisponibles() {
        try {
            LocalDate entrada = leerFecha(dpEntrada, "la fecha de entrada");
            LocalDate salida = leerFecha(dpSalida, "la fecha de salida");
            disponibles = empresa.buscarDisponibles(entrada, salida, armarOcupantes().size());
            LinkedList<String> textos = new LinkedList<>();
            for (Apartamento a : disponibles) {
                textos.add(a + " | dormitorios " + a.getNumeroDormitorios() + " | capacidad " + a.getCapacidadMaxima() + " | " + a.getCaracteristicas());
            }
            llenarLista(listaDisponibles, textos);
            txtCotizacion.clear();
            if (disponibles.isEmpty()) {
                mensaje("No hay apartamentos disponibles para esas fechas y ese número de personas.");
            }
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void cotizarEstancia() {
        try {
            Apartamento a = disponibles.get(posicionElegida(listaDisponibles, "un apartamento"));
            LocalDate entrada = leerFecha(dpEntrada, "la fecha de entrada");
            LocalDate salida = leerFecha(dpSalida, "la fecha de salida");
            String texto = empresa.detalleCotizacion(a, entrada, salida, armarOcupantes());
            if (nuevaEsPortal) {
                int total = empresa.cotizar(a, entrada, salida, armarOcupantes());
                texto += "\nAnticipo del 30 %: " + Folio.dinero(total * 30 / 100);
            }
            txtCotizacion.setText(texto);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void crearReservaNueva() {
        try {
            Apartamento a = disponibles.get(posicionElegida(listaDisponibles, "un apartamento"));
            LocalDate entrada = leerFecha(dpEntrada, "la fecha de entrada");
            LocalDate salida = leerFecha(dpSalida, "la fecha de salida");
            LinkedList<Ocupante> grupo = armarOcupantes();
            Huesped titular = titularElegido();

            String canal = "PORTAL";
            if (!nuevaEsPortal) {
                canal = cmbCanal.getValue();
            }

            if (canal.equals("PORTAL")) {
                Reserva r = empresa.crearReservaPortal(titular, a, entrada, salida, grupo);
                guardar();
                mensaje("Reserva " + r.getCodigo() + " creada.\nValor de la estancia: " + Folio.dinero(r.getValorEstancia())
                        + "\nQueda Pendiente: paga el anticipo de " + Folio.dinero(r.calcularAnticipo())
                        + " en \"Mis reservas\" para confirmarla (si no, se cancela en 24 horas).");
            } else if (canal.equals("DIRECTA")) {
                Reserva r = empresa.crearReservaDirecta(titular, a, entrada, salida, grupo);
                guardar();
                mensaje("Reserva " + r.getCodigo() + " creada.\nValor de la estancia: " + Folio.dinero(r.getValorEstancia())
                        + "\nQueda Pendiente hasta que se confirme.");
            } else {
                String codigoExterno = leerTexto(txtCodigoExterno, "Código externo");
                ReservaExterna r = new ReservaExterna(empresa.generarCodigoReserva(), entrada, salida, a, titular, grupo, codigoExterno);
                boolean guardada = empresa.registrarReservaExterna(r);
                guardar();
                if (guardada) {
                    mensaje("Reserva externa " + r.getCodigo() + " registrada.\nValor de la estancia: " + Folio.dinero(r.getValorEstancia())
                            + "\nComisión a la plataforma (15 %): " + Folio.dinero(r.calcularComision()));
                } else {
                    mensaje("La reserva externa chocó con una reserva vigente o un bloqueo: se rechazó y quedó registrada como conflicto de canal.");
                }
            }
            limpiarNuevaReserva();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void limpiarNuevaReserva() {
        ocupantes = new LinkedList<>();
        disponibles = new LinkedList<>();
        listaOcupantes.getItems().clear();
        listaDisponibles.getItems().clear();
        txtCotizacion.clear();
        dpEntrada.setValue(null);
        dpSalida.setValue(null);
        if (txtCodigoExterno != null) {
            txtCodigoExterno.clear();
        }
    }

    // =====================================================================
    // RESERVAS: LISTA, PAGOS, CARGOS Y FOLIO
    // =====================================================================

    private Node crearPestanaReservas(boolean esRecepcion) {
        listaReservas = new ListView<>();
        listaReservas.setPrefHeight(230);
        txtMonto = new TextField();
        txtMonto.setPromptText("Valor del pago");
        cmbMedio = new ComboBox<>();
        cmbMedio.getItems().addAll(MedioPago.values());
        cmbMedio.setValue(MedioPago.EFECTIVO);
        txtFolio = new TextArea();
        txtFolio.setEditable(false);
        txtFolio.setPrefRowCount(11);

        Button btnPago = new Button(esRecepcion ? "Registrar pago" : "Pagar anticipo / abonar");
        btnPago.setOnAction(e -> registrarPago());
        Button btnCancelar = new Button("Cancelar reserva");
        btnCancelar.setOnAction(e -> cancelarReserva());
        Button btnFolio = new Button("Ver folio");
        btnFolio.setOnAction(e -> verFolio());
        Button btnActualizar = new Button("Actualizar lista");
        btnActualizar.setOnAction(e -> actualizarListas());

        VBox caja = nuevaCaja();
        if (esRecepcion) {
            caja.getChildren().add(new Label("RESERVAS DEL ALOJAMIENTO"));
        } else {
            caja.getChildren().add(new Label("MIS RESERVAS"));
        }
        caja.getChildren().addAll(listaReservas, btnActualizar);

        if (esRecepcion) {
            Button btnConfirmar = new Button("Confirmar");
            btnConfirmar.setOnAction(e -> confirmarReserva());
            Button btnCheckIn = new Button("Check-in");
            btnCheckIn.setOnAction(e -> hacerCheckIn());
            Button btnCheckOut = new Button("Check-out");
            btnCheckOut.setOnAction(e -> hacerCheckOut());
            Button btnNoShow = new Button("Declarar no-show");
            btnNoShow.setOnAction(e -> declararNoShow());
            HBox acciones = new HBox(8);
            acciones.getChildren().addAll(btnConfirmar, btnCheckIn, btnCheckOut, btnNoShow, btnCancelar);
            caja.getChildren().add(acciones);
        }

        HBox pago = new HBox(8);
        pago.getChildren().addAll(new Label("Pago:"), txtMonto, cmbMedio, btnPago);
        caja.getChildren().add(pago);

        if (esRecepcion) {
            txtConceptoCargo = new TextField();
            txtConceptoCargo.setPromptText("Desayuno, lavandería...");
            txtMontoCargo = new TextField();
            txtMontoCargo.setPromptText("Valor del cargo");
            cmbTipoCargo = new ComboBox<>();
            cmbTipoCargo.getItems().add(TipoCargo.SERVICIO_ADICIONAL);
            cmbTipoCargo.getItems().add(TipoCargo.PENALIDAD);
            cmbTipoCargo.getItems().add(TipoCargo.AJUSTE);
            cmbTipoCargo.setValue(TipoCargo.SERVICIO_ADICIONAL);
            Button btnCargo = new Button("Registrar cargo");
            btnCargo.setOnAction(e -> registrarCargo());
            HBox cargo = new HBox(8);
            cargo.getChildren().addAll(new Label("Cargo adicional:"), txtConceptoCargo, txtMontoCargo, cmbTipoCargo, btnCargo);
            caja.getChildren().add(cargo);
        } else {
            caja.getChildren().add(btnCancelar);
        }
        caja.getChildren().addAll(btnFolio, txtFolio);
        return conScroll(caja);
    }

    private void llenarReservas() {
        if (listaReservas == null) {
            return;
        }
        if (usuarioActual instanceof Huesped) {
            reservasMostradas = empresa.historialHuesped((Huesped) usuarioActual);
        } else {
            reservasMostradas = empresa.getReservas();
        }
        LinkedList<String> textos = new LinkedList<>();
        for (Reserva r : reservasMostradas) {
            textos.add(describirReserva(r));
        }
        llenarLista(listaReservas, textos);
    }

    private String describirReserva(Reserva r) {
        return r.getCodigo() + " | " + r.getCanal() + " | " + r.getApartamento().getCodigo() + " | " + r.getTitular().getNombre()
                + " | " + r.getFechaEntrada() + " a " + r.getFechaSalida() + " | " + r.getEstado()
                + " | valor " + Folio.dinero(r.getValorEstancia()) + " | saldo " + Folio.dinero(r.getFolio().calcularSaldo());
    }

    private Reserva reservaElegida() {
        return reservasMostradas.get(posicionElegida(listaReservas, "una reserva"));
    }

    private void confirmarReserva() {
        try {
            Reserva r = reservaElegida();
            r.confirmar();
            guardar();
            actualizarListas();
            mensaje("La reserva " + r.getCodigo() + " quedó Confirmada.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void hacerCheckIn() {
        try {
            Reserva r = reservaElegida();
            r.hacerCheckIn();
            guardar();
            actualizarListas();
            mensaje("Check-in realizado. La reserva está En curso y el apartamento quedó Ocupado.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void hacerCheckOut() {
        try {
            Reserva r = reservaElegida();
            r.hacerCheckOut();
            guardar();
            actualizarListas();
            mensaje("Check-out realizado. La reserva quedó Finalizada y el apartamento Pendiente de preparación.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void declararNoShow() {
        try {
            Reserva r = reservaElegida();
            r.declararNoShow();
            guardar();
            actualizarListas();
            mensaje("La reserva quedó como No-show. Se retiene lo pagado según la política.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    //Cancelación: antes de cancelar se muestra cuánto se devolvería
    private void cancelarReserva() {
        try {
            Reserva r = reservaElegida();
            if (r.getEstado() != EstadoReserva.PENDIENTE && r.getEstado() != EstadoReserva.CONFIRMADA) {
                throw new IllegalStateException("Solo se puede cancelar una reserva Pendiente o Confirmada");
            }
            int devolucion = r.calcularDevolucion(LocalDateTime.now());
            boolean acepta = confirmar("Si cancelas ahora se devolverían " + Folio.dinero(devolucion)
                    + " (el sistema solo registra la devolución, no mueve dinero real).\n¿Cancelar la reserva " + r.getCodigo() + "?");
            if (acepta) {
                r.cancelar();
                guardar();
                actualizarListas();
                mensaje("Reserva cancelada. Devolución registrada: " + Folio.dinero(r.getDevolucion()));
            }
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void registrarPago() {
        try {
            Reserva r = reservaElegida();
            int monto = leerEntero(txtMonto, "Valor del pago");
            empresa.registrarPago(r, monto, cmbMedio.getValue());
            guardar();
            txtMonto.clear();
            actualizarListas();
            mensaje("Pago registrado. Saldo del folio: " + Folio.dinero(r.getFolio().calcularSaldo()) + "\nEstado de la reserva: " + r.getEstado());
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void registrarCargo() {
        try {
            Reserva r = reservaElegida();
            String concepto = leerTexto(txtConceptoCargo, "Concepto");
            int monto = leerEntero(txtMontoCargo, "Valor del cargo");
            empresa.registrarCargo(r, concepto, monto, cmbTipoCargo.getValue());
            guardar();
            txtConceptoCargo.clear();
            txtMontoCargo.clear();
            actualizarListas();
            mensaje("Cargo registrado. Saldo del folio: " + Folio.dinero(r.getFolio().calcularSaldo()));
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void verFolio() {
        try {
            Reserva r = reservaElegida();
            String texto = "Reserva " + r.getCodigo() + " - " + r.getTitular().getNombre() + "\n";
            texto += "Apartamento " + r.getApartamento() + " | Estado " + r.getEstado() + "\n\nCARGOS\n";
            for (Cargo c : r.getFolio().getCargos()) {
                texto += "  " + c.concepto() + " (" + c.tipo() + "): " + Folio.dinero(c.monto()) + "\n";
            }
            texto += "\nPAGOS\n";
            for (Pago p : r.getFolio().getPagos()) {
                texto += "  " + p.fecha() + " " + p.medioPago() + ": " + Folio.dinero(p.monto()) + "\n";
            }
            texto += "\nTotal cargos: " + Folio.dinero(r.getFolio().totalCargo());
            texto += "\nTotal pagos: " + Folio.dinero(r.getFolio().totalPago());
            texto += "\nSALDO: " + Folio.dinero(r.getFolio().calcularSaldo());
            txtFolio.setText(texto);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // =====================================================================
    // PANTALLA DE LA RECEPCIÓN
    // =====================================================================

    private void mostrarRecepcion() {
        TabPane pestanas = new TabPane();
        agregarPestana(pestanas, "Reservas", crearPestanaReservas(true));
        agregarPestana(pestanas, "Nueva reserva", crearPestanaNuevaReserva(false));
        agregarPestana(pestanas, "Apartamentos y novedades", crearPestanaAptosRecepcion());
        agregarPestana(pestanas, "Calendario", crearPestanaCalendario());
        agregarPestana(pestanas, "Canal externo", crearPestanaCanalExterno());
        agregarPestana(pestanas, "Huéspedes", crearPestanaHuespedes());
        mostrarConPestanas(pestanas, "Bienvenido, " + usuarioActual.getNombre() + " (Recepción)", "SGA - Recepción");
        actualizarListas();
    }

    private Node crearPestanaAptosRecepcion() {
        listaAptosRecepcion = new ListView<>();
        listaAptosRecepcion.setPrefHeight(150);
        cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll(EstadoOperativo.values());
        cmbEstado.setValue(EstadoOperativo.PREPARADO);
        txtMotivoFuera = new TextField();
        txtMotivoFuera.setPromptText("Motivo (obligatorio si pasa a Fuera de servicio)");
        cmbTipoNovedad = new ComboBox<>();
        cmbTipoNovedad.getItems().addAll(TipoNovedad.values());
        cmbTipoNovedad.setValue(TipoNovedad.DANIO);
        txtDescNovedad = new TextField();
        txtDescNovedad.setPromptText("Daño, faltante o situación reportada");
        txtNovedades = new TextArea();
        txtNovedades.setEditable(false);
        txtNovedades.setPrefRowCount(8);

        Button btnEstado = new Button("Cambiar estado operativo");
        btnEstado.setOnAction(e -> cambiarEstadoApto());
        Button btnNovedad = new Button("Registrar novedad");
        btnNovedad.setOnAction(e -> registrarNovedad());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("APARTAMENTOS (selecciona uno)"), listaAptosRecepcion,
                fila("Nuevo estado", cmbEstado), fila("Motivo", txtMotivoFuera), btnEstado,
                fila("Tipo de novedad", cmbTipoNovedad), fila("Descripción", txtDescNovedad), btnNovedad,
                new Label("NOVEDADES REGISTRADAS"), txtNovedades);
        return conScroll(caja);
    }

    private void llenarAptosRecepcion() {
        if (listaAptosRecepcion == null) {
            return;
        }
        aptosRecepcion = empresa.getApartamentos();
        LinkedList<String> textos = new LinkedList<>();
        for (Apartamento a : aptosRecepcion) {
            String texto = a + " | " + a.getEstadoOperativo();
            if (a.getEstadoOperativo() == EstadoOperativo.FUERA_DE_SERVICIO) {
                texto += " (" + a.getFueraDeServicio() + ")";
            }
            if (!a.isActivo()) {
                texto += " | INACTIVO";
            }
            textos.add(texto);
        }
        llenarLista(listaAptosRecepcion, textos);
    }

    private void llenarNovedades() {
        if (txtNovedades == null) {
            return;
        }
        String texto = "";
        for (Novedad n : empresa.getNovedades()) {
            texto += n.getFecha() + " | " + n.getApartamento().getCodigo() + " | " + n.getTipo() + " | " + n.getDescripcion();
            if (n.getRegistradaPor() != null) {
                texto += " | " + n.getRegistradaPor().getNombre();
            }
            texto += "\n";
        }
        txtNovedades.setText(texto);
    }

    private void cambiarEstadoApto() {
        try {
            Apartamento a = aptosRecepcion.get(posicionElegida(listaAptosRecepcion, "un apartamento"));
            EstadoOperativo nuevo = cmbEstado.getValue();
            if (nuevo == EstadoOperativo.FUERA_DE_SERVICIO) {
                //Al pasar a Fuera de servicio se debe registrar el motivo
                a.registrarMotivoFueraDeServicio(leerTexto(txtMotivoFuera, "Motivo"));
            } else {
                a.actualizarEstadoOperativo(nuevo);
            }
            guardar();
            txtMotivoFuera.clear();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void registrarNovedad() {
        try {
            Apartamento a = aptosRecepcion.get(posicionElegida(listaAptosRecepcion, "un apartamento"));
            Novedad n = new Novedad(txtDescNovedad.getText().trim(), LocalDate.now(), cmbTipoNovedad.getValue(), a, (Recepcionista) usuarioActual);
            empresa.registrarNovedad(n);
            guardar();
            txtDescNovedad.clear();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private Node crearPestanaCanalExterno() {
        txtCantidadSim = new TextField("10");
        txtConflictos = new TextArea();
        txtConflictos.setEditable(false);
        txtConflictos.setPrefRowCount(15);
        Button btnSimular = new Button("Simular reservas externas");
        btnSimular.setOnAction(e -> simularCanalExterno());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("SIMULADOR DE CANAL EXTERNO: genera reservas externas aleatorias para probar los conflictos de canal"),
                fila("Cantidad de reservas", txtCantidadSim), btnSimular,
                new Label("CONFLICTOS DE CANAL REGISTRADOS"), txtConflictos);
        return conScroll(caja);
    }

    private void simularCanalExterno() {
        try {
            int cantidad = leerEntero(txtCantidadSim, "Cantidad de reservas");
            if (cantidad <= 0 || cantidad > 500) {
                throw new IllegalArgumentException("La cantidad debe estar entre 1 y 500");
            }
            int reservasAntes = empresa.getReservas().size();
            SimuladorCanalExterno simulador = new SimuladorCanalExterno();
            int conflictos = simulador.simular(empresa, cantidad).size();
            int aceptadas = empresa.getReservas().size() - reservasAntes;
            guardar();
            actualizarListas();
            mensaje("Se enviaron " + cantidad + " reservas externas.\nAceptadas: " + aceptadas + "\nConflictos de canal: " + conflictos);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void llenarConflictos() {
        if (txtConflictos == null) {
            return;
        }
        String texto = "";
        for (ConflictoCanal c : empresa.getConflictoCanales()) {
            texto += c.getFechaRegistro() + " | código externo " + c.getCodigoExterno() + " | apartamento " + c.getCodigoApartamento()
                    + " | " + c.getFechaEntrada() + " a " + c.getFechaSalida() + " | " + c.getMotivo() + "\n";
        }
        txtConflictos.setText(texto);
    }

    private Node crearPestanaHuespedes() {
        listaHuespedes = new ListView<>();
        listaHuespedes.setPrefHeight(150);
        txtHistorial = new TextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setPrefRowCount(8);
        Button btnHistorial = new Button("Ver historial de estancias del huésped seleccionado");
        btnHistorial.setOnAction(e -> verHistorial());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("HUÉSPEDES REGISTRADOS"), listaHuespedes, btnHistorial, txtHistorial);
        caja.getChildren().add(crearFormularioHuesped());
        return conScroll(caja);
    }

    private void llenarHuespedes() {
        if (listaHuespedes == null) {
            return;
        }
        huespedesLista = empresa.getHuespedes();
        LinkedList<String> textos = new LinkedList<>();
        for (Huesped h : huespedesLista) {
            textos.add(h.getCedula() + " | " + h.getNombre() + " | " + h.getTelefono() + " | " + h.getCorreo());
        }
        llenarLista(listaHuespedes, textos);
    }

    private void verHistorial() {
        try {
            Huesped h = huespedesLista.get(posicionElegida(listaHuespedes, "un huésped"));
            String texto = "Historial de " + h.getNombre() + "\n\n";
            LinkedList<Reserva> historial = empresa.historialHuesped(h);
            if (historial.isEmpty()) {
                texto += "Todavía no tiene reservas.";
            }
            for (Reserva r : historial) {
                texto += describirReserva(r) + "\n";
            }
            txtHistorial.setText(texto);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // =====================================================================
    // CALENDARIO DE OCUPACIÓN (lo ven la recepción y el administrador)
    // =====================================================================

    private Node crearPestanaCalendario() {
        cmbAptoCalendario = new ComboBox<>();
        txtAnio = new TextField(String.valueOf(LocalDate.now().getYear()));
        txtMes = new TextField(String.valueOf(LocalDate.now().getMonthValue()));
        txtCalendario = new TextArea();
        txtCalendario.setEditable(false);
        txtCalendario.setPrefRowCount(32);
        Button btnMostrar = new Button("Mostrar calendario del mes");
        btnMostrar.setOnAction(e -> mostrarCalendario());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("CALENDARIO DE OCUPACIÓN: por apartamento, las noches libres, ocupadas y bloqueadas"),
                fila("Apartamento", cmbAptoCalendario), fila("Año", txtAnio), fila("Mes (1 a 12)", txtMes),
                btnMostrar, txtCalendario);
        return conScroll(caja);
    }

    private void llenarAptosCalendario() {
        if (cmbAptoCalendario == null) {
            return;
        }
        aptosCalendario = empresa.getApartamentos();
        LinkedList<String> textos = new LinkedList<>();
        for (Apartamento a : aptosCalendario) {
            textos.add(a.toString());
        }
        llenarCombo(cmbAptoCalendario, textos);
    }

    private void mostrarCalendario() {
        try {
            Apartamento a = aptosCalendario.get(posicionCombo(cmbAptoCalendario, "un apartamento"));
            int anio = leerEntero(txtAnio, "Año");
            int mes = leerEntero(txtMes, "Mes");
            if (mes < 1 || mes > 12) {
                throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
            }
            CalendarioOcupacion calendario = new CalendarioOcupacion(empresa);
            LinkedList<EstadoNoche> estados = calendario.estadosDelMes(a, YearMonth.of(anio, mes));
            String texto = a + " - " + mes + "/" + anio + "\n\n";
            for (int dia = 1; dia <= estados.size(); dia++) {
                texto += "Día " + dia + ": " + estados.get(dia - 1) + "\n";
            }
            txtCalendario.setText(texto);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // =====================================================================
    // PANTALLA DEL ADMINISTRADOR
    // =====================================================================

    private void mostrarAdministrador() {
        TabPane pestanas = new TabPane();
        agregarPestana(pestanas, "Apartamentos", crearPestanaAptosAdmin());
        agregarPestana(pestanas, "Temporadas", crearPestanaTemporadas());
        agregarPestana(pestanas, "Política de cancelación", crearPestanaPolitica());
        agregarPestana(pestanas, "Bloqueos", crearPestanaBloqueos());
        agregarPestana(pestanas, "Empleados", crearPestanaEmpleados());
        agregarPestana(pestanas, "Calendario", crearPestanaCalendario());
        agregarPestana(pestanas, "Reportes", crearPestanaReportes());
        mostrarConPestanas(pestanas, "Bienvenido, " + usuarioActual.getNombre() + " (Administrador)", "SGA - Administración");
        actualizarListas();
    }

    //---------------------------------------------------------------- Apartamentos
    private Node crearPestanaAptosAdmin() {
        listaAptosAdmin = new ListView<>();
        listaAptosAdmin.setPrefHeight(170);
        txtAptCodigo = new TextField();
        txtAptNombre = new TextField();
        txtAptDormitorios = new TextField();
        txtAptCapacidad = new TextField();
        txtAptCaracteristicas = new TextField();
        txtAptCaracteristicas.setPromptText("Balcón, Jacuzzi, Chimenea");
        lblAptFoto = new Label("Sin fotografía nueva");
        fotoApto = null;
        txtTarifaBaja = new TextField();
        txtTarifaMedia = new TextField();
        txtTarifaAlta = new TextField();

        Button btnFoto = new Button("Elegir fotografía");
        btnFoto.setOnAction(e -> elegirFotoApto());
        Button btnCrear = new Button("Crear apartamento nuevo");
        btnCrear.setOnAction(e -> crearApartamento());
        Button btnCargar = new Button("Cargar datos del seleccionado");
        btnCargar.setOnAction(e -> cargarApartamento());
        Button btnModificar = new Button("Guardar cambios del seleccionado");
        btnModificar.setOnAction(e -> modificarApartamento());
        Button btnEliminar = new Button("Eliminar (lógico)");
        btnEliminar.setOnAction(e -> eliminarApartamento());
        Button btnReactivar = new Button("Reactivar");
        btnReactivar.setOnAction(e -> reactivarApartamento());
        Button btnTarifas = new Button("Guardar tarifas del seleccionado");
        btnTarifas.setOnAction(e -> guardarTarifas());

        HBox acciones = new HBox(8);
        acciones.getChildren().addAll(btnCargar, btnModificar, btnEliminar, btnReactivar);

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("APARTAMENTOS (selecciona uno para modificarlo)"), listaAptosAdmin, acciones,
                new Label("DATOS DEL APARTAMENTO (para crear uno nuevo o modificar el seleccionado)"),
                fila("Código", txtAptCodigo), fila("Nombre", txtAptNombre),
                fila("Dormitorios (solo descriptivo)", txtAptDormitorios), fila("Capacidad máxima", txtAptCapacidad),
                fila("Características", txtAptCaracteristicas), btnFoto, lblAptFoto, btnCrear,
                new Label("TARIFAS POR PERSONA POR NOCHE (del apartamento seleccionado)"),
                fila("Temporada baja", txtTarifaBaja), fila("Temporada media", txtTarifaMedia),
                fila("Temporada alta", txtTarifaAlta), btnTarifas);
        return conScroll(caja);
    }

    private void llenarAptosAdmin() {
        if (listaAptosAdmin == null) {
            return;
        }
        aptosAdmin = empresa.getApartamentos();
        LinkedList<String> textos = new LinkedList<>();
        for (Apartamento a : aptosAdmin) {
            String texto = a + " | dorm. " + a.getNumeroDormitorios() + " | cap. " + a.getCapacidadMaxima()
                    + " | tarifas B/M/A: " + Folio.dinero(a.getTarifa(TemporadaAlojamiento.BAJA)) + " / "
                    + Folio.dinero(a.getTarifa(TemporadaAlojamiento.MEDIA)) + " / " + Folio.dinero(a.getTarifa(TemporadaAlojamiento.ALTA));
            if (!a.isActivo()) {
                texto += " | INACTIVO";
            }
            textos.add(texto);
        }
        llenarLista(listaAptosAdmin, textos);
    }

    private void elegirFotoApto() {
        File elegida = elegirImagen();
        if (elegida != null) {
            fotoApto = elegida;
            lblAptFoto.setText(elegida.getName());
        }
    }

    private LinkedList<String> leerCaracteristicas() {
        LinkedList<String> lista = new LinkedList<>();
        String[] partes = txtAptCaracteristicas.getText().split(",");
        for (int i = 0; i < partes.length; i++) {
            if (!partes[i].isBlank()) {
                lista.add(partes[i].trim());
            }
        }
        return lista;
    }

    //Guarda la fotografía elegida (si hay) en la lista de fotos del apartamento
    private void guardarFotoApto(Apartamento a) {
        if (fotoApto == null) {
            return;
        }
        String nombreArchivo = a.getCodigo() + "_" + (a.getRutasFotos().size() + 1) + extension(fotoApto);
        ManejoImagen manejo = new ManejoImagen("fotos/apartamentos");
        String ruta = manejo.guardarImagen(fotoApto, nombreArchivo);
        if (ruta == null) {
            mensaje("El apartamento quedó guardado, pero la fotografía no es válida (usa png, jpg o jpeg).");
        } else {
            a.getRutasFotos().add(ruta);
        }
        fotoApto = null;
        lblAptFoto.setText("Sin fotografía nueva");
    }

    private void crearApartamento() {
        try {
            Apartamento a = new Apartamento(leerTexto(txtAptCodigo, "Código"), leerTexto(txtAptNombre, "Nombre"),
                    leerEntero(txtAptDormitorios, "Dormitorios"), leerEntero(txtAptCapacidad, "Capacidad máxima"),
                    leerCaracteristicas(), new LinkedList<String>());
            empresa.registrarApartamento(a);
            guardarFotoApto(a);
            guardar();
            actualizarListas();
            mensaje("Apartamento creado. Ahora define sus tarifas (selecciónalo en la lista y llena las tarifas).");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void cargarApartamento() {
        try {
            Apartamento a = aptosAdmin.get(posicionElegida(listaAptosAdmin, "un apartamento"));
            txtAptCodigo.setText(a.getCodigo());
            txtAptNombre.setText(a.getNombre());
            txtAptDormitorios.setText(String.valueOf(a.getNumeroDormitorios()));
            txtAptCapacidad.setText(String.valueOf(a.getCapacidadMaxima()));
            String caracteristicas = "";
            for (String c : a.getCaracteristicas()) {
                if (!caracteristicas.isEmpty()) {
                    caracteristicas += ", ";
                }
                caracteristicas += c;
            }
            txtAptCaracteristicas.setText(caracteristicas);
            txtTarifaBaja.setText(String.valueOf(a.getTarifa(TemporadaAlojamiento.BAJA)));
            txtTarifaMedia.setText(String.valueOf(a.getTarifa(TemporadaAlojamiento.MEDIA)));
            txtTarifaAlta.setText(String.valueOf(a.getTarifa(TemporadaAlojamiento.ALTA)));
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void modificarApartamento() {
        try {
            Apartamento a = aptosAdmin.get(posicionElegida(listaAptosAdmin, "un apartamento"));
            String nombre = leerTexto(txtAptNombre, "Nombre");
            int dormitorios = leerEntero(txtAptDormitorios, "Dormitorios");
            int capacidad = leerEntero(txtAptCapacidad, "Capacidad máxima");
            if (capacidad <= 0 || dormitorios < 0) {
                throw new IllegalArgumentException("La capacidad debe ser mayor que cero y los dormitorios no pueden ser negativos");
            }
            a.setNombre(nombre);
            a.setNumeroDormitorios(dormitorios);
            a.setCapacidadMaxima(capacidad);
            a.setCaracteristicas(leerCaracteristicas());
            guardarFotoApto(a);
            guardar();
            actualizarListas();
            mensaje("Apartamento modificado (el código no se cambia).");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void eliminarApartamento() {
        try {
            Apartamento a = aptosAdmin.get(posicionElegida(listaAptosAdmin, "un apartamento"));
            if (confirmar("El apartamento " + a.getCodigo() + " dejará de aparecer para la venta (eliminación lógica). ¿Continuar?")) {
                empresa.eliminarApartamento(a.getCodigo());
                guardar();
                actualizarListas();
            }
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void reactivarApartamento() {
        try {
            aptosAdmin.get(posicionElegida(listaAptosAdmin, "un apartamento")).setActivo(true);
            guardar();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void guardarTarifas() {
        try {
            Apartamento a = aptosAdmin.get(posicionElegida(listaAptosAdmin, "un apartamento"));
            empresa.definirTarifa(a, TemporadaAlojamiento.BAJA, leerEntero(txtTarifaBaja, "Tarifa temporada baja"));
            empresa.definirTarifa(a, TemporadaAlojamiento.MEDIA, leerEntero(txtTarifaMedia, "Tarifa temporada media"));
            empresa.definirTarifa(a, TemporadaAlojamiento.ALTA, leerEntero(txtTarifaAlta, "Tarifa temporada alta"));
            guardar();
            actualizarListas();
            mensaje("Tarifas guardadas. Las reservas que ya existen conservan su valor.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    //---------------------------------------------------------------- Temporadas
    private Node crearPestanaTemporadas() {
        listaTemporadas = new ListView<>();
        listaTemporadas.setPrefHeight(250);
        dpTempInicio = new DatePicker();
        dpTempFin = new DatePicker();
        cmbTempTipo = new ComboBox<>();
        cmbTempTipo.getItems().add(TemporadaAlojamiento.MEDIA);
        cmbTempTipo.getItems().add(TemporadaAlojamiento.ALTA);
        cmbTempTipo.setValue(TemporadaAlojamiento.ALTA);
        Button btnAgregar = new Button("Agregar temporada");
        btnAgregar.setOnAction(e -> agregarTemporada());
        Button btnEliminar = new Button("Eliminar la seleccionada");
        btnEliminar.setOnAction(e -> eliminarTemporada());

        Label ayuda = new Label("Baja (base): todas las fechas que no pertenezcan a otra temporada. "
                + "Media: puentes festivos y periodos definidos por la administración. "
                + "Alta: diciembre-enero, Semana Santa y mitad de año.");
        ayuda.setWrapText(true);

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("TEMPORADAS"), ayuda, listaTemporadas, btnEliminar,
                new Label("NUEVA TEMPORADA"), fila("Inicio", dpTempInicio), fila("Fin (incluido)", dpTempFin),
                fila("Tipo", cmbTempTipo), btnAgregar);
        return conScroll(caja);
    }

    private void llenarTemporadas() {
        if (listaTemporadas == null) {
            return;
        }
        temporadasLista = empresa.getTemporadas();
        LinkedList<String> textos = new LinkedList<>();
        for (Temporada t : temporadasLista) {
            textos.add(t.getFechaInicio() + " a " + t.getFechaFin() + " - " + t.getTipo());
        }
        llenarLista(listaTemporadas, textos);
    }

    private void agregarTemporada() {
        try {
            Temporada t = new Temporada(leerFecha(dpTempInicio, "la fecha de inicio"), leerFecha(dpTempFin, "la fecha de fin"), cmbTempTipo.getValue());
            empresa.registrarTemporada(t);
            guardar();
            dpTempInicio.setValue(null);
            dpTempFin.setValue(null);
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void eliminarTemporada() {
        try {
            empresa.eliminarTemporada(temporadasLista.get(posicionElegida(listaTemporadas, "una temporada")));
            guardar();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    //---------------------------------------------------------------- Política de cancelación
    private Node crearPestanaPolitica() {
        txtPolDevolucion = new TextField();
        txtPolRetencion = new TextField();
        txtPolPorcentaje = new TextField();
        lblPolResumen = new Label();
        lblPolResumen.setWrapText(true);
        Button btnGuardar = new Button("Guardar política");
        btnGuardar.setOnAction(e -> guardarPolitica());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("POLÍTICA DE CANCELACIÓN (solo afecta las reservas que se creen después)"),
                fila("Días de antelación para devolución total (más de)", txtPolDevolucion),
                fila("Días mínimos para retención parcial", txtPolRetencion),
                fila("Porcentaje retenido del valor (%)", txtPolPorcentaje), btnGuardar, lblPolResumen);
        return conScroll(caja);
    }

    private void llenarPolitica() {
        if (txtPolDevolucion == null) {
            return;
        }
        PoliticaCancelacion p = empresa.getPoliticaCancelacion();
        txtPolDevolucion.setText(String.valueOf(p.getDiasDevolucion()));
        txtPolRetencion.setText(String.valueOf(p.getDiasRetencion()));
        txtPolPorcentaje.setText(String.valueOf(p.getPorcentajeRetencion()));
        lblPolResumen.setText("Con más de " + p.getDiasDevolucion() + " días de antelación se devuelve todo lo pagado; entre "
                + p.getDiasRetencion() + " y " + p.getDiasDevolucion() + " días se retiene el " + p.getPorcentajeRetencion()
                + " % del valor de la estancia; con menos de " + p.getDiasRetencion() + " días (o en caso de no-show) se retiene el 100 %.");
    }

    private void guardarPolitica() {
        try {
            empresa.configurarPoliticaCancelacion(leerEntero(txtPolDevolucion, "Días de devolución total"),
                    leerEntero(txtPolRetencion, "Días de retención parcial"), leerDecimal(txtPolPorcentaje, "Porcentaje retenido"));
            guardar();
            llenarPolitica();
            mensaje("La política quedó guardada. Las reservas ya creadas conservan la política con la que se crearon.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    //---------------------------------------------------------------- Bloqueos
    private Node crearPestanaBloqueos() {
        listaBloqueos = new ListView<>();
        listaBloqueos.setPrefHeight(200);
        cmbBloqApto = new ComboBox<>();
        dpBloqInicio = new DatePicker();
        dpBloqFin = new DatePicker();
        txtBloqMotivo = new TextField();
        Button btnRegistrar = new Button("Registrar bloqueo");
        btnRegistrar.setOnAction(e -> registrarBloqueo());
        Button btnEliminar = new Button("Eliminar el bloqueo seleccionado");
        btnEliminar.setOnAction(e -> eliminarBloqueo());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("BLOQUEOS: fechas en que un apartamento no se puede vender"), listaBloqueos, btnEliminar,
                new Label("NUEVO BLOQUEO"), fila("Apartamento", cmbBloqApto), fila("Inicio", dpBloqInicio),
                fila("Fin (incluido)", dpBloqFin), fila("Motivo", txtBloqMotivo), btnRegistrar);
        return conScroll(caja);
    }

    private void llenarBloqueos() {
        if (listaBloqueos == null) {
            return;
        }
        bloqueosLista = empresa.getBloqueos();
        LinkedList<String> textos = new LinkedList<>();
        for (Bloqueo b : bloqueosLista) {
            textos.add(b.getApartamento().getCodigo() + " | " + b.getFechaInicio() + " a " + b.getFechaFin() + " | " + b.getMotivo());
        }
        llenarLista(listaBloqueos, textos);

        aptosBloqueo = new LinkedList<>();
        LinkedList<String> nombres = new LinkedList<>();
        for (Apartamento a : empresa.getApartamentos()) {
            if (a.isActivo()) {
                aptosBloqueo.add(a);
                nombres.add(a.toString());
            }
        }
        llenarCombo(cmbBloqApto, nombres);
    }

    private void registrarBloqueo() {
        try {
            Apartamento a = aptosBloqueo.get(posicionCombo(cmbBloqApto, "el apartamento"));
            Bloqueo b = new Bloqueo(a, leerFecha(dpBloqInicio, "la fecha de inicio"), leerFecha(dpBloqFin, "la fecha de fin"), leerTexto(txtBloqMotivo, "Motivo"));
            empresa.registrarBloqueo(b);
            guardar();
            dpBloqInicio.setValue(null);
            dpBloqFin.setValue(null);
            txtBloqMotivo.clear();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void eliminarBloqueo() {
        try {
            empresa.eliminarBloqueo(bloqueosLista.get(posicionElegida(listaBloqueos, "un bloqueo")));
            guardar();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    //---------------------------------------------------------------- Empleados (recepcionistas)
    private Node crearPestanaEmpleados() {
        listaEmpleados = new ListView<>();
        listaEmpleados.setPrefHeight(180);
        txtEmpNombre = new TextField();
        txtEmpCedula = new TextField();
        txtEmpTelefono = new TextField();
        txtEmpCorreo = new TextField();
        txtEmpUsuario = new TextField();
        txtEmpContrasenia = new PasswordField();
        Button btnContratar = new Button("Contratar con estos datos");
        btnContratar.setOnAction(e -> contratarEmpleado());
        Button btnCargar = new Button("Cargar datos del seleccionado");
        btnCargar.setOnAction(e -> cargarEmpleado());
        Button btnModificar = new Button("Guardar cambios del seleccionado");
        btnModificar.setOnAction(e -> modificarEmpleado());
        Button btnDesvincular = new Button("Desvincular al seleccionado");
        btnDesvincular.setOnAction(e -> desvincularEmpleado());

        HBox acciones = new HBox(8);
        acciones.getChildren().addAll(btnCargar, btnModificar, btnDesvincular);

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("RECEPCIONISTAS"), listaEmpleados, acciones,
                new Label("DATOS (para contratar a alguien nuevo o editar al seleccionado; la cédula y el usuario no se cambian)"),
                fila("Nombre completo", txtEmpNombre), fila("Documento", txtEmpCedula), fila("Teléfono", txtEmpTelefono),
                fila("Correo", txtEmpCorreo), fila("Usuario", txtEmpUsuario), fila("Contraseña", txtEmpContrasenia), btnContratar);
        return conScroll(caja);
    }

    private void llenarEmpleados() {
        if (listaEmpleados == null) {
            return;
        }
        empleadosLista = empresa.getRecepcionistas();
        LinkedList<String> textos = new LinkedList<>();
        for (Recepcionista r : empleadosLista) {
            String estado = "Activo";
            if (!r.getActivo()) {
                estado = "Desvinculado";
            }
            textos.add(r.getNombre() + " | " + r.getCedula() + " | usuario " + r.getUsuario() + " | " + estado);
        }
        llenarLista(listaEmpleados, textos);
    }

    private void contratarEmpleado() {
        try {
            Recepcionista r = new Recepcionista(leerTexto(txtEmpNombre, "Nombre"), leerTexto(txtEmpCedula, "Documento"),
                    leerTexto(txtEmpTelefono, "Teléfono"), leerTexto(txtEmpCorreo, "Correo"), leerTexto(txtEmpUsuario, "Usuario"),
                    leerTexto(txtEmpContrasenia, "Contraseña"), LocalDate.now(), true);
            empresa.registrarRecepcionista(r);
            guardar();
            limpiarFormularioEmpleado();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void cargarEmpleado() {
        try {
            Recepcionista r = empleadosLista.get(posicionElegida(listaEmpleados, "un recepcionista"));
            txtEmpNombre.setText(r.getNombre());
            txtEmpCedula.setText(r.getCedula());
            txtEmpTelefono.setText(r.getTelefono());
            txtEmpCorreo.setText(r.getCorreo());
            txtEmpUsuario.setText(r.getUsuario());
            txtEmpContrasenia.clear();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void modificarEmpleado() {
        try {
            Recepcionista r = empleadosLista.get(posicionElegida(listaEmpleados, "un recepcionista"));
            String nombre = leerTexto(txtEmpNombre, "Nombre");
            String telefono = leerTexto(txtEmpTelefono, "Teléfono");
            String correo = leerTexto(txtEmpCorreo, "Correo");
            r.setNombre(nombre);
            r.setTelefono(telefono);
            r.setCorreo(correo);
            if (!txtEmpContrasenia.getText().isBlank()) {
                r.setContrasenia(txtEmpContrasenia.getText());
            }
            guardar();
            limpiarFormularioEmpleado();
            actualizarListas();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void desvincularEmpleado() {
        try {
            Recepcionista r = empleadosLista.get(posicionElegida(listaEmpleados, "un recepcionista"));
            if (confirmar("¿Desvincular a " + r.getNombre() + "? Ya no podrá iniciar sesión.")) {
                empresa.desvincularRecepcionista(r.getCedula());
                guardar();
                actualizarListas();
            }
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void limpiarFormularioEmpleado() {
        txtEmpNombre.clear();
        txtEmpCedula.clear();
        txtEmpTelefono.clear();
        txtEmpCorreo.clear();
        txtEmpUsuario.clear();
        txtEmpContrasenia.clear();
    }

    //---------------------------------------------------------------- Reportes
    private Node crearPestanaReportes() {
        cmbReporte = new ComboBox<>();
        cmbReporte.getItems().add("Ocupación por apartamento");
        cmbReporte.getItems().add("Ingresos por periodo");
        cmbReporte.getItems().add("Reservas por canal (incluye canceladas y no-show)");
        cmbReporte.getItems().add("Saldos pendientes");
        cmbReporte.getItems().add("Apartamentos con más novedades");
        cmbReporte.getItems().add("Ocupación por temporada");
        cmbReporte.getItems().add("Comportamiento de las cancelaciones");
        cmbReporte.setValue("Ocupación por apartamento");
        LocalDate hoy = LocalDate.now();
        dpRepDesde = new DatePicker();
        dpRepDesde.setValue(hoy.withDayOfMonth(1));
        dpRepHasta = new DatePicker();
        dpRepHasta.setValue(hoy.withDayOfMonth(hoy.lengthOfMonth()));
        txtReporte = new TextArea();
        txtReporte.setEditable(false);
        txtReporte.setPrefRowCount(22);
        Button btnGenerar = new Button("Generar reporte");
        btnGenerar.setOnAction(e -> generarReporte());

        VBox caja = nuevaCaja();
        caja.getChildren().addAll(new Label("REPORTES Y ESTADÍSTICAS (las fechas se usan en los reportes por periodo)"),
                fila("Reporte", cmbReporte), fila("Desde", dpRepDesde), fila("Hasta", dpRepHasta), btnGenerar, txtReporte);
        return conScroll(caja);
    }

    private void generarReporte() {
        try {
            EstadisticaAvanzada estadisticas = new EstadisticaAvanzada(empresa);
            String tipo = cmbReporte.getValue();
            String texto;
            if (tipo.equals("Ocupación por apartamento")) {
                texto = estadisticas.ocupacionPorApartamento(leerFecha(dpRepDesde, "la fecha inicial"), leerFecha(dpRepHasta, "la fecha final"));
            } else if (tipo.equals("Ingresos por periodo")) {
                texto = estadisticas.ingresosPorPeriodo(leerFecha(dpRepDesde, "la fecha inicial"), leerFecha(dpRepHasta, "la fecha final"));
            } else if (tipo.equals("Saldos pendientes")) {
                texto = estadisticas.saldosPendientes();
            } else if (tipo.equals("Apartamentos con más novedades")) {
                texto = estadisticas.apartamentosConMasNovedades();
            } else if (tipo.equals("Ocupación por temporada")) {
                texto = estadisticas.ocupacionPorTemporada(leerFecha(dpRepDesde, "la fecha inicial"), leerFecha(dpRepHasta, "la fecha final"));
            } else if (tipo.equals("Comportamiento de las cancelaciones")) {
                texto = estadisticas.comportamientoCancelaciones();
            } else {
                texto = estadisticas.reservasPorCanal();
            }
            txtReporte.setText(texto);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // =====================================================================
    // ACTUALIZAR LAS LISTAS DE LA PANTALLA
    // =====================================================================

    //Se llama cada vez que se cambia de pestaña y después de cada acción
    private void actualizarListas() {
        if (usuarioActual instanceof Huesped) {
            llenarReservas();
        } else if (usuarioActual instanceof Recepcionista) {
            llenarReservas();
            llenarTitulares();
            llenarAptosRecepcion();
            llenarNovedades();
            llenarConflictos();
            llenarHuespedes();
            llenarAptosCalendario();
        } else if (usuarioActual instanceof AdministradorAlojamiento) {
            llenarAptosAdmin();
            llenarTemporadas();
            llenarPolitica();
            llenarBloqueos();
            llenarEmpleados();
            llenarAptosCalendario();
        }
    }
}
