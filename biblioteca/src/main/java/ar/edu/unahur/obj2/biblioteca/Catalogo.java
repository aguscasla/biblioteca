package ar.edu.unahur.obj2.biblioteca;

import java.util.HashMap;
import java.util.Map;

import ar.edu.unahur.obj2.exceptions.LibroNoDisponibleException;
import ar.edu.unahur.obj2.exceptions.LibroNoRegistradoException;
import ar.edu.unahur.obj2.exceptions.LibroYaRegistradoException;
import ar.edu.unahur.obj2.libros.Libro;

public class Catalogo {

    private Map<Integer, Libro> catalogo = new HashMap<>();

    public void agregarLibro(Libro libro){

        if(libro == null){
            throw new IllegalArgumentException("El libro es invalido");
        }

        if(catalogo.containsKey(libro.getId())){
            throw new LibroYaRegistradoException();
        }

        catalogo.put(libro.getId(), libro);

    }

    public void eliminarLibro(Integer idDelLibro){
        Libro libro = catalogo.get(idDelLibro);

        if(libro == null){
            throw new LibroNoRegistradoException();
        }

        catalogo.remove(idDelLibro);
    }

    public Libro obtenerLibro(Integer idDelLibro){
        Libro libro = catalogo.get(idDelLibro);

        if(libro == null){
            throw new LibroNoRegistradoException();
        }

        return libro;
    }

    public Libro obtenerLibroDisponible(String tituloDelLibro){

        Libro libroDisponible = catalogo.values().stream()
                        .filter(libro -> libro.getTitulo().equals(tituloDelLibro))
                        .filter(libro -> libro.getEstado() == Libro.Estado.DISPONIBLE)
                        .findFirst()
                        .orElse(null);

        if(libroDisponible == null){
            throw new LibroNoDisponibleException("El libro no se encuentra disponible");
        }

        return libroDisponible;
    }

}
