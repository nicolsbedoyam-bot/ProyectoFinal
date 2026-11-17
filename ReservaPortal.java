package co.edu.uniquindio.ProjectoFinal;

import java.time.LocalDate;
import java.util.LinkedList;

public class ReservaPortal extends Reserva {
    private static final int PORCENTAJE_ANTICIPO = 30;

    public ReservaPortal(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes){
        super(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes);
    }

    public ReservaPortal(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes, PoliticaCancelacion politicaCancelacion){
        super(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes, politicaCancelacion);
    }

    @Override
    public Canal getCanal(){
        return Canal.PORTAL;
    }

    //Portal: la reserva queda Pendiente hasta que pague un anticipo del 30 %
    @Override
    public boolean requiereAnticipo(){
        return true;
    }

    @Override
    public int calcularAnticipo(){
        return getValorEstancia() * PORCENTAJE_ANTICIPO / 100;
    }
}
