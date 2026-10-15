package criteriosDeBusqueda;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ar.edu.unahur.obj2.criteriosDeBusqueda.BusquedaPorAutor;
import ar.edu.unahur.obj2.criteriosDeBusqueda.CriterioDeBusqueda;

public class CriterioDeBusquedaTest {

    private CriterioDeBusqueda criterioAutor;

    @BeforeEach 
    void setUp(){
        criterioAutor = new BusquedaPorAutor("Luis Borges");
    }

    @Test 
    void deberiaLanzarUnaExcepcionAlCombinarCriteriosConY_SiSePasaUnCriterioNulo(){
        Executable act = () -> criterioAutor.y(null);

        assertThrows(NullPointerException.class, act);
    }

    @Test 
    void deberiaLanzarUnaExcepcionAlCombinarCriteriosConO_SiSePasaUnCriterioNulo(){
        Executable act = () -> criterioAutor.o(null);

        assertThrows(NullPointerException.class, act);
    }

}
