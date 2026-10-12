package ar.edu.unahur.obj2.exceptions;

public class UsuarioInhabilitadoException extends RuntimeException{

    public UsuarioInhabilitadoException() {
        super("El usuario ya se encuentra inhabilitado");
    }

}
