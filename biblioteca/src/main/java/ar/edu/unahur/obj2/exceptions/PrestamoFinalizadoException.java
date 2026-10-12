package ar.edu.unahur.obj2.exceptions;

public class PrestamoFinalizadoException extends RuntimeException{

    public PrestamoFinalizadoException() {
        super("El prestamo se encuentra finalizado");
    }

}
