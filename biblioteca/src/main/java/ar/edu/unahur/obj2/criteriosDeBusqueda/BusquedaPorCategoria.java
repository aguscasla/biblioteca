package ar.edu.unahur.obj2.criteriosDeBusqueda;

import ar.edu.unahur.obj2.libros.Libro;

public class BusquedaPorCategoria implements CriterioDeBusqueda{

    private Libro.Categoria categoria;

    public BusquedaPorCategoria(Libro.Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public Boolean coincide(Libro libro) {
        return libro.getCategoria().equals(categoria);
    }

}
