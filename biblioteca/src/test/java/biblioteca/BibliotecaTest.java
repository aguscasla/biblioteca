package biblioteca;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ar.edu.unahur.obj2.biblioteca.Biblioteca;
import ar.edu.unahur.obj2.exceptions.LibroNoDisponibleException;
import ar.edu.unahur.obj2.exceptions.LibroNoRegistradoException;
import ar.edu.unahur.obj2.exceptions.PrestamoNoEncontradoException;
import ar.edu.unahur.obj2.exceptions.PrestamoNoRenovableException;
import ar.edu.unahur.obj2.exceptions.PrestamoSinRenovacionesDisponiblesException;
import ar.edu.unahur.obj2.exceptions.PrestamoVencidoException;
import ar.edu.unahur.obj2.exceptions.UsuarioInactivoException;
import ar.edu.unahur.obj2.exceptions.UsuarioInhabilitadoException;
import ar.edu.unahur.obj2.exceptions.UsuarioNoReasociableException;
import ar.edu.unahur.obj2.exceptions.UsuarioNoRegistradoException;
import ar.edu.unahur.obj2.exceptions.UsuarioSinPrestamosDisponiblesException;
import ar.edu.unahur.obj2.exceptions.UsuarioYaRegistradoException;
import ar.edu.unahur.obj2.libros.Libro;
import ar.edu.unahur.obj2.prestamos.Prestamo;
import ar.edu.unahur.obj2.usuarios.Usuario;

public class BibliotecaTest {

    private Usuario usuario;
    private Biblioteca biblioteca;
    private Libro libro;

    ZoneId zona = ZoneId.of("America/Argentina/Buenos_Aires");
    LocalDate fechaPrueba = LocalDate.of(2026, 9, 29);

    Clock relojFijo = Clock.fixed(
        fechaPrueba.atStartOfDay(zona).toInstant(),
        zona);
    
    @BeforeEach 
    void setUp(){
        usuario = new Usuario("Agustina", "Aguero", "46787883", 
            20, "3755578920", "agusagueroo89@gmail.com");
        
        biblioteca = new Biblioteca(relojFijo);

        libro = new Libro(1, "Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, 
            LocalDate.of(2008, 8, 1), 
            644, Libro.Idioma.INGLES);

        biblioteca.agregarLibroAlCatalogo(libro);
    }

    @Test 
    void deberiaAsociarUnUsarioNoRegistrado(){
        biblioteca.asociarUsuario(usuario, 2);

        assertTrue(biblioteca.getSocios().containsValue(usuario));
    }

    @Test 
    void noDeberiaAsociarAUnUsuario_SiYaFueAsociado(){
        biblioteca.asociarUsuario(usuario, 1);

        assertThrows(UsuarioYaRegistradoException.class, () -> biblioteca.asociarUsuario(usuario, 2));
    }

    @Test 
    void deberiaCambiarElEstadoDeUnUsuario_SiSeLoDesasocia(){
        biblioteca.asociarUsuario(usuario, 2);
        biblioteca.desasociarUsuario("46787883");

        assertEquals(Usuario.Estado.BAJA_VOLUNTARIA, biblioteca.obtenerUsuario("46787883").getEstado());
    }

    @Test 
    void noDeberiaDesasociarUnUsuario_SiEsteNoEstaRegistrado(){
        assertThrows(UsuarioNoRegistradoException.class, () -> biblioteca.desasociarUsuario("46787883"));
    }

    @Test 
    void noDeberiaDesasociarUnUsuario_SiNoSeEncuentraActivo(){
        biblioteca.asociarUsuario(usuario, 2);
        usuario.darBajaVoluntaria();;

        assertThrows(UsuarioInactivoException.class, () -> biblioteca.desasociarUsuario("46787883"));
    }

    @Test 
    void deberiaReasociarAUnUsuario(){
        biblioteca.asociarUsuario(usuario, 2);
        biblioteca.desasociarUsuario("46787883");
        biblioteca.reasociarUsuario("46787883");

        assertEquals(Usuario.Estado.ACTIVO, biblioteca.obtenerUsuario("46787883").getEstado());
    }

