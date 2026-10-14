package ar.edu.unahur.obj2.criteriosDeBusqueda;

import ar.edu.unahur.obj2.libros.Libro;

public class BusquedaPorDisponibilidad implements CriterioDeBusqueda{

    @Override
    public Boolean coincide(Libro libro) {
        return libro.getEstado() == Libro.Estado.DISPONIBLE;
    }

}
