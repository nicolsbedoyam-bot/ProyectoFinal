package co.edu.uniquindio.ProjectoFinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaTest {

    private static final Logger LOG = Logger.getLogger(EmpresaTest.class.getName());

    private Empresa empresa;
    private Apartamento apartamento;
    private Huesped titular;
    private LocalDate entrada;
    private LocalDate salida;

    @BeforeEach
    void preparar() {
        empresa = new Empresa("900", "Aparta-Suites UQ", "Salento");
        apartamento = new Apartamento("A1", "Suite 1", 2, 4, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(apartamento);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 100000);
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.ALTA, 200000);
        titular = new Huesped("Ana", "1", "300", "ana@correo.com", "ana", "clave", LocalDate.of(1990, 1, 1), null);
        empresa.registrarHuesped(titular);
        entrada = LocalDate.now().plusDays(20);
        salida = entrada.plusDays(3);
    }

    //Crea una lista con la cantidad de adultos indicada
    private LinkedList<Ocupante> adultos(int cantidad) {
        LinkedList<Ocupante> lista = new LinkedList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(new Ocupante("Adulto " + i, LocalDate.of(1990, 1, 1)));
        }
        return lista;
    }

    // ---------------------------------------------------------------- Datos de la empresa

    @DisplayName("La empresa guarda sus datos")
    @Test
    void testDatos() {
        LOG.info("Iniciando prueba de datos de la empresa...");

        assertEquals("900", empresa.getNit());
        assertEquals("Aparta-Suites UQ", empresa.getNombre());
        assertEquals("Salento", empresa.getUbicacion());

        empresa.setNombre("Otro nombre");
        assertEquals("Otro nombre", empresa.getNombre());

        LOG.info("Finalizando prueba de datos de la empresa...");
    }

    @DisplayName("Las listas que devuelve la empresa son copias")
    @Test
    void testListasSonCopias() {
        LOG.info("Iniciando prueba de copias de listas...");

        empresa.getApartamentos().clear();
        empresa.getRoles().clear();

        assertEquals(1, empresa.getApartamentos().size());
        assertEquals(1, empresa.getRoles().size());

        LOG.info("Finalizando prueba de copias de listas...");
    }

    // ---------------------------------------------------------------- Apartamentos y tarifas

    @DisplayName("No se registran dos apartamentos con el mismo código")
    @Test
    void testApartamentoRepetido() {
        LOG.info("Iniciando prueba de apartamento repetido...");

        Apartamento repetido = new Apartamento("A1", "Otro", 1, 2, new LinkedList<>(), new LinkedList<>());

        try {
            empresa.registrarApartamento(repetido);
            fail("El código repetido debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getApartamentos().size());
        }

        LOG.info("Finalizando prueba de apartamento repetido...");
    }

    @DisplayName("La capacidad máxima debe ser mayor que cero")
    @Test
    void testApartamentoSinCapacidad() {
        LOG.info("Iniciando prueba de capacidad inválida...");

        Apartamento sinCapacidad = new Apartamento("A2", "Sin capacidad", 1, 0, new LinkedList<>(), new LinkedList<>());

        try {
            empresa.registrarApartamento(sinCapacidad);
            fail("La capacidad cero debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertNull(empresa.buscarApartamento("A2"));
        }

        LOG.info("Finalizando prueba de capacidad inválida...");
    }

    @DisplayName("Se busca un apartamento por su código")
    @Test
    void testBuscarApartamento() {
        LOG.info("Iniciando prueba de búsqueda de apartamento...");

        assertEquals(apartamento, empresa.buscarApartamento("A1"));
        assertNull(empresa.buscarApartamento("Z9"));

        LOG.info("Finalizando prueba de búsqueda de apartamento...");
    }

    @DisplayName("Definir una tarifa actualiza la que ya existía")
    @Test
    void testDefinirTarifa() {
        LOG.info("Iniciando prueba de tarifas...");

        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 120000);

        assertEquals(120000, apartamento.getTarifa(TemporadaAlojamiento.BAJA));
        assertEquals(2, apartamento.getTarifas().size());

        try {
            empresa.definirTarifa(apartamento, TemporadaAlojamiento.MEDIA, 0);
            fail("Una tarifa en cero debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(0, apartamento.getTarifa(TemporadaAlojamiento.MEDIA));
        }

        LOG.info("Finalizando prueba de tarifas...");
    }

    @DisplayName("Eliminación lógica: el apartamento queda inactivo y no se ofrece")
    @Test
    void testEliminarApartamento() {
        LOG.info("Iniciando prueba de eliminación lógica...");

        empresa.eliminarApartamento("A1");

        assertFalse(apartamento.isActivo());
        assertEquals(1, empresa.getApartamentos().size());
        assertTrue(empresa.buscarDisponibles(entrada, salida, 1).isEmpty());

        LOG.info("Finalizando prueba de eliminación lógica...");
    }

    @DisplayName("No se elimina un apartamento que tiene reservas activas")
    @Test
    void testEliminarConReservas() {
        LOG.info("Iniciando prueba de eliminar con reservas...");

        empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));

        try {
            empresa.eliminarApartamento("A1");
            fail("No se puede eliminar un apartamento con reservas activas");
        } catch (IllegalStateException e) {
            assertTrue(apartamento.isActivo());
        }

        LOG.info("Finalizando prueba de eliminar con reservas...");
    }

    // ---------------------------------------------------------------- Temporadas

    @DisplayName("Sin otra temporada definida, todas las fechas son temporada baja")
    @Test
    void testTemporadaBaseEsBaja() {
        LOG.info("Iniciando prueba de temporada base...");

        assertEquals(TemporadaAlojamiento.BAJA, empresa.tipoTemporada(entrada));

        empresa.registrarTemporada(new Temporada(entrada, entrada.plusDays(5), TemporadaAlojamiento.ALTA));

        assertEquals(TemporadaAlojamiento.ALTA, empresa.tipoTemporada(entrada.plusDays(2)));
        assertEquals(TemporadaAlojamiento.BAJA, empresa.tipoTemporada(entrada.plusDays(6)));

        LOG.info("Finalizando prueba de temporada base...");
    }

    @DisplayName("Las temporadas no se cruzan y la baja no se define")
    @Test
    void testTemporadasInvalidas() {
        LOG.info("Iniciando prueba de temporadas inválidas...");

        empresa.registrarTemporada(new Temporada(entrada, entrada.plusDays(5), TemporadaAlojamiento.MEDIA));

        try {
            empresa.registrarTemporada(new Temporada(entrada.plusDays(2), entrada.plusDays(8), TemporadaAlojamiento.ALTA));
            fail("Una temporada que se cruza debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getTemporadas().size());
        }

        try {
            empresa.registrarTemporada(new Temporada(entrada.plusDays(20), entrada.plusDays(25), TemporadaAlojamiento.BAJA));
            fail("La temporada baja no se define");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getTemporadas().size());
        }

        try {
            empresa.registrarTemporada(new Temporada(entrada.plusDays(30), entrada.plusDays(28), TemporadaAlojamiento.ALTA));
            fail("Una temporada con el fin antes del inicio debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getTemporadas().size());
        }

        LOG.info("Finalizando prueba de temporadas inválidas...");
    }

    @DisplayName("Una temporada se puede eliminar")
    @Test
    void testEliminarTemporada() {
        LOG.info("Iniciando prueba de eliminar temporada...");

        Temporada temporada = new Temporada(entrada, entrada.plusDays(5), TemporadaAlojamiento.MEDIA);
        empresa.registrarTemporada(temporada);
        empresa.eliminarTemporada(temporada);

        assertTrue(empresa.getTemporadas().isEmpty());

        LOG.info("Finalizando prueba de eliminar temporada...");
    }

    // ---------------------------------------------------------------- Cotización

    @DisplayName("La estancia cuesta tarifa x noches x personas que pagan")
    @Test
    void testCotizar() {
        LOG.info("Iniciando prueba de cotización...");

        //2 adultos, 3 noches, tarifa baja de 100000
        assertEquals(600000, empresa.cotizar(apartamento, entrada, salida, adultos(2)));

        LOG.info("Finalizando prueba de cotización...");
    }

    @DisplayName("Los menores de 6 años ocupan cupo pero no pagan")
    @Test
    void testCotizarConMenores() {
        LOG.info("Iniciando prueba de cotización con menores...");

        LinkedList<Ocupante> grupo = adultos(2);
        grupo.add(new Ocupante("Niña", entrada.minusYears(4)));

        assertEquals(600000, empresa.cotizar(apartamento, entrada, salida, grupo));

        LOG.info("Finalizando prueba de cotización con menores...");
    }

    @DisplayName("Una estancia que cruza temporadas se cobra noche por noche")
    @Test
    void testCotizarCruzandoTemporadas() {
        LOG.info("Iniciando prueba de cotización con varias temporadas...");

        //La primera noche es baja y las otras dos son altas
        empresa.registrarTemporada(new Temporada(entrada.plusDays(1), entrada.plusDays(10), TemporadaAlojamiento.ALTA));

        //2 personas: 100000*2 + 200000*2 + 200000*2
        assertEquals(1000000, empresa.cotizar(apartamento, entrada, salida, adultos(2)));

        LOG.info("Finalizando prueba de cotización con varias temporadas...");
    }

    @DisplayName("Si falta la tarifa de la temporada de una noche, la cotización se rechaza")
    @Test
    void testCotizarSinTarifa() {
        LOG.info("Iniciando prueba de cotización sin tarifa...");

        Apartamento sinAlta = new Apartamento("A2", "Solo baja", 1, 2, new LinkedList<>(), new LinkedList<>());
        empresa.registrarApartamento(sinAlta);
        empresa.definirTarifa(sinAlta, TemporadaAlojamiento.BAJA, 50000);
        empresa.registrarTemporada(new Temporada(entrada, entrada.plusDays(10), TemporadaAlojamiento.ALTA));

        try {
            empresa.cotizar(sinAlta, entrada, salida, adultos(1));
            fail("Sin tarifa de temporada alta la cotización debía rechazarse");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("A2"));
        }

        LOG.info("Finalizando prueba de cotización sin tarifa...");
    }

    @DisplayName("El detalle de la cotización muestra cada noche y el total")
    @Test
    void testDetalleCotizacion() {
        LOG.info("Iniciando prueba del detalle de la cotización...");

        String detalle = empresa.detalleCotizacion(apartamento, entrada, salida, adultos(1));

        assertTrue(detalle.contains(entrada.toString()));
        assertTrue(detalle.contains(entrada.plusDays(2).toString()));
        assertFalse(detalle.contains(salida.toString() + "  "));
        assertTrue(detalle.contains("TOTAL: $300.000"));

        LOG.info("Finalizando prueba del detalle de la cotización...");
    }

    // ---------------------------------------------------------------- Disponibilidad

    @DisplayName("Solo se ofrecen los apartamentos con capacidad suficiente")
    @Test
    void testBuscarPorCapacidad() {
        LOG.info("Iniciando prueba de búsqueda por capacidad...");

        assertEquals(1, empresa.buscarDisponibles(entrada, salida, 4).size());
        assertTrue(empresa.buscarDisponibles(entrada, salida, 5).isEmpty());

        LOG.info("Finalizando prueba de búsqueda por capacidad...");
    }

    @DisplayName("Un apartamento fuera de servicio no se ofrece")
    @Test
    void testBuscarFueraDeServicio() {
        LOG.info("Iniciando prueba de búsqueda con apartamento fuera de servicio...");

        apartamento.registrarMotivoFueraDeServicio("Daño en la tubería");

        assertTrue(empresa.buscarDisponibles(entrada, salida, 1).isEmpty());

        LOG.info("Finalizando prueba de búsqueda con apartamento fuera de servicio...");
    }

    @DisplayName("La búsqueda valida las fechas y las personas")
    @Test
    void testBuscarConDatosInvalidos() {
        LOG.info("Iniciando prueba de búsqueda inválida...");

        try {
            empresa.buscarDisponibles(salida, entrada, 2);
            fail("La salida antes de la entrada debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("salida"));
        }

        try {
            empresa.buscarDisponibles(LocalDate.now().minusDays(1), entrada, 2);
            fail("Una entrada anterior a hoy debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("hoy"));
        }

        try {
            empresa.buscarDisponibles(entrada, salida, 0);
            fail("Cero personas debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("personas"));
        }

        LOG.info("Finalizando prueba de búsqueda inválida...");
    }

    // ---------------------------------------------------------------- Reservas

    @DisplayName("Sin doble venta: dos reservas activas no comparten una noche, sin importar el canal")
    @Test
    void testDobleVenta() {
        LOG.info("Iniciando prueba de doble venta...");

        empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));

        try {
            empresa.crearReservaDirecta(titular, apartamento, entrada.plusDays(1), salida.plusDays(1), adultos(2));
            fail("La segunda reserva comparte noches y debía rechazarse");
        } catch (IllegalStateException e) {
            assertEquals(1, empresa.getReservas().size());
        }

        LOG.info("Finalizando prueba de doble venta...");
    }

    @DisplayName("La noche de la fecha de salida queda libre para otra reserva")
    @Test
    void testNocheDeSalida() {
        LOG.info("Iniciando prueba de la noche de salida...");

        empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));
        empresa.crearReservaDirecta(titular, apartamento, salida, salida.plusDays(1), adultos(2));

        assertEquals(2, empresa.getReservas().size());

        LOG.info("Finalizando prueba de la noche de salida...");
    }

    @DisplayName("Una reserva cancelada libera sus noches")
    @Test
    void testCanceladaLiberaNoches() {
        LOG.info("Iniciando prueba de noches liberadas...");

        Reserva reserva = empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));
        assertTrue(empresa.hayConflicto(apartamento, entrada, salida));

        reserva.cancelar();

        assertFalse(empresa.hayConflicto(apartamento, entrada, salida));

        LOG.info("Finalizando prueba de noches liberadas...");
    }

    @DisplayName("Una reserva nueva no puede superar la capacidad, tener fechas inválidas ni estar en el pasado")
    @Test
    void testValidacionesDeReserva() {
        LOG.info("Iniciando prueba de validaciones de reserva...");

        try {
            empresa.crearReservaDirecta(titular, apartamento, entrada, salida, adultos(5));
            fail("5 ocupantes superan la capacidad de 4");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("capacidad"));
        }

        try {
            empresa.crearReservaDirecta(titular, apartamento, salida, entrada, adultos(2));
            fail("La salida antes de la entrada debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("salida"));
        }

        try {
            empresa.crearReservaDirecta(titular, apartamento, entrada, entrada, adultos(2));
            fail("Una estancia de cero noches debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("noche"));
        }

        try {
            empresa.crearReservaDirecta(titular, apartamento, LocalDate.now().minusDays(1), entrada, adultos(2));
            fail("Una entrada anterior a hoy debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("hoy"));
        }

        try {
            empresa.crearReservaDirecta(titular, apartamento, entrada, salida, new LinkedList<Ocupante>());
            fail("Una reserva sin ocupantes debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("ocupante"));
        }

        assertTrue(empresa.getReservas().isEmpty());

        LOG.info("Finalizando prueba de validaciones de reserva...");
    }

    @DisplayName("Valor congelado: cambiar la tarifa después no afecta la reserva ya creada")
    @Test
    void testValorCongelado() {
        LOG.info("Iniciando prueba de valor congelado...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, adultos(2));
        empresa.definirTarifa(apartamento, TemporadaAlojamiento.BAJA, 500000);

        assertEquals(600000, reserva.getValorEstancia());
        assertEquals(600000, reserva.getFolio().totalCargo());

        LOG.info("Finalizando prueba de valor congelado...");
    }

    @DisplayName("Los códigos de reserva se generan en orden")
    @Test
    void testCodigoDeReserva() {
        LOG.info("Iniciando prueba de códigos de reserva...");

        assertEquals("RES-0001", empresa.generarCodigoReserva());

        Reserva primera = empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(1));
        assertEquals("RES-0001", primera.getCodigo());
        assertEquals("RES-0002", empresa.generarCodigoReserva());
        assertEquals(primera, empresa.buscarReserva("RES-0001"));
        assertNull(empresa.buscarReserva("RES-0099"));

        LOG.info("Finalizando prueba de códigos de reserva...");
    }

    @DisplayName("Las reservas externas no se crean con crearReserva")
    @Test
    void testCrearReservaRechazaExterna() {
        LOG.info("Iniciando prueba de crearReserva con una externa...");

        ReservaExterna externa = new ReservaExterna("X1", entrada, salida, apartamento, titular, adultos(2), "BKG-1");

        try {
            empresa.crearReserva(externa);
            fail("Las reservas externas se registran con registrarReservaExterna");
        } catch (IllegalArgumentException e) {
            assertTrue(empresa.getReservas().isEmpty());
        }

        LOG.info("Finalizando prueba de crearReserva con una externa...");
    }

    @DisplayName("Reserva externa: si choca con una vigente se rechaza y queda como conflicto de canal")
    @Test
    void testExternaConConflicto() {
        LOG.info("Iniciando prueba de conflicto de canal...");

        empresa.crearReservaDirecta(titular, apartamento, entrada, salida, adultos(2));
        ReservaExterna choca = new ReservaExterna("X1", entrada, salida, apartamento, titular, adultos(2), "BKG-1");

        boolean guardada = empresa.registrarReservaExterna(choca);

        assertFalse(guardada);
        assertEquals(1, empresa.getReservas().size());
        assertEquals(1, empresa.getConflictoCanales().size());
        assertEquals("BKG-1", empresa.getConflictoCanales().get(0).getCodigoExterno());
        assertEquals("A1", empresa.getConflictoCanales().get(0).getCodigoApartamento());

        LOG.info("Finalizando prueba de conflicto de canal...");
    }

    @DisplayName("Reserva externa sin choque: se guarda con su código y su valor")
    @Test
    void testExternaSinConflicto() {
        LOG.info("Iniciando prueba de reserva externa...");

        ReservaExterna libre = new ReservaExterna("X1", entrada, salida, apartamento, titular, adultos(2), "BKG-1");

        assertTrue(empresa.registrarReservaExterna(libre));
        assertEquals(1, empresa.getReservas().size());
        assertEquals(600000, libre.getValorEstancia());
        assertTrue(empresa.getCodigosExternos().contains("BKG-1"));
        assertTrue(empresa.getConflictoCanales().isEmpty());

        LOG.info("Finalizando prueba de reserva externa...");
    }

    @DisplayName("Un mismo código externo no puede crear dos reservas")
    @Test
    void testCodigoExternoRepetido() {
        LOG.info("Iniciando prueba de código externo repetido...");

        empresa.registrarReservaExterna(new ReservaExterna("X1", entrada, salida, apartamento, titular, adultos(2), "BKG-1"));
        ReservaExterna repetida = new ReservaExterna("X2", entrada.plusDays(10), entrada.plusDays(12), apartamento, titular, adultos(2), "BKG-1");

        try {
            empresa.registrarReservaExterna(repetida);
            fail("El código externo repetido debía rechazarse");
        } catch (IllegalStateException e) {
            assertEquals(1, empresa.getReservas().size());
        }

        LOG.info("Finalizando prueba de código externo repetido...");
    }

    @DisplayName("El historial de un huésped incluye solo sus reservas")
    @Test
    void testHistorialHuesped() {
        LOG.info("Iniciando prueba de historial...");

        Huesped otro = new Huesped("Luis", "2", "301", "luis@correo.com", "luis", "clave", LocalDate.of(1985, 1, 1), null);
        empresa.registrarHuesped(otro);
        empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(1));
        empresa.crearReservaDirecta(otro, apartamento, salida.plusDays(5), salida.plusDays(7), adultos(1));

        assertEquals(1, empresa.historialHuesped(titular).size());
        assertEquals(1, empresa.historialHuesped(otro).size());
        assertEquals(2, empresa.getReservas().size());

        LOG.info("Finalizando prueba de historial...");
    }

    @DisplayName("Una reserva pendiente reciente no se cancela automáticamente")
    @Test
    void testPendientesRecientes() {
        LOG.info("Iniciando prueba de pendientes vencidas...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, adultos(1));

        assertEquals(0, empresa.cancelarPendientesVencidas());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());

        LOG.info("Finalizando prueba de pendientes vencidas...");
    }

    // ---------------------------------------------------------------- Bloqueos

    @DisplayName("Un bloqueo quita el apartamento de los disponibles esas noches")
    @Test
    void testBloqueo() {
        LOG.info("Iniciando prueba de bloqueo...");

        empresa.registrarBloqueo(new Bloqueo(apartamento, entrada, entrada.plusDays(1), "Plomería"));

        assertTrue(empresa.buscarDisponibles(entrada, salida, 2).isEmpty());
        assertEquals(1, empresa.buscarDisponibles(entrada.plusDays(5), entrada.plusDays(7), 2).size());

        try {
            empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));
            fail("No se puede reservar una noche bloqueada");
        } catch (IllegalStateException e) {
            assertTrue(empresa.getReservas().isEmpty());
        }

        LOG.info("Finalizando prueba de bloqueo...");
    }

    @DisplayName("No se bloquea un apartamento que tiene reservas activas en esas fechas")
    @Test
    void testBloqueoConReserva() {
        LOG.info("Iniciando prueba de bloqueo con reserva...");

        empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));

        try {
            empresa.registrarBloqueo(new Bloqueo(apartamento, entrada, entrada.plusDays(1), "Pintura"));
            fail("El bloqueo choca con una reserva activa");
        } catch (IllegalStateException e) {
            assertTrue(empresa.getBloqueos().isEmpty());
        }

        LOG.info("Finalizando prueba de bloqueo con reserva...");
    }

    @DisplayName("Un bloqueo necesita fechas válidas y un motivo; se puede eliminar")
    @Test
    void testBloqueoInvalidoYEliminar() {
        LOG.info("Iniciando prueba de bloqueo inválido...");

        try {
            empresa.registrarBloqueo(new Bloqueo(apartamento, entrada.plusDays(5), entrada, "Motivo"));
            fail("El fin antes del inicio debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(empresa.getBloqueos().isEmpty());
        }

        try {
            empresa.registrarBloqueo(new Bloqueo(apartamento, entrada, entrada.plusDays(1), ""));
            fail("Un bloqueo sin motivo debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(empresa.getBloqueos().isEmpty());
        }

        Bloqueo bloqueo = new Bloqueo(apartamento, entrada, entrada.plusDays(1), "Plomería");
        empresa.registrarBloqueo(bloqueo);
        assertEquals(1, empresa.getBloqueos().size());
        empresa.eliminarBloqueo(bloqueo);
        assertTrue(empresa.getBloqueos().isEmpty());

        LOG.info("Finalizando prueba de bloqueo inválido...");
    }

    // ---------------------------------------------------------------- Pagos y cargos

    @DisplayName("Con el anticipo del 30 % la reserva del portal pasa a Confirmada")
    @Test
    void testPagoConfirmaReservaPortal() {
        LOG.info("Iniciando prueba de pagos...");

        Reserva reserva = empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());

        empresa.registrarPago(reserva, 100000, MedioPago.TARJETA);
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());

        empresa.registrarPago(reserva, 80000, MedioPago.TRANSFERENCIA);
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertEquals(420000, reserva.getFolio().calcularSaldo());

        LOG.info("Finalizando prueba de pagos...");
    }

    @DisplayName("Un pago debe ser mayor que cero y no puede superar el saldo")
    @Test
    void testPagoInvalido() {
        LOG.info("Iniciando prueba de pago inválido...");

        Reserva reserva = empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(2));

        try {
            empresa.registrarPago(reserva, 0, MedioPago.EFECTIVO);
            fail("Un pago en cero debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(0, reserva.getFolio().totalPago());
        }

        try {
            empresa.registrarPago(reserva, 700000, MedioPago.EFECTIVO);
            fail("Un pago mayor al saldo debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(0, reserva.getFolio().totalPago());
        }

        LOG.info("Finalizando prueba de pago inválido...");
    }

    @DisplayName("No se registran pagos ni cargos en una reserva cancelada")
    @Test
    void testPagosEnReservaCancelada() {
        LOG.info("Iniciando prueba de pagos en reserva cancelada...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, adultos(2));
        reserva.cancelar();

        try {
            empresa.registrarPago(reserva, 1000, MedioPago.EFECTIVO);
            fail("No se registran pagos en una reserva cancelada");
        } catch (IllegalStateException e) {
            assertEquals(0, reserva.getFolio().totalPago());
        }

        try {
            empresa.registrarCargo(reserva, "Desayuno", 20000, TipoCargo.SERVICIO_ADICIONAL);
            fail("No se registran cargos en una reserva cancelada");
        } catch (IllegalStateException e) {
            assertEquals(0, reserva.getFolio().totalCargo());
        }

        LOG.info("Finalizando prueba de pagos en reserva cancelada...");
    }

    @DisplayName("Un cargo adicional se suma al folio")
    @Test
    void testCargoAdicional() {
        LOG.info("Iniciando prueba de cargo adicional...");

        Reserva reserva = empresa.crearReservaDirecta(titular, apartamento, entrada, salida, adultos(2));
        empresa.registrarCargo(reserva, "Desayuno", 25000, TipoCargo.SERVICIO_ADICIONAL);

        assertEquals(625000, reserva.getFolio().totalCargo());

        try {
            empresa.registrarCargo(reserva, "Lavandería", 0, TipoCargo.SERVICIO_ADICIONAL);
            fail("Un cargo en cero debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(625000, reserva.getFolio().totalCargo());
        }

        try {
            empresa.registrarCargo(reserva, "", 5000, TipoCargo.SERVICIO_ADICIONAL);
            fail("Un cargo sin concepto debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(625000, reserva.getFolio().totalCargo());
        }

        LOG.info("Finalizando prueba de cargo adicional...");
    }

    // ---------------------------------------------------------------- Personas y política

    @DisplayName("La cédula y el usuario no se pueden repetir")
    @Test
    void testRolesUnicos() {
        LOG.info("Iniciando prueba de roles únicos...");

        Huesped mismaCedula = new Huesped("Otra", "1", "3", "o@o.com", "otra", "x", LocalDate.of(2000, 1, 1), null);
        try {
            empresa.registrarHuesped(mismaCedula);
            fail("La cédula repetida debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("cédula"));
        }

        Huesped mismoUsuario = new Huesped("Otra", "2", "3", "o@o.com", "ana", "x", LocalDate.of(2000, 1, 1), null);
        try {
            empresa.registrarHuesped(mismoUsuario);
            fail("El usuario repetido debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("usuario"));
        }

        assertEquals(1, empresa.getRoles().size());

        LOG.info("Finalizando prueba de roles únicos...");
    }

    @DisplayName("Los datos obligatorios de una persona no pueden estar vacíos")
    @Test
    void testPersonaIncompleta() {
        LOG.info("Iniciando prueba de persona incompleta...");

        Huesped sinNombre = new Huesped("", "5", "3", "o@o.com", "sinnombre", "x", LocalDate.of(2000, 1, 1), null);

        try {
            empresa.registrarHuesped(sinNombre);
            fail("Una persona sin nombre debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getRoles().size());
        }

        LOG.info("Finalizando prueba de persona incompleta...");
    }

    @DisplayName("Autenticación: devuelve la persona o null si los datos no coinciden")
    @Test
    void testAutenticar() {
        LOG.info("Iniciando prueba de autenticación...");

        assertEquals(titular, empresa.autenticar("ana", "clave"));
        assertNull(empresa.autenticar("ana", "mala"));
        assertNull(empresa.autenticar("nadie", "clave"));
        assertNull(empresa.autenticar(null, null));

        LOG.info("Finalizando prueba de autenticación...");
    }

    @DisplayName("Un recepcionista desvinculado ya no puede ingresar")
    @Test
    void testDesvincularRecepcionista() {
        LOG.info("Iniciando prueba de desvinculación...");

        Recepcionista recepcionista = new Recepcionista("Rita", "9", "3", "r@r.com", "rita", "pw", LocalDate.now(), true);
        empresa.registrarRecepcionista(recepcionista);
        assertEquals(recepcionista, empresa.autenticar("rita", "pw"));
        assertEquals(1, empresa.getRecepcionistas().size());

        empresa.desvincularRecepcionista("9");

        assertFalse(recepcionista.getActivo());
        assertNull(empresa.autenticar("rita", "pw"));

        try {
            empresa.desvincularRecepcionista("000");
            fail("Una cédula inexistente debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getRecepcionistas().size());
        }

        LOG.info("Finalizando prueba de desvinculación...");
    }

    @DisplayName("Se busca un huésped por su cédula")
    @Test
    void testBuscarHuesped() {
        LOG.info("Iniciando prueba de búsqueda de huésped...");

        assertEquals(titular, empresa.buscarHuesped("1"));
        assertNull(empresa.buscarHuesped("999"));
        assertEquals(1, empresa.getHuespedes().size());

        LOG.info("Finalizando prueba de búsqueda de huésped...");
    }

    @DisplayName("Registrar un administrador permite iniciar sesión con él")
    @Test
    void testAdministrador() {
        LOG.info("Iniciando prueba de administrador...");

        empresa.registrarAdministrador(new AdministradorAlojamiento("Admin", "100", "3", "a@a.com", "admin", "admin123"));

        assertNotNull(empresa.autenticar("admin", "admin123"));

        LOG.info("Finalizando prueba de administrador...");
    }

    @DisplayName("La política de cancelación se configura y las reservas nuevas la guardan")
    @Test
    void testPoliticaDeCancelacion() {
        LOG.info("Iniciando prueba de política de cancelación...");

        assertEquals(7, empresa.getPoliticaCancelacion().getDiasDevolucion());

        empresa.configurarPoliticaCancelacion(10, 3, 40);
        Reserva reserva = empresa.crearReservaPortal(titular, apartamento, entrada, salida, adultos(1));

        assertEquals(10, empresa.getPoliticaCancelacion().getDiasDevolucion());
        assertEquals(10, reserva.getPoliticaCancelacion().getDiasDevolucion());
        assertEquals(40.0, reserva.getPoliticaCancelacion().getPorcentajeRetencion());

        //Un cambio posterior no afecta a la reserva ya creada
        empresa.configurarPoliticaCancelacion(5, 1, 20);
        assertEquals(10, reserva.getPoliticaCancelacion().getDiasDevolucion());

        LOG.info("Finalizando prueba de política de cancelación...");
    }

    @DisplayName("La política de cancelación rechaza valores que no tienen sentido")
    @Test
    void testPoliticaInvalida() {
        LOG.info("Iniciando prueba de política inválida...");

        try {
            empresa.configurarPoliticaCancelacion(-1, 2, 50);
            fail("Días negativos debían rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(7, empresa.getPoliticaCancelacion().getDiasDevolucion());
        }

        try {
            empresa.configurarPoliticaCancelacion(3, 5, 50);
            fail("Retención mayor que devolución debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(7, empresa.getPoliticaCancelacion().getDiasDevolucion());
        }

        try {
            empresa.configurarPoliticaCancelacion(7, 2, 150);
            fail("Un porcentaje mayor a 100 debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(7, empresa.getPoliticaCancelacion().getDiasDevolucion());
        }

        LOG.info("Finalizando prueba de política inválida...");
    }

    @DisplayName("Las novedades se registran con descripción")
    @Test
    void testNovedades() {
        LOG.info("Iniciando prueba de novedades...");

        empresa.registrarNovedad(new Novedad("Llave dañada", LocalDate.now(), TipoNovedad.DANIO, apartamento, null));
        assertEquals(1, empresa.getNovedades().size());

        try {
            empresa.registrarNovedad(new Novedad("", LocalDate.now(), TipoNovedad.OTRA, apartamento, null));
            fail("Una novedad sin descripción debía rechazarse");
        } catch (IllegalArgumentException e) {
            assertEquals(1, empresa.getNovedades().size());
        }

        LOG.info("Finalizando prueba de novedades...");
    }
}
