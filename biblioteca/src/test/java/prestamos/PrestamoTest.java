package prestamos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ar.edu.unahur.obj2.exceptions.FechaInvalidaException;
import ar.edu.unahur.obj2.libros.Libro;
import ar.edu.unahur.obj2.prestamos.Prestamo;
import ar.edu.unahur.obj2.usuarios.Usuario;

public class PrestamoTest {
    
    private Prestamo prestamo;
    private Libro ejemplar;
    private Usuario socio;

    @BeforeEach 
    void setUp(){
        ejemplar = new Libro("Clean Code", "Robert C. Martin", Libro.Categoria.ACADEMICO, 
            LocalDate.of(2014, 1, 1), 340, 
            Libro.Idioma.CASTELLANO);

        socio = new Usuario("Agustina", "Aguero", "4787883", 20, 
            "3755578920", "agus@gmail.com");

        prestamo = new Prestamo(101, socio, 
            ejemplar, LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 25), 
            5);

    }

    @Test
    void deberiaSerUnPrestamoEnCursoApenasSeInstancia(){
        assertEquals(Prestamo.Estado.EN_CURSO, prestamo.getEstado());
    }

    @Test 
    void deberiaEstarVencido_SiLaFechaActualSuperaLaFechaLimite(){
        assertTrue(prestamo.estaVencido(LocalDate.of(2026, 9, 29)));
    }

    @Test 
    void noDeberiaEstarVencido_SiAunNoHaPasadoLaFechaLimite(){
        Prestamo prestamo = new Prestamo(2, socio, ejemplar, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), 2);

        assertFalse(prestamo.estaVencido(LocalDate.of(2026, 9, 29)));
    }

    @Test 
    void deberiaEstarFinalizado_SiSeFinalizaElPrestamo(){
        prestamo.finalizarPrestamo();

        assertEquals(Prestamo.Estado.FINALIZADO, prestamo.getEstado());
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnIdNulo(){
        Executable act = () -> new Prestamo(null, socio, ejemplar, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnIdInvalido(){
        Executable act = () -> new Prestamo(0, socio, ejemplar, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnSocioNulo(){
        Executable act = () -> new Prestamo(2, null, ejemplar, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnEjemplarNulo(){
        Executable act = () -> new Prestamo(2, socio, null, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaFechaDeInicioNula(){
        Executable act = () -> new Prestamo(2, socio, ejemplar, null, 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaFechaDeInicioIgualALaFechaLimite(){
        Executable act = () -> new Prestamo(2, socio, ejemplar, LocalDate.of(2026, 10, 1), 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaFechaDeInicioPosteriorALaFechaLimite(){
        Executable act = () -> new Prestamo(2, socio, ejemplar, LocalDate.of(2026, 10, 20), 
            LocalDate.of(2026, 10, 1), 2);

        assertThrows(FechaInvalidaException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaFechaLimiteNula(){
        Executable act = () -> new Prestamo(2, socio, ejemplar, LocalDate.of(2026, 9, 20), 
            null, 2);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaCantidadDeRenovacionesNula(){
        Executable act = () -> new Prestamo(2, socio, ejemplar, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), null);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcion_SiSeInstanciaConUnaCantidadDeRenovacionesInvalida(){
        Executable act = () -> new Prestamo(2, socio, ejemplar, LocalDate.of(2026, 9, 20), 
            LocalDate.of(2026, 10, 1), -1);

        assertThrows(IllegalArgumentException.class, act);
    }

}
