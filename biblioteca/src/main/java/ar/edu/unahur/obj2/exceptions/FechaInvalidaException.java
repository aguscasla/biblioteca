package ar.edu.unahur.obj2.exceptions;

public class FechaInvalidaException extends RuntimeException{

    public FechaInvalidaException() {
        super("La fecha no es valida");
    }

}
