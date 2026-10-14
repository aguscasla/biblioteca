package ar.edu.unahur.obj2.criteriosDeBusqueda;

import java.util.Objects;

import ar.edu.unahur.obj2.libros.Libro;

public class BusquedaPorAutor implements CriterioDeBusqueda{

    private String autor;

    public BusquedaPorAutor(String autor) {
        this.autor = Objects.requireNonNull(autor, "El autor es obligatorio");
    }

    @Override
    public Boolean coincide(Libro libro) {
        return libro.getAutor().equals(autor);
    }

}
