package ar.edu.unahur.obj2.exceptions;

public class UsuarioInactivoException extends RuntimeException{

    public UsuarioInactivoException() {
        super("El usuario no se encuentra activo");
    }
    
}
