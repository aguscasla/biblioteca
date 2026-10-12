package ar.edu.unahur.obj2.exceptions;

public class UsuarioNoReasociableException extends RuntimeException{

    public UsuarioNoReasociableException() {
        super("Solamente se permite reasociar a los usuarios con baja voluntaria");
    }

}
