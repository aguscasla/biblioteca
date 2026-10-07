package biblioteca;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.unahur.obj2.biblioteca.Biblioteca;
import ar.edu.unahur.obj2.exceptions.LibroInexistenteException;
import ar.edu.unahur.obj2.exceptions.LibroNoDisponibleException;
import ar.edu.unahur.obj2.exceptions.PrestamoFinalizadoException;
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

    LocalDateTime fechaFija = LocalDateTime.of(2026, 9, 29, 12, 0, 0);
    ZoneId zonaHoraria = ZoneId.systemDefault();
    ZonedDateTime fechaZonificada = fechaFija.atZone(zonaHoraria);
    
    @BeforeEach 
    void setUp(){
        usuario = new Usuario("Agustina", "Aguero", "46787883", 
            20, "3755578920", "agusagueroo89@gmail.com");
        
        biblioteca = new Biblioteca(Clock.fixed(fechaZonificada.toInstant(), zonaHoraria));

        libro = new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, 
            LocalDate.of(2008, 8, 1), 
            644, Libro.Idioma.INGLES);
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
        usuario.setEstado(Usuario.Estado.BAJA_VOLUNTARIA);

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
        usuario.setEstado(Usuario.Estado.INHABILITADO);

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
        biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 20), 0);

        assertTrue(biblioteca.estaInfringiendoLasNormas(usuario));
    }

    @Test 
    void unUsuarioNoInfringeLasNormas_SiDevuelveSusPrestamosATiempoYNoConservaNingunLibro(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 25), 0);

        biblioteca.finalizarPrestamo(prestamo);

        assertFalse(biblioteca.estaInfringiendoLasNormas(usuario));
    }

    @Test 
    void unUsuarioInfringeLasNormas_SiDevolvioUnEjemplarVencidoEnVariasOcasiones(){
        biblioteca.asociarUsuario(usuario, 3);
        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 24), 0);

        biblioteca.finalizarPrestamo(prestamo);

        Prestamo prestamo2 = biblioteca.otorgarPrestamo(2, usuario, libro, LocalDate.of(2026, 9, 5), 
            LocalDate.of(2026, 9, 23), 0);

        biblioteca.finalizarPrestamo(prestamo2);

        Prestamo prestamo3 = biblioteca.otorgarPrestamo(3, usuario, libro, LocalDate.of(2026, 9, 8), 
            LocalDate.of(2026, 9, 28), 0);

        biblioteca.finalizarPrestamo(prestamo3);

        assertTrue(biblioteca.estaInfringiendoLasNormas(usuario));
    }

    @Test 
    void deberiaOtorgarUnPrestamoAUnUsuarioRegistrado(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 9, 30), 2);

        assertTrue(biblioteca.prestamosEnCursoDelUsuario(usuario).contains(prestamo));
    }

    @Test 
    void unPrestamoDeberiaEstarEnCurso_SiSeOtorgaAUnUsuario(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 30), 2);

        assertEquals(Prestamo.Estado.EN_CURSO, prestamo.getEstado());
    }

    @Test 
    void deberiaFinalizarseUnPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2);

        biblioteca.finalizarPrestamo(prestamo);

        assertEquals(Prestamo.Estado.FINALIZADO, prestamo.getEstado());
    }

    @Test 
    void unEjemplarFiguraDisponible_SiSeFinalizaSuPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2);

        biblioteca.finalizarPrestamo(prestamo);

        assertEquals(Libro.Estado.DISPONIBLE, libro.getEstado());
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElEJemplarNoSeEncuentraDisponible(){
        biblioteca.asociarUsuario(usuario, 3);
        libro.cambiarEstado(Libro.Estado.EN_REPARACION);

        assertThrows(LibroNoDisponibleException.class, () -> 
            biblioteca.otorgarPrestamo(1, usuario, libro,LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiUnEJemplarNoExiste(){
        biblioteca.asociarUsuario(usuario, 3);

        assertThrows(LibroInexistenteException.class, () -> 
            biblioteca.otorgarPrestamo(1, usuario, null,LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElUsuarioNoEstaActivo(){
        biblioteca.asociarUsuario(usuario, 3);
        biblioteca.desasociarUsuario("46787883");

        assertThrows(UsuarioInactivoException.class, () -> 
            biblioteca.otorgarPrestamo(1, usuario, libro,LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElUsuarioAlcanzoSuLimiteDePrestamos(){
        biblioteca.asociarUsuario(usuario, 1);

        biblioteca.otorgarPrestamo(2, usuario, libro,LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2);

        assertThrows(UsuarioSinPrestamosDisponiblesException.class, () -> 
            biblioteca.otorgarPrestamo(1, usuario, libro,LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 2));
    }

    @Test 
    void noDeberiaOtorgarUnPrestamo_SiElUsuarioInfringeLasNormas(){
        biblioteca.asociarUsuario(usuario, 3);

        biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 20), 0);

        assertThrows(UsuarioInhabilitadoException.class, () -> 
            biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 10, 25), 0));
    }

    @Test 
    void deberiaDescontarUnaRenovacionDeUnPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 9, 30), 3);

        biblioteca.renovarPrestamo(prestamo);

        assertEquals(2, prestamo.getCantidadDeRenovaciones());
    }

    @Test 
    void deberiaAlargarUnaSemanaLaFechaLimiteAlRenovarElPrestamo(){
        biblioteca.asociarUsuario(usuario, 3);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 10), 
            LocalDate.of(2026, 9, 30), 3);

        biblioteca.renovarPrestamo(prestamo);

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
        Libro libro2 = new Libro("Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        biblioteca.asociarUsuario(usuario, 2);
        biblioteca.asociarUsuario(usuario2, 2);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 1), 
        LocalDate.of(2026, 9, 25), 0);

        Prestamo prestamo2 = biblioteca.otorgarPrestamo(2, usuario2, libro2, LocalDate.of(2026, 9, 1), 
        LocalDate.of(2026, 9, 25), 0);

        biblioteca.finalizarPrestamo(prestamo);
        biblioteca.finalizarPrestamo(prestamo2);

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
        Libro libro2 = new Libro("Harry Potter", "J.K. Rolling", Libro.Categoria.FANTASIA, 
            LocalDate.of(1980, 5, 14), 232, Libro.Idioma.INGLES);

        biblioteca.asociarUsuario(usuario, 2);
        biblioteca.asociarUsuario(usuario2, 2);

        biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 1), 
            LocalDate.of(2026, 9, 20), 0);

        Prestamo prestamo2 = biblioteca.otorgarPrestamo(2, usuario2, libro2, LocalDate.of(2026, 9, 1), 
        LocalDate.of(2026, 9, 25), 0);

        biblioteca.finalizarPrestamo(prestamo2);

        biblioteca.evaluarUsuarios();

        assertEquals(Usuario.Estado.INHABILITADO, biblioteca.obtenerUsuario("46787883").getEstado());
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiNoSeEncuentraEnCurso(){
        biblioteca.asociarUsuario(usuario, 2);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 10, 5), 2);

        biblioteca.finalizarPrestamo(prestamo);

        assertThrows(PrestamoFinalizadoException.class, () -> biblioteca.renovarPrestamo(prestamo));
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiEsteSeEncuentraVencido(){
        biblioteca.asociarUsuario(usuario, 2);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 9, 28), 2);

        assertThrows(PrestamoVencidoException.class, () -> biblioteca.renovarPrestamo(prestamo));
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiLaFechaActualEstaAMasDeDosDiasDeLaFechaLimite(){
        biblioteca.asociarUsuario(usuario, 2);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 10, 3), 2);

        assertThrows(PrestamoNoRenovableException.class, () -> biblioteca.renovarPrestamo(prestamo));
    }

    @Test 
    void noDeberiaRenovarUnPrestamo_SiAlcanzoLaCantidadDeRenovacionesDisponibles(){
        biblioteca.asociarUsuario(usuario, 2);

        Prestamo prestamo = biblioteca.otorgarPrestamo(1, usuario, libro, LocalDate.of(2026, 9, 15),
            LocalDate.of(2026, 9, 30), 1);

        biblioteca.renovarPrestamo(prestamo);

        fechaFija = LocalDateTime.of(2026, 10, 5, 12, 0, 0);
        ZonedDateTime fechaZonificada = fechaFija.atZone(zonaHoraria);
        biblioteca.setReloj(Clock.fixed(fechaZonificada.toInstant(), zonaHoraria));

        assertThrows(PrestamoSinRenovacionesDisponiblesException.class, () -> biblioteca.renovarPrestamo(prestamo));
    }
}
