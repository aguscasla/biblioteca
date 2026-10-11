package ar.edu.unahur.obj2.exceptions;

public class PrestamoNoEncontradoException extends RuntimeException{

    public PrestamoNoEncontradoException() {
        super("No se ha encontrando el prestamo");
    }

}
