package co.edu.uniquindio.ProjectoFinal;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public abstract class Reserva implements Serializable {
    private String codigo;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private LocalDateTime fechaCreacion;
    private int valorEstancia;
    private boolean valorCongelado;
    private LocalDate fechaCancelacion;
    private int devolucion;
    private EstadoReserva estado;
    private Apartamento apartamento;
    private Huesped titular;
    private LinkedList<Ocupante> ocupantes;
    private Folio folio;
    private PoliticaCancelacion politicaCancelacion;

    //La política por defecto es la del enunciado: más de 7 días se devuelve todo, entre 2 y 7 se retiene el 50 %
    public Reserva(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes){
        this(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes, new PoliticaCancelacion(7, 2, 50));
    }

    public Reserva(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes, PoliticaCancelacion politicaCancelacion){
        if(fechaEntrada == null || fechaSalida == null || apartamento == null || titular == null || ocupantes == null || politicaCancelacion == null){
            throw new IllegalArgumentException("Los datos de la reserva no pueden ser nulos");
        }
        this.codigo = codigo;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.apartamento = apartamento;
        this.titular = titular;
        this.ocupantes = new LinkedList<>(ocupantes);
        //Valor congelado: la política se guarda al crear la reserva
        this.politicaCancelacion = politicaCancelacion;
        this.fechaCreacion = LocalDateTime.now();
        //Toda reserva nace en estado Pendiente
        this.estado = EstadoReserva.PENDIENTE;
        this.folio = new Folio();
    }

    @Override
    public String toString(){
        return "Reserva:" + codigo + "-" + apartamento.getCodigo() + "-" + fechaEntrada + "-" + fechaSalida + "-" + estado;
    }

    //Métodos propios de cada canal
    public abstract Canal getCanal();

    public boolean requiereAnticipo(){
        return false;
    }

    public int calcularAnticipo(){
        return 0;
    }

    public int calcularComision(){
        return 0;
    }

    //Noches: la noche de la fecha de salida no se cobra ni se ocupa
    public int calcularNoches(){
        return (int) ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    //Total de cargos acumulados en el folio (alojamiento, servicios, penalidades y ajustes)
    public int calcularTotal(){
        return folio.calcularTotal();
    }

    //Una reserva está activa mientras esté Pendiente, Confirmada o En curso
    public boolean estaActiva(){
        return estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA || estado == EstadoReserva.EN_CURSO;
    }

    //Valor congelado: el valor se guarda al crear la reserva y queda en el folio como cargo de alojamiento
    public void setValorEstancia(int valorEstancia){
        if(valorCongelado){
            throw new IllegalStateException("El valor de la estancia ya fue congelado");
        }
        this.valorEstancia = valorEstancia;
        this.valorCongelado = true;
        folio.agregarCargo(new Cargo("Alojamiento", valorEstancia, TipoCargo.ALOJAMIENTO));
    }

    //Ciclo de vida: solo se permiten los cambios de la tabla; cualquier otro se rechaza
    private boolean transicionPermitida(EstadoReserva actual, EstadoReserva nuevo){
        if(actual == EstadoReserva.PENDIENTE){
            return nuevo == EstadoReserva.CONFIRMADA || nuevo == EstadoReserva.CANCELADA;
        }
        if(actual == EstadoReserva.CONFIRMADA){
            return nuevo == EstadoReserva.EN_CURSO || nuevo == EstadoReserva.CANCELADA || nuevo == EstadoReserva.NO_SHOW;
        }
        if(actual == EstadoReserva.EN_CURSO){
            return nuevo == EstadoReserva.FINALIZADA;
        }
        //Finalizada, Cancelada y No-show son estados finales
        return false;
    }

    private void validarTransicion(EstadoReserva nuevo){
        if(nuevo == null || !transicionPermitida(estado, nuevo)){
            throw new IllegalStateException("Cambio de estado no permitido: " + estado + " -> " + nuevo);
        }
    }

    public void cambiarEstado(EstadoReserva nuevo){
        validarTransicion(nuevo);
        this.estado = nuevo;
    }

    //Se confirma con el anticipo, si el canal lo exige
    public void confirmar(){
        validarTransicion(EstadoReserva.CONFIRMADA);
        if(requiereAnticipo() && folio.totalPago() < calcularAnticipo()){
            throw new IllegalStateException("Se necesita el anticipo de " + calcularAnticipo() + " para confirmar la reserva");
        }
        cambiarEstado(EstadoReserva.CONFIRMADA);
    }

    //Solo se permite el check-in si la reserva está Confirmada, ya llegó la fecha de entrada y el apartamento está Preparado
    public void hacerCheckIn(){
        hacerCheckIn(LocalDate.now());
    }

    public void hacerCheckIn(LocalDate hoy){
        validarTransicion(EstadoReserva.EN_CURSO);
        if(hoy.isBefore(fechaEntrada)){
            throw new IllegalStateException("Todavía no llega la fecha de entrada");
        }
        if(!apartamento.estaPreparado()){
            throw new IllegalStateException("El apartamento no está en estado Preparado");
        }
        cambiarEstado(EstadoReserva.EN_CURSO);
        apartamento.actualizarEstadoOperativo(EstadoOperativo.OCUPADO);
    }

    //Al hacer la salida, la reserva pasa a Finalizada y el apartamento a Pendiente de preparación
    public void hacerCheckOut(){
        cambiarEstado(EstadoReserva.FINALIZADA);
        apartamento.actualizarEstadoOperativo(EstadoOperativo.PENDIENTE_PREPARACION);
    }

    //No-show: el titular no llega antes de las 10:00 p. m. del día de entrada
    public void declararNoShow(){
        declararNoShow(LocalDateTime.now());
    }

    public void declararNoShow(LocalDateTime ahora){
        validarTransicion(EstadoReserva.NO_SHOW);
        if(ahora.isBefore(fechaEntrada.atTime(22, 0))){
            throw new IllegalStateException("Todavía no pasa la hora límite (10:00 p. m. del día de entrada)");
        }
        cambiarEstado(EstadoReserva.NO_SHOW);
        liquidar(EstadoReserva.NO_SHOW, ahora.toLocalDate());
    }

    //Cancelación de una reserva Pendiente o Confirmada. Calcula y registra la devolución, pero no mueve dinero real
    public void cancelar(){
        cancelar(LocalDateTime.now());
    }

    public void cancelar(LocalDateTime ahora){
        cambiarEstado(EstadoReserva.CANCELADA);
        this.fechaCancelacion = ahora.toLocalDate();
        liquidar(EstadoReserva.CANCELADA, fechaCancelacion);
    }

    //Cuánto se devolvería si se cancelara en ese momento (no modifica nada)
    public int calcularDevolucion(LocalDateTime ahora){
        if(!estaActiva()){
            return devolucion;
        }
        int retenido = calcularRetenido(ahora.toLocalDate(), EstadoReserva.CANCELADA);
        return folio.totalPago() - retenido;
    }

    //Lo retenido nunca supera lo que se ha pagado
    private int calcularRetenido(LocalDate hoy, EstadoReserva motivo){
        int retencion = politicaCancelacion.calcularRetencion(valorEstancia, hoy, fechaEntrada, motivo);
        return Math.min(retencion, folio.totalPago());
    }

    //Pagos inalterables: la liquidación se registra como movimientos contrarios en el folio
    private void liquidar(EstadoReserva motivo, LocalDate hoy){
        int pagado = folio.totalPago();
        int retenido = calcularRetenido(hoy, motivo);
        this.devolucion = pagado - retenido;
        if(valorCongelado){
            folio.agregarCargo(new Cargo("Anulación del alojamiento", -valorEstancia, TipoCargo.AJUSTE));
        }
        if(retenido > 0){
            String concepto = motivo == EstadoReserva.NO_SHOW ? "Retención por no-show" : "Retención por cancelación";
            folio.agregarCargo(new Cargo(concepto, retenido, TipoCargo.PENALIDAD));
        }
        if(devolucion > 0){
            folio.registrarPago(new Pago(-devolucion, hoy, medioUltimoPago()));
        }
    }

    private MedioPago medioUltimoPago(){
        LinkedList<Pago> pagos = folio.getPagos();
        if(pagos.isEmpty()){
            return MedioPago.TRANSFERENCIA;
        }
        return pagos.getLast().medioPago();
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public int getValorEstancia() {
        return valorEstancia;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public int getDevolucion() {
        return devolucion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public Huesped getTitular() {
        return titular;
    }

    public LinkedList<Ocupante> getOcupantes() {
        return new LinkedList<>(ocupantes);
    }

    public Folio getFolio() {
        return folio;
    }

    public PoliticaCancelacion getPoliticaCancelacion() {
        return politicaCancelacion;
    }
}
