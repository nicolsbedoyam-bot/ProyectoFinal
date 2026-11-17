package co.edu.uniquindio.ProjectoFinal;

import java.time.LocalDate;
import java.util.LinkedList;

public class ReservaExterna extends Reserva {
    private static final int PORCENTAJE_COMISION = 15;

    private String codigoExterno;

    public ReservaExterna(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes, String codigoExterno){
        super(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes);
        this.codigoExterno = codigoExterno;
    }

    public ReservaExterna(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes, String codigoExterno, PoliticaCancelacion politicaCancelacion){
        super(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes, politicaCancelacion);
        this.codigoExterno = codigoExterno;
    }

    @Override
    public Canal getCanal(){
        return Canal.EXTERNA;
    }

    //Externa: el alojamiento paga a la plataforma una comisión del 15 % sobre el valor de la estancia
    @Override
    public int calcularComision(){
        return getValorEstancia() * PORCENTAJE_COMISION / 100;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }
}
