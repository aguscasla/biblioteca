package ar.edu.unahur.obj2.criteriosDeBusqueda;

import ar.edu.unahur.obj2.libros.Libro;

public interface CriterioDeBusqueda {

    Boolean coincide(Libro libro); 

    default CriterioDeBusqueda y(CriterioDeBusqueda otro) {
        return libro -> coincide(libro) && otro.coincide(libro);
    }

    default CriterioDeBusqueda o(CriterioDeBusqueda otro) {
        return libro -> coincide(libro) || otro.coincide(libro);
    }

    default CriterioDeBusqueda no() {
        return libro -> !coincide(libro);
    }

}