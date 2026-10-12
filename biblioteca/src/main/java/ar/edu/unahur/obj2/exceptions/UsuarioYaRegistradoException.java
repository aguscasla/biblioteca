package ar.edu.unahur.obj2.exceptions;

public class UsuarioYaRegistradoException extends RuntimeException{

    public UsuarioYaRegistradoException() {
        super("El usuario ya se encuentra registrado");
    }

}
