package ar.edu.unahur.obj2.criteriosDeBusqueda;

import java.util.Objects;

import ar.edu.unahur.obj2.libros.Libro;

public interface CriterioDeBusqueda {

    Boolean coincide(Libro libro); 

    default CriterioDeBusqueda y(CriterioDeBusqueda otro) {
        Objects.requireNonNull(otro, "El criterio de busqueda no puede ser null");
        return libro -> coincide(libro) && otro.coincide(libro);
    }

    default CriterioDeBusqueda o(CriterioDeBusqueda otro) {
        Objects.requireNonNull(otro, "El criterio de busqueda no puede ser null");
        return libro -> coincide(libro) || otro.coincide(libro);
    }

    default CriterioDeBusqueda no() {
        return libro -> !coincide(libro);
    }

}