package prestamos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.unahur.obj2.biblioteca.Biblioteca;
import ar.edu.unahur.obj2.libros.Libro;
import ar.edu.unahur.obj2.prestamos.Prestamo;

public class PrestamoTest {
    
    private Prestamo prestamo;
    private Biblioteca biblioteca;
    private Libro ejemplar;

    LocalDateTime fechaFija = LocalDateTime.of(2026, 9, 29, 12, 0, 0);
    ZoneId zonaHoraria = ZoneId.systemDefault();
    ZonedDateTime fechaZonificada = fechaFija.atZone(zonaHoraria);

    @BeforeEach 
    void setUp(){
        ejemplar = new Libro(null, null, null, 
            null, null, null);

        prestamo = new Prestamo(101, null, 
            ejemplar, LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 25), 
            5);

        biblioteca = new Biblioteca(Clock.fixed(fechaZonificada.toInstant(), zonaHoraria));
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
        prestamo.setFechaLimite(LocalDate.of(2026, 10, 1));

        assertFalse(prestamo.estaVencido(LocalDate.of(2026, 9, 29)));
    }

    @Test 
    void deberiaEstarFinalizado_SiSeDevuelve(){
        biblioteca.finalizarPrestamo(prestamo);

        assertEquals(Prestamo.Estado.FINALIZADO, prestamo.getEstado());
    }

}