    @Test 
    void noDeberiaReasociarUnUsuario_SiNoSeEncuentraRegistrado(){
        assertThrows(UsuarioNoRegistradoException.class, () -> biblioteca.reasociarUsuario("46787883"));
    }
    
    @Test 
    void noDeberiaReasociarUnUsuario_SiNoSeEncuentraConBajaVoluntaria(){
        biblioteca.asociarUsuario(usuario, 3);
        assertThrows(UsuarioNoReasociableException.class, () -> biblioteca.reasociarUsuario("46787883"));
    }

    @Test 
    void deberiaInhabilitarUnUsuario(){
        biblioteca.asociarUsuario(usuario, 3);
        biblioteca.inhabilitarUsuario("46787883");

        assertEquals(Usuario.Estado.INHABILITADO, biblioteca.obtenerUsuario("46787883").getEstado());
    }

    @Test 
    void noDeberiaInhabilitarUnUsuario_SiYaSeEncuentraInhabilitado(){
        biblioteca.asociarUsuario(usuario, 3);
        usuario.inhabilitar();;

        assertThrows(UsuarioInhabilitadoException.class, () -> biblioteca.inhabilitarUsuario("46787883"));
    }

    @Test 
    void noDeberiaInhabilitarUnUsuario_SiElUsuarioNoEstaRegistrado(){
        assertThrows(UsuarioNoRegistradoException.class, () -> biblioteca.inhabilitarUsuario("46787883"));
    }

    @Test 
    void deberiaObtenerUnUsuarioRegistrado(){
        biblioteca.asociarUsuario(usuario, 2);

        assertEquals(usuario, biblioteca.obtenerUsuario("46787883"));
    }

