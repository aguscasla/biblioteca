package biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ar.edu.unahur.obj2.biblioteca.Catalogo;
import ar.edu.unahur.obj2.criteriosDeBusqueda.BusquedaPorAutor;
import ar.edu.unahur.obj2.criteriosDeBusqueda.BusquedaPorCategoria;
import ar.edu.unahur.obj2.criteriosDeBusqueda.BusquedaPorDisponibilidad;
import ar.edu.unahur.obj2.criteriosDeBusqueda.BusquedaPorIdioma;
import ar.edu.unahur.obj2.criteriosDeBusqueda.BusquedaPorTitulo;
import ar.edu.unahur.obj2.criteriosDeBusqueda.CriterioDeBusqueda;
import ar.edu.unahur.obj2.exceptions.LibroNoRegistradoException;
import ar.edu.unahur.obj2.exceptions.LibroYaRegistradoException;
import ar.edu.unahur.obj2.libros.Libro;

public class CatalogoTest {

    private Catalogo catalogo;
    private Libro elPrincipito;
    private Libro ficciones;
    private Libro elAleph;
    private Libro dune;
    private Libro cienAniosDeSoledad;

    @BeforeEach 
    void setUp(){
        catalogo = new Catalogo();

        elPrincipito = new Libro(1, "El Principito", "Antoine de Saint-Exupery", 
            Libro.Categoria.FANTASIA, 
            LocalDate.of(1943, 4, 1),
            47, Libro.Idioma.FRANCES); 

        ficciones = new Libro(2, "Ficciones", "Jorge Luis Borges",
            Libro.Categoria.LITERATURA,
            LocalDate.of(1944, 1, 1), 174, Libro.Idioma.CASTELLANO);

        elAleph = new Libro(3, "El Aleph", "Jorge Luis Borges",
            Libro.Categoria.LITERATURA,
            LocalDate.of(1949, 1, 1), 146, Libro.Idioma.CASTELLANO);

        dune = new Libro(4, "Dune", "Frank Herbert",
            Libro.Categoria.CIENCIA_FICCION,
            LocalDate.of(1965, 8, 1), 896, Libro.Idioma.INGLES);

        cienAniosDeSoledad = new Libro(5, "Cien años de soledad", "Gabriel Garcia Marquez",
            Libro.Categoria.LITERATURA,
            LocalDate.of(1967, 5, 30), 417, Libro.Idioma.CASTELLANO);

        catalogo.agregarLibro(elPrincipito);
        catalogo.agregarLibro(ficciones);
        catalogo.agregarLibro(elAleph);
        catalogo.agregarLibro(dune);
        catalogo.agregarLibro(cienAniosDeSoledad);

        dune.prestar();

        cienAniosDeSoledad.reparar();
    }

