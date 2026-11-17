package co.edu.uniquindio.ProjectoFinal;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public class Ocupante implements Serializable {
    private String nombre;
    private LocalDate fechaNacimiento;

    public Ocupante(String nombre, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public String toString() {
        return "Ocupante:" + nombre + "-" + fechaNacimiento;
    }

    //La edad nunca se guarda: se calcula a la fecha que se indique
    public int calcularEdad(LocalDate fecha) {
        if (fecha == null || fechaNacimiento == null || fecha.isBefore(fechaNacimiento)) {
            return 0;
        }
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    //Los menores de 6 años ocupan cupo pero no pagan. La edad se calcula a la fecha de entrada
    public boolean paga(LocalDate fechaEntrada) {
        return calcularEdad(fechaEntrada) >= 6;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
