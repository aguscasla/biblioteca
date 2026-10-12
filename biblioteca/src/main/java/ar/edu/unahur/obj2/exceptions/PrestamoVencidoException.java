package ar.edu.unahur.obj2.exceptions;

public class PrestamoVencidoException extends RuntimeException{

    public PrestamoVencidoException() {
        super("El prestamo se encuentra vencido");
    }

}