    @Test 
    void unUsuarioInfringeLasNormas_SiNoHaDevueltoUnEjemplarLuegoDeUnaSemanaDeSuFechaLimite(){
        biblioteca.asociarUsuario(usuario, 3);
        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 20), 0);

        assertTrue(biblioteca.estaInfringiendoLasNormas(usuario));
    }

    @Test 
    void unUsuarioNoInfringeLasNormas_SiDevuelveSusPrestamosATiempoYNoConservaNingunLibro(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 25), 0);

        biblioteca.finalizarPrestamo(1);

        assertFalse(biblioteca.estaInfringiendoLasNormas(usuario));
    }

    @Test 
    void unUsuarioInfringeLasNormas_SiDevolvioUnEjemplarVencidoEnVariasOcasiones(){
        biblioteca.asociarUsuario(usuario, 3);
        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 24), 0);

        biblioteca.finalizarPrestamo(1);

        biblioteca.otorgarPrestamo(2, "46787883", "Clean Code", LocalDate.of(2026, 9, 5), 
            LocalDate.of(2026, 9, 23), 0);

        biblioteca.finalizarPrestamo(1);

        biblioteca.otorgarPrestamo(3, "46787883", "Clean Code", LocalDate.of(2026, 9, 8), 
            LocalDate.of(2026, 9, 28), 0);

        biblioteca.finalizarPrestamo(1);

        assertTrue(biblioteca.estaInfringiendoLasNormas(usuario));
    }

    @Test 
    void deberiaOtorgarUnPrestamoAUnUsuarioRegistrado(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 9, 30), 2);
        
        Prestamo prestamo = biblioteca.obtenerPrestamo(libro);

        assertTrue(biblioteca.prestamosEnCursoDelUsuario(usuario).contains(prestamo));
    }

    @Test 
    void unPrestamoDeberiaEstarEnCurso_SiSeOtorgaAUnUsuario(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 30), 2);

        Prestamo prestamo = biblioteca.obtenerPrestamo(libro);

        assertEquals(Prestamo.Estado.EN_CURSO, prestamo.getEstado());
    }

    @Test 
    void deberiaFinalizarseUnPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2);

        Prestamo prestamo = biblioteca.obtenerPrestamo(libro);
        biblioteca.finalizarPrestamo(1);

        assertEquals(Prestamo.Estado.FINALIZADO, prestamo.getEstado());
    }

    @Test 
    void unEjemplarFiguraDisponible_SiSeFinalizaSuPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2);

        biblioteca.finalizarPrestamo(1);

        assertEquals(Libro.Estado.DISPONIBLE, libro.getEstado());
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElEJemplarNoSeEncuentraDisponible(){
        biblioteca.asociarUsuario(usuario, 3);
        libro.reparar();

        assertThrows(LibroNoDisponibleException.class, () -> 
            biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiUnEJemplarNoExiste(){
        biblioteca.asociarUsuario(usuario, 3);

        assertThrows(LibroNoDisponibleException.class, () -> 
            biblioteca.otorgarPrestamo(1, "46787883", null, LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElUsuarioNoEstaActivo(){
        biblioteca.asociarUsuario(usuario, 3);
        biblioteca.desasociarUsuario("46787883");

        assertThrows(UsuarioInactivoException.class, () -> 
            biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElUsuarioAlcanzoSuLimiteDePrestamos(){
        Libro libro2 = new Libro(2, "Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        biblioteca.agregarLibroAlCatalogo(libro2);
        biblioteca.asociarUsuario(usuario, 1);

        biblioteca.otorgarPrestamo(2, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2);

        assertThrows(UsuarioSinPrestamosDisponiblesException.class, () -> 
            biblioteca.otorgarPrestamo(1, "46787883", "Harry Potter", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));       
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElUsuarioInfringeLasNormas(){
        Libro libro2 = new Libro(2, "Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        biblioteca.agregarLibroAlCatalogo(libro2);
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 20), 0);

        assertThrows(UsuarioInhabilitadoException.class, () -> 
            biblioteca.otorgarPrestamo(1, "46787883", "Harry Potter", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 0));
    }

    @Test 
    void deberiaDescontarUnaRenovacionDeUnPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 9, 30), 3);

        biblioteca.renovarPrestamo(1);
        Prestamo prestamo = biblioteca.obtenerPrestamo(libro);

        assertEquals(2, prestamo.getCantidadDeRenovaciones());
    }

    @Test 
    void deberiaAlargarUnaSemanaLaFechaLimiteAlRenovarElPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 9, 30), 3);

        biblioteca.renovarPrestamo(1);
        Prestamo prestamo = biblioteca.obtenerPrestamo(libro);

        assertEquals(LocalDate.of(2026, 10, 7), prestamo.getFechaLimite());
    }

    @Test 
    void noDeberiaAsociarUnUsuario_SiLePasanUnUsuarioConValorNulo(){
        assertThrows(IllegalArgumentException.class, () -> biblioteca.asociarUsuario(null, 1));
    }

    @Test 
    void noDeberiaObtenerUnUsuario_SiElUsuarioNoEstaRegistrado(){
        assertThrows(UsuarioNoRegistradoException.class, () -> biblioteca.obtenerUsuario("46787883"));
    }

    @Test 
    void noDeberiaDarDeBajaANingunUsuario_SiNingunoInclumpleLasNormasAlEvaluarlo(){
        Usuario usuario2 = new Usuario("Edu", "Aguero", "12566044", 69, 
            "3755578920", "ed@gmail.com");
        Libro libro2 = new Libro(2, "Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        biblioteca.agregarLibroAlCatalogo(libro2);
        biblioteca.asociarUsuario(usuario, 2);
        biblioteca.asociarUsuario(usuario2, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 1), 
        LocalDate.of(2026, 9, 25), 0);

        biblioteca.otorgarPrestamo(2, "12566044", "Harry Potter", LocalDate.of(2026, 9, 1), 
        LocalDate.of(2026, 9, 25), 0);

        biblioteca.finalizarPrestamo(1);
        biblioteca.finalizarPrestamo(2);

        biblioteca.evaluarUsuarios();
        
        assertAll(
            () -> assertEquals(Usuario.Estado.ACTIVO, biblioteca.obtenerUsuario("46787883").getEstado()),
            () -> assertEquals(Usuario.Estado.ACTIVO, biblioteca.obtenerUsuario("12566044").getEstado())
        );
    }

    @Test 
    void deberiaInhabilitarAUnUsuario_SiEsteNoCumpleLasNormasAlEvaluarlo(){
        Usuario usuario2 = new Usuario("Edu", "Aguero", "12566044", 69, 
            "3755578920", "ed@gmail.com");
        Libro libro2 = new Libro(2, "Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        biblioteca.agregarLibroAlCatalogo(libro2);
        biblioteca.asociarUsuario(usuario, 2);
        biblioteca.asociarUsuario(usuario2, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 20), 0);

        biblioteca.otorgarPrestamo(2, "12566044", "Harry Potter", LocalDate.of(2026, 9, 1), 
        LocalDate.of(2026, 9, 25), 0);

        biblioteca.finalizarPrestamo(2);

        biblioteca.evaluarUsuarios();

        assertEquals(Usuario.Estado.INHABILITADO, biblioteca.obtenerUsuario("46787883").getEstado());
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiNoSeEncuentraEnCurso(){
        biblioteca.asociarUsuario(usuario, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 10, 5), 2);

        biblioteca.finalizarPrestamo(1);

        assertThrows(PrestamoNoEncontradoException.class, () -> biblioteca.renovarPrestamo(1));
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiEsteSeEncuentraVencido(){
        biblioteca.asociarUsuario(usuario, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 9, 28), 2);

        assertThrows(PrestamoVencidoException.class, () -> biblioteca.renovarPrestamo(1));
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiLaFechaActualEstaAMasDeDosDiasDeLaFechaLimite(){
        biblioteca.asociarUsuario(usuario, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 10, 3), 2);

        assertThrows(PrestamoNoRenovableException.class, () -> biblioteca.renovarPrestamo(1));
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiAlcanzoLaCantidadDeRenovacionesDisponibles(){
        biblioteca.asociarUsuario(usuario, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 9, 30), 0);

        assertThrows(PrestamoSinRenovacionesDisponiblesException.class, () -> biblioteca.renovarPrestamo(1));
    }

    @Test 
    void deberiaLanzarUnaExcepcionAlBuscarUnLibroDelCatalogo_SiSeEliminaElLibroDelCatalogo(){
        biblioteca.eliminarLibroDelCatalogo(1);

        Executable act = () -> biblioteca.obtenerLibroDelCatalogo(1);

        assertThrows(LibroNoRegistradoException.class, act);
    }

    @Test 
    void deberiaPoderAgregarUnLibroAlCatalogo(){
        Libro libro2 = new Libro(2, "Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES); 

        biblioteca.agregarLibroAlCatalogo(libro2);

        assertEquals(libro2, biblioteca.obtenerLibroDelCatalogo(2));
    }

    @Test 
    void deberiaPoderObtenerDelCatalogoUnLibroQueEstaDisponible(){
        assertEquals(libro, biblioteca.obtenerLibroDisponible("Clean Code"));
    }

    @Test 
    void deberiaLanzarUnaExcepcionAlBuscarUnLibroDisponible_SiNoHayNingunLibroDisponibleEnElCatalogo(){
        biblioteca.asociarUsuario(usuario, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", 
            LocalDate.of(2026, 9, 10), LocalDate.of(2026, 10, 1), 2);

        Executable act = () -> biblioteca.obtenerLibroDisponible("Clean Code");

        assertThrows(LibroNoDisponibleException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExepcionAlEliminarUnLibro_SiElLibroSeEncuentraPrestado(){
        biblioteca.asociarUsuario(usuario, 2);

        biblioteca.otorgarPrestamo(1, "46787883", "Clean Code", 
            LocalDate.of(2026, 9, 10), LocalDate.of(2026, 10, 1), 2);

        Executable act = () -> biblioteca.eliminarLibroDelCatalogo(1);

        assertThrows(LibroNoDisponibleException.class, act);
    }
}
