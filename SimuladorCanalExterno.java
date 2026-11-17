package co.edu.uniquindio.ProjectoFinal;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Random;

public class SimuladorCanalExterno {
    private static final String[] NOMBRES = {"Carlos", "Laura", "Andrés", "Marcela", "Juan", "Valentina", "Sofía", "Daniel"};
    private static final String[] APELLIDOS = {"Gómez", "Rodríguez", "Martínez", "López", "Herrera", "Ramírez"};

    private final Random random = new Random();
    private int consecutivo = 0;

    //Genera una reserva externa aleatoria sobre un apartamento de la empresa
    public ReservaExterna generarReservaAleatoria(Empresa empresa) {
        LinkedList<Apartamento> apartamentos = new LinkedList<>();
        for (Apartamento a : empresa.getApartamentos()) {
            if (a.verificarDisponibilidad()) {
                apartamentos.add(a);
            }
        }
        if (apartamentos.isEmpty()) {
            return null;
        }
        Apartamento apartamento = apartamentos.get(random.nextInt(apartamentos.size()));

        //La entrada no puede ser anterior a hoy
        LocalDate entrada = LocalDate.now().plusDays(1 + random.nextInt(60));
        LocalDate salida = entrada.plusDays(1 + random.nextInt(5));

        LinkedList<Ocupante> ocupantes = generarOcupantes(1 + random.nextInt(apartamento.getCapacidadMaxima()));
        Huesped titular = generarTitular();

        //Un mismo código externo no puede crear dos reservas
        consecutivo++;
        String codigoExterno = "EXT-" + System.currentTimeMillis() + "-" + consecutivo;
        String codigo = "RES-" + codigoExterno;

        //El valor de la estancia lo calcula Empresa al guardar la reserva
        return new ReservaExterna(codigo, entrada, salida, apartamento, titular, ocupantes, codigoExterno);
    }

    //Envía varias reservas externas a la empresa. Devuelve los conflictos de canal que se generaron
    public LinkedList<ConflictoCanal> simular(Empresa empresa, int cantidad) {
        LinkedList<ConflictoCanal> conflictos = new LinkedList<>();
        for (int i = 0; i < cantidad; i++) {
            ReservaExterna reserva = generarReservaAleatoria(empresa);
            if (reserva == null) {
                break;
            }
            int conflictosAntes = empresa.getConflictoCanales().size();
            //Empresa rechaza la reserva y registra el conflicto si choca con una vigente
            try {
                empresa.registrarReservaExterna(reserva);
            } catch (RuntimeException e) {
                //Una reserva externa inválida simplemente se descarta
            }
            LinkedList<ConflictoCanal> actuales = empresa.getConflictoCanales();
            if (actuales.size() > conflictosAntes) {
                conflictos.add(actuales.getLast());
            }
        }
        return conflictos;
    }

    //Auxiliares
    private LinkedList<Ocupante> generarOcupantes(int cantidad) {
        LinkedList<Ocupante> ocupantes = new LinkedList<>();
        for (int i = 0; i < cantidad; i++) {
            //Edades entre 0 y 60 años, para que algunos menores de 6 no paguen
            LocalDate nacimiento = LocalDate.now().minusDays(random.nextInt(60 * 365));
            ocupantes.add(new Ocupante(nombreAleatorio(), nacimiento));
        }
        return ocupantes;
    }

    private Huesped generarTitular() {
        String cedula = String.valueOf(10000000 + random.nextInt(90000000));
        LocalDate nacimiento = LocalDate.now().minusYears(20 + random.nextInt(40));
        return new Huesped(nombreAleatorio(), cedula, "300" + (1000000 + random.nextInt(9000000)),
                "externo" + cedula + "@plataforma.com", cedula, "externo", nacimiento, null);
    }

    private String nombreAleatorio() {
        return NOMBRES[random.nextInt(NOMBRES.length)] + " " + APELLIDOS[random.nextInt(APELLIDOS.length)];
    }
}
