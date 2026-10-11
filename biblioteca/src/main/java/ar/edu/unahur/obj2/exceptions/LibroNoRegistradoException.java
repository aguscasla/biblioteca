package ar.edu.unahur.obj2.exceptions;

public class LibroNoRegistradoException extends RuntimeException{

    public LibroNoRegistradoException() {
        super("El libro no se encuentra registrado");
    }

}
