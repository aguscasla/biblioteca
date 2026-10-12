package ar.edu.unahur.obj2.exceptions;

public class LibroInhabilitadoException extends RuntimeException{

    public LibroInhabilitadoException() {
        super("El libro se encuentra inhabilitado");
    }

}
