package co.edu.uniquindio.ProjectoFinal;

import java.time.LocalDate;
import java.util.LinkedList;

public class ReservaDirecta extends Reserva {

    public ReservaDirecta(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes){
        super(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes);
    }

    public ReservaDirecta(String codigo, LocalDate fechaEntrada, LocalDate fechaSalida, Apartamento apartamento, Huesped titular, LinkedList<Ocupante> ocupantes, PoliticaCancelacion politicaCancelacion){
        super(codigo, fechaEntrada, fechaSalida, apartamento, titular, ocupantes, politicaCancelacion);
    }

    @Override
    public Canal getCanal(){
        return Canal.DIRECTA;
    }

    //Directa: la recepción la registra y no exige anticipo
    @Override
    public boolean requiereAnticipo(){
        return false;
    }
}
