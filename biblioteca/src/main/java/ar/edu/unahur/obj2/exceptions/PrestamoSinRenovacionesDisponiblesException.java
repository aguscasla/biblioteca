package ar.edu.unahur.obj2.exceptions;

public class PrestamoSinRenovacionesDisponiblesException extends RuntimeException{
    
    public PrestamoSinRenovacionesDisponiblesException() {
        super("El prestamo no cuenta con renovaciones disponibles");
    }

}
