package ar.edu.unahur.obj2.exceptions;

public class UsuarioNoRegistradoException extends RuntimeException{

    public UsuarioNoRegistradoException() {
        super("El usuario no se encuentra registrado");
    }

}
