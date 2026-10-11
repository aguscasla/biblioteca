package ar.edu.unahur.obj2.exceptions;

public class LibroYaRegistradoException extends RuntimeException{

    public LibroYaRegistradoException() {
        super("El libro ya se encuentra registrado");
    }

}
