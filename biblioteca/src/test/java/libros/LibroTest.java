package libros;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.function.Executable;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.unahur.obj2.exceptions.LibroInhabilitadoException;
import ar.edu.unahur.obj2.libros.Libro;

public class LibroTest {

    private Libro libro;

    @BeforeEach 
    void setUp(){
        libro = new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            340, Libro.Idioma.CASTELLANO);

    }
    
    @Test
    void deberiaEstarDisponibleAlInstanciarlo(){
        Libro libro = new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            340, Libro.Idioma.CASTELLANO);

        assertEquals(Libro.Estado.DISPONIBLE, libro.getEstado());
    }

    @Test 
    void deberiaEstarPrestado_SiSePresta(){
        libro.prestar();

        assertEquals(Libro.Estado.PRESTADO, libro.getEstado());
    }

    @Test 
    void deberiaEstarInhabilitado_SiSeInhabilita(){
        libro.inhabilitar();

        assertEquals(Libro.Estado.INHABILITADO, libro.getEstado());
    }

    @Test 
    void deberiaEstarEnReparacion_SiSeRepara(){
        libro.reparar();

        assertEquals(Libro.Estado.EN_REPARACION, libro.getEstado());
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeHabilitaUnLibroConEstadoInhabilitado(){
        libro.inhabilitar();

        Executable act = () -> libro.habilitar();

        assertThrows(LibroInhabilitadoException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnTituloNulo(){
        Executable act = () -> new Libro(null, "Robert C. Martin", Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            340, Libro.Idioma.CASTELLANO);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnAutorNulo(){
        Executable act = () -> new Libro("Clean Code", null, Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            340, Libro.Idioma.CASTELLANO);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaCategoriaNula(){
        Executable act = () -> new Libro("Clean Code", "Robert C. Martin", null, LocalDate.of(2014, 1, 1), 
            340, Libro.Idioma.CASTELLANO);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaFechaDePublicacionNula(){
        Executable act = () -> new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, null, 
            340, Libro.Idioma.CASTELLANO);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaCantidadDePaginasNula(){
        Executable act = () -> new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            null, Libro.Idioma.CASTELLANO);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaCantidadDePaginasInvalida(){
        Executable act = () -> new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            0, Libro.Idioma.CASTELLANO);

        assertThrows(IllegalArgumentException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnIdiomaNulo(){
        Executable act = () -> new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, LocalDate.of(2014, 1, 1), 
            340, null);

        assertThrows(NullPointerException.class, act);
    }

}