    @Test 
    void deberiaAgregarUnLibro(){
        Libro harryPotter = new Libro(6, "Harry Potter", "J.K. Rowling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        catalogo.agregarLibro(harryPotter);

        assertEquals(harryPotter, catalogo.obtenerLibro(6));
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeAgregaUnLibroNulo(){
        Executable act = () -> catalogo.agregarLibro(null);

        assertThrows(IllegalArgumentException.class, act);
    }

    @Test 
    void deberiaEliminarUnLibro(){
        catalogo.eliminarLibro(1);

        Executable act = () -> catalogo.obtenerLibro(1);

        assertThrows(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeEliminaUnLibroQueNoEstaEnElCatalogo(){
        Executable act = () -> catalogo.eliminarLibro(99);

        assertThrows(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaObtenerUnLibro(){
        assertEquals(elPrincipito, catalogo.obtenerLibro(1));
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiQuiereObtenerUnLibroQueNoEstaEnElCatalogo(){
        Executable act = () -> catalogo.obtenerLibro(99);

        assertThrowsExactly(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeAgregaUnLibroConIDQueYaSeEncuentraEnElCatalogo(){
        Executable act = () -> catalogo.agregarLibro(elPrincipito);

        assertThrows(LibroYaRegistradoException.class, act);
    }

    @Test 
    void deberiaFiltrarPorTitulo(){
        Set<Libro> librosEsperados = Set.of(elPrincipito);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(new BusquedaPorTitulo("El Principito")));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test
    void deberiaFiltrarPorAutor(){
        Set<Libro> librosEsperados = Set.of(ficciones, elAleph);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(new BusquedaPorAutor("Jorge Luis Borges")));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test
    void deberiaFiltrarPorCategoria(){
        Set<Libro> librosEsperados = Set.of(ficciones, elAleph, cienAniosDeSoledad);

        Set<Libro> librosObtenidos = Set.copyOf(
                catalogo.librosFiltrados(new BusquedaPorCategoria(Libro.Categoria.LITERATURA)));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test
    void deberiaFiltrarPorIdioma(){
        Set<Libro> librosEsperados = Set.of(ficciones, elAleph, cienAniosDeSoledad);

        Set<Libro> librosObtenidos = Set.copyOf(
                catalogo.librosFiltrados(new BusquedaPorIdioma(Libro.Idioma.CASTELLANO)));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test
    void deberiaFiltrarUnicamenteLosLibrosDisponibles(){
        Set<Libro> librosEsperados = Set.of(elPrincipito, ficciones, elAleph);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(new BusquedaPorDisponibilidad()));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test 
    void deberiaFiltrarLosLibrosQueCoincidenEnAutorEIdioma(){
        Set<Libro> librosEsperados = Set.of(ficciones, elAleph);
                
        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            new BusquedaPorAutor("Jorge Luis Borges").y(
                new BusquedaPorIdioma(Libro.Idioma.CASTELLANO)
            )
        ));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test 
    void deberiaFiltrarUnaColeccionVaciaCombinandoDosCriterios_SiNingunLibroCoincideEnAutorEIdioma(){
        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            new BusquedaPorAutor("Jorge Luis Borges").y(
                new BusquedaPorIdioma(Libro.Idioma.INGLES)
            )
        ));

        assertTrue(librosObtenidos.isEmpty());
    }

    @Test 
    void deberiaFiltrarLosLibrosQueCoincidenEnAutorIdiomaYCategoria(){
        Set<Libro> librosEsperados = Set.of(elPrincipito);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            new BusquedaPorAutor("Antoine de Saint-Exupery").y(
                new BusquedaPorIdioma(Libro.Idioma.FRANCES).y(
                    new BusquedaPorCategoria(Libro.Categoria.FANTASIA)
                )
            )
        ));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test 
    void deberiaFiltrarLosLibrosQueCoincidenEnAutorOTitulo(){
        Set<Libro> librosEsperados = Set.of(ficciones, dune);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            new BusquedaPorAutor("Frank Herbert").o(
                new BusquedaPorTitulo("Ficciones")
            )
        ));

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test 
    void deberiaFiltrarLosLibrosQueNoCoincidenEnIdioma(){
        Set<Libro> librosEsperados = Set.of(elPrincipito, dune);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            new BusquedaPorIdioma(Libro.Idioma.CASTELLANO).no()
            )
        );

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test 
    void deberiaFiltrarLosLibrosQueCoincidenEnIdiomaOCategoriaPeroNoEnAutor(){
        Set<Libro> librosEsperados = Set.of(cienAniosDeSoledad, dune);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            (new BusquedaPorIdioma(Libro.Idioma.CASTELLANO).o(
                new BusquedaPorCategoria(Libro.Categoria.CIENCIA_FICCION)).y(
                    new BusquedaPorAutor("Jorge Luis Borges").no()
                )
            )
            )
        );

        assertEquals(librosEsperados, librosObtenidos);
    }

    @Test 
    void deberiaFiltrarUnaColeccionVacia_SiNingunLibroCoincideEnIdiomaOTitulo(){
        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            new BusquedaPorIdioma(Libro.Idioma.PORTUGUES).o(
                new BusquedaPorTitulo("Harry Potter, El Caliz de Fuego")
            )
            )
        );

        assertTrue(librosObtenidos.isEmpty());
    }

    @Test 
    void deberiaFiltrarLosLibrosQueNoCumplenAmbosCriteriosDeAutorEIdioma(){
        Set<Libro> librosEsperados = Set.of(elPrincipito, dune, cienAniosDeSoledad);

        Set<Libro> librosObtenidos = Set.copyOf(catalogo.librosFiltrados(
            (new BusquedaPorIdioma(Libro.Idioma.CASTELLANO).y(
                new BusquedaPorAutor("Jorge Luis Borges")).no()
            )
            )
        );

        assertEquals(librosEsperados, librosObtenidos);
    }

}