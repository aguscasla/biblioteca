package ar.edu.unahur.obj2.criteriosDeBusqueda;

import ar.edu.unahur.obj2.libros.Libro;

public class BusquedaPorIdioma implements CriterioDeBusqueda{

    private Libro.Idioma idioma;

    public BusquedaPorIdioma(Libro.Idioma idioma) {
        this.idioma = idioma;
    }

    @Override
    public Boolean coincide(Libro libro) {
        return libro.getIdioma().equals(idioma);
    }

}
