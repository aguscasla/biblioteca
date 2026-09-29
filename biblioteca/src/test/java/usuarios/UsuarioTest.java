package usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.unahur.obj2.biblioteca.Biblioteca;
import ar.edu.unahur.obj2.usuarios.Usuario;

public class UsuarioTest {

    private Usuario usuario;
    private Biblioteca biblioteca;

    @BeforeEach 
    void setUp(){
        usuario = new Usuario("Agustina", "Aguero", "46787883", 
            20, "3755578920", "agusagueroo89@gmail.com");

        biblioteca = new Biblioteca(Clock.systemDefaultZone());

    }

    @Test 
    void deberiaEstarNoRegistradoAlMomentoDeInstanciarse(){
        assertEquals(Usuario.Estado.NO_REGISTRADO, usuario.getEstado());
    }

}
