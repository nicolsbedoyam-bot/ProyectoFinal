package co.edu.uniquindio.ProjectoFinal;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedList;

public class CalendarioOcupacion {
    private Empresa empresa;

    public CalendarioOcupacion(Empresa empresa) {
        this.empresa = empresa;
    }

    //Indica si una noche de un apartamento está ocupada, bloqueada o libre.
    //La noche de la fecha de salida no se ocupa: la noche cubre [entrada, salida)
    public EstadoNoche estadoNoche(Apartamento apartamento, LocalDate fecha) {
        if (apartamento == null || fecha == null) {
            throw new IllegalArgumentException("El apartamento y la fecha no pueden ser nulos");
        }
        for (Reserva r : empresa.getReservas()) {
            boolean mismoApartamento = r.getApartamento().getCodigo().equals(apartamento.getCodigo());
            boolean cubreNoche = !fecha.isBefore(r.getFechaEntrada()) && fecha.isBefore(r.getFechaSalida());
            if (r.estaActiva() && mismoApartamento && cubreNoche) {
                return EstadoNoche.OCUPADA;
            }
        }
        for (Bloqueo b : empresa.getBloqueos()) {
            if (b.getApartamento().getCodigo().equals(apartamento.getCodigo()) && b.cubre(fecha)) {
                return EstadoNoche.BLOQUEADA;
            }
        }
        return EstadoNoche.LIBRE;
    }

    //Vista mensual: una posición por cada día del mes (índice 0 = día 1)
    public LinkedList<EstadoNoche> estadosDelMes(Apartamento apartamento, YearMonth mes) {
        LinkedList<EstadoNoche> estados = new LinkedList<>();
        for (int dia = 1; dia <= mes.lengthOfMonth(); dia++) {
            estados.add(estadoNoche(apartamento, mes.atDay(dia)));
        }
        return estados;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
