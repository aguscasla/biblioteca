package biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ar.edu.unahur.obj2.biblioteca.Catalogo;
import ar.edu.unahur.obj2.exceptions.LibroNoDisponibleException;
import ar.edu.unahur.obj2.exceptions.LibroNoRegistradoException;
import ar.edu.unahur.obj2.libros.Libro;

public class CatalogoTest {

    private Catalogo catalogo;
    private Libro libro;

    @BeforeEach 
    void setUp(){
        catalogo = new Catalogo();
        libro = new Libro("El Principito", "Antonie de Saint-Exupery", Libro.Categoria.FANTASIA, 
            LocalDate.of(1943, 4, 1),
            47, Libro.Idioma.FRANCES);

        catalogo.agregarLibro(libro);
    }

    @Test 
    void deberiaAgregarUnLibro(){
        Libro libro2 = new Libro("Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES); 

        catalogo.agregarLibro(libro2);

        assertEquals(libro2, catalogo.obtenerLibro(libro2.getId()));
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeAgregaUnLibroNulo(){
        Executable act = () -> catalogo.agregarLibro(null);

        assertThrows(IllegalArgumentException.class, act);
    }

    @Test 
    void deberiaEliminarUnLibro(){
        catalogo.eliminarLibro(libro.getId());

        Executable act = () -> catalogo.obtenerLibro(libro.getId());

        assertThrows(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeEliminaUnLibroQueNoEstaEnElCatalogo(){
        Executable act = () -> catalogo.eliminarLibro(0);

        assertThrows(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaObtenerUnLibro(){
        assertEquals(libro, catalogo.obtenerLibro(libro.getId()));
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiQuiereObtenerUnLibroQueNoEstaEnElCatalogo(){
        Executable act = () -> catalogo.obtenerLibro(0);

        assertThrowsExactly(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaObtenerUnLibroDisponible(){
        assertEquals(libro, catalogo.obtenerLibroDisponible("El Principito"));
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiNoHayNingunLibroDisponible(){
        libro.prestar();

        Executable act = () -> catalogo.obtenerLibroDisponible("El Principito");

        assertThrows(LibroNoDisponibleException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiNoHayNingunLibroQueConcidaConElTitulo(){
        Executable act = () -> catalogo.obtenerLibroDisponible("Harry Potter");

        assertThrows(LibroNoDisponibleException.class, act);
    }

}
