package ar.edu.unahur.obj2.exceptions;

public class UsuarioSinPrestamosDisponiblesException extends RuntimeException{

    public UsuarioSinPrestamosDisponiblesException() {
        super("El usuario alcanzo el limite de prestamos");
    }

}
