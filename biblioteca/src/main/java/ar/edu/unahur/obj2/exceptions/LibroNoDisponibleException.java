package ar.edu.unahur.obj2.exceptions;

public class LibroNoDisponibleException extends RuntimeException{

    public LibroNoDisponibleException() {
        super("El libro no se encuentra disponible");
    }

}
