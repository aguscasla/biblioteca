package usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.unahur.obj2.usuarios.Usuario;

public class UsuarioTest {

    private Usuario usuario;

    @BeforeEach 
    void setUp(){
        usuario = new Usuario("Agustina", "Aguero", "46787883", 
            20, "3755578920", "agusagueroo89@gmail.com");
    }

    @Test 
    void deberiaEstarNoRegistradoAlMomentoDeInstanciarse(){
        assertEquals(Usuario.Estado.NO_REGISTRADO, usuario.getEstado());
    }

    @Test 
    void deberiaCambiarElEstadoAActivo_SiSeDaDeAlta(){
        usuario.darDeAlta();

        assertEquals(Usuario.Estado.ACTIVO, usuario.getEstado());
    }

    @Test 
    void deberiaCambiarElEstadoABajaVoluntaria_SiSeDaDeBajaVoluntaria(){
        usuario.darBajaVoluntaria();

        assertEquals(Usuario.Estado.BAJA_VOLUNTARIA, usuario.getEstado());
    }

    @Test 
    void deberiaCambiarElEstadoAInhabilitado_SiSeInhabilita(){
        usuario.inhabilitar();

        assertEquals(Usuario.Estado.INHABILITADO, usuario.getEstado());
    }

    @Test 
    void deberiaTenerUnLimiteDePrestamosEnCeroAlInstanciar(){
        assertEquals(0, usuario.getLimiteDePrestamos());
    }

    @Test
    void deberiaLanzarUnExcepcion_SiSePasaUnNombreConValorNulo(){
        assertThrows(NullPointerException.class, () -> new Usuario(null, "Aguero", 
            "46787883", 20, 
            "3755578920", "agus@gmail.com"));
    }

    @Test
    void deberiaLanzarUnExcepcion_SiSePasaUnApellidoConValorNulo(){
        assertThrows(NullPointerException.class, () -> new Usuario("Agus", null, 
            "46787883", 20, 
            "3755578920", "agus@gmail.com"));
    }

    @Test
    void deberiaLanzarUnExcepcion_SiSePasaUnDNIConValorNulo(){
        assertThrows(NullPointerException.class, () -> new Usuario("Agus", "Aguero", 
            null, 20, 
            "3755578920", "agus@gmail.com"));
    }
    
    @Test
    void deberiaLanzarUnExcepcion_SiSePasaUnaEdadConValorNulo(){
        assertThrows(NullPointerException.class, () -> new Usuario("Agus", "Aguero", 
            "46787883", null, 
            "3755578920", "agus@gmail.com"));
    }

    @Test
    void deberiaLanzarUnExcepcion_SiSePasaUnTelefonoConValorNulo(){
        assertThrows(NullPointerException.class, () -> new Usuario("Agus", "Aguero", 
            "46787883", 20, 
            null, "agus@gmail.com"));
    }

    @Test
    void deberiaLanzarUnExcepcion_SiSePasaUnCorreoElectronicoConValorNulo(){
        assertThrows(NullPointerException.class, () -> new Usuario("Agus", "Aguero", 
            "46787883", 20, 
            "3755578920", null));
    }

}
