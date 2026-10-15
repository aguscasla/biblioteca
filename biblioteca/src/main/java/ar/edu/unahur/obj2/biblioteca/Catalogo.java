package ar.edu.unahur.obj2.biblioteca;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ar.edu.unahur.obj2.criteriosDeBusqueda.CriterioDeBusqueda;
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

    public List<Libro> librosFiltrados(CriterioDeBusqueda criteriosDeBusqueda){
        Objects.requireNonNull(criteriosDeBusqueda, "El criterio de busqueda no puede ser null");
        
        return catalogo.values().stream()
                .filter(libro -> criteriosDeBusqueda.coincide(libro))
                .toList();
    }

}
