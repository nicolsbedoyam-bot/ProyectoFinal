package co.edu.uniquindio.ProjectoFinal;

import java.io.*;

public class ArchivoSerializado implements IPersistenciaDato {

    private String rutaArchivo;

    public ArchivoSerializado(String rutaArchivo){

        this.rutaArchivo= rutaArchivo;
    }

    @Override
    public void guardar(Empresa empresa){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            out.writeObject(empresa);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar: " + e.getMessage(), e);
        }
    }

    @Override
    public Empresa cargar(){
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return new Empresa("Sin informacion", "Sin informacion", "Sin ubicacion");
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Empresa) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar: " + e.getMessage(), e);
        }
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
