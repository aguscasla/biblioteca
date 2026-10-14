package ar.edu.unahur.obj2.criteriosDeBusqueda;

import java.util.Objects;

import ar.edu.unahur.obj2.libros.Libro;

public class BusquedaPorTitulo implements CriterioDeBusqueda{

    private String titulo;

    public BusquedaPorTitulo(String titulo) {
        this.titulo = Objects.requireNonNull(titulo, "El titulo es obligatorio");
    }
 
    @Override
    public Boolean coincide(Libro libro) {
        return libro.getTitulo().equals(titulo);
    }

}
