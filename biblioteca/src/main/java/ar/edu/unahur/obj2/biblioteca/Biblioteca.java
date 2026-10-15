package ar.edu.unahur.obj2.biblioteca;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ar.edu.unahur.obj2.exceptions.LibroNoDisponibleException;
import ar.edu.unahur.obj2.exceptions.PrestamoNoEncontradoException;
import ar.edu.unahur.obj2.exceptions.PrestamoNoRenovableException;
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

public class Biblioteca {

    private Catalogo catalogo;
    private Map<String, Usuario> socios = new HashMap<>();
    private ArrayList<Prestamo> prestamos = new ArrayList<>();
    private Clock reloj;

    public Biblioteca(Clock reloj) {
        this.reloj = reloj;
        this.catalogo = new Catalogo();
    }

    public void asociarUsuario(Usuario usuario, Integer limiteDePrestamos){
    
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null.");
        }

        if (socios.containsKey(usuario.getDni())) {
            throw new UsuarioYaRegistradoException();
        }
        
        usuario.establecerLimiteDePrestamos(limiteDePrestamos);
        usuario.darDeAlta();
        
        socios.put(usuario.getDni(), usuario);

    }

    public void reasociarUsuario(String dniUsuario){
        Usuario usuario = obtenerUsuario(dniUsuario);

        if(usuario.getEstado() != Usuario.Estado.BAJA_VOLUNTARIA){
            throw new UsuarioNoReasociableException();
        }

        usuario.darDeAlta();

    }

    public void desasociarUsuario(String dniUsuario){
        Usuario usuario = obtenerUsuario(dniUsuario);

        if(usuario.getEstado() != Usuario.Estado.ACTIVO){
            throw new UsuarioInactivoException();
        }

        usuario.darBajaVoluntaria();
    }

    public void inhabilitarUsuario(String dniUsuario){
        Usuario usuario = obtenerUsuario(dniUsuario);

        if(usuario.getEstado() == Usuario.Estado.INHABILITADO){
            throw new UsuarioInhabilitadoException();
        }

        usuario.inhabilitar();
    }

    public Usuario obtenerUsuario(String dniDelUsuario){
        Usuario usuario = socios.get(dniDelUsuario);

        if(usuario == null){
            throw new UsuarioNoRegistradoException();
        }

        return usuario;
    }

    public void evaluarUsuarios(){
        socios.forEach((dni, usuario) -> {
            if(estaInfringiendoLasNormas(usuario)){
                inhabilitarUsuario(dni);
            }
        }
        );
    }

    public Boolean estaInfringiendoLasNormas(Usuario usuario){
        ArrayList<Prestamo> prestamosDelUsuario = obtenerPrestamosDelUsuario(usuario);

        return acumulaPrestamosVencidos(prestamosDelUsuario) || (unEjemplarNoHaSidoDevuelto(prestamosDelUsuario));
    }

    public Boolean unEjemplarNoHaSidoDevuelto(ArrayList<Prestamo> prestamosDelUsuario){
        LocalDate hoy = LocalDate.now(reloj);

        return prestamosDelUsuario.stream()
                .filter(prestamo -> prestamo.getEstado() == Prestamo.Estado.EN_CURSO)
                .anyMatch(prestamo -> {
                            return hoy.isAfter(prestamo.getFechaLimite().plusDays(7));
                }
        );
    }       

    public Boolean acumulaPrestamosVencidos(ArrayList<Prestamo> prestamosDelUsuario){

        Integer cantidadDePrestamosVencidos = (int) prestamosDelUsuario.stream()
                .filter(prestamo -> prestamo.getEstado().equals(Prestamo.Estado.FINALIZADO))
                .filter(prestamo -> prestamo.getFechaDeDevolucion() != null)
                .filter(prestamo -> prestamo.getFechaDeDevolucion().isAfter(prestamo.getFechaLimite()))
                .count();

        return cantidadDePrestamosVencidos >= 3;
    }

    public ArrayList<Prestamo> obtenerPrestamosDelUsuario(Usuario usuario){
        return prestamos.stream()
                .filter(prestamo -> prestamo.getSocio() == usuario)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void renovarPrestamo(Integer idDelLibro){
        Libro libro = catalogo.obtenerLibro(idDelLibro);

        Prestamo prestamo = obtenerPrestamo(libro);

        LocalDate hoy = LocalDate.now(reloj);

        if(prestamo.estaVencido(hoy)){
            throw new PrestamoVencidoException();
        }

        if(!(!hoy.isBefore(prestamo.getFechaLimite().minusDays(2)) && !hoy.isAfter(prestamo.getFechaLimite()))){
            throw new PrestamoNoRenovableException();
        }
        
        prestamo.descontarRenovacion();
        prestamo.extenderFechaLimite(7);

    }

    public Prestamo obtenerPrestamo(Libro libro){
        Prestamo prestamo = prestamos.stream()
                .filter(p -> p.getEjemplar().equals(libro))
                .filter(p -> p.getEstado() == Prestamo.Estado.EN_CURSO)
                .findFirst()
                .orElse(null);

        if(prestamo == null){
            throw new PrestamoNoEncontradoException();
        }

        return prestamo;
    }

    public void finalizarPrestamo(Integer idDelLibro){

        Libro libro = catalogo.obtenerLibro(idDelLibro);

        Prestamo prestamoAFinalizar = obtenerPrestamo(libro);

        LocalDate hoy = LocalDate.now(reloj);

        prestamoAFinalizar.finalizarPrestamo();
        prestamoAFinalizar.establecerFechaDeDevolucion(hoy);
        libro.habilitar();

    }

    public void validarPrestamo(Usuario socio, Libro libro){
        
        if(estaInfringiendoLasNormas(socio)){
            throw new UsuarioInhabilitadoException();
        }

        if(socio.getEstado() != Usuario.Estado.ACTIVO){
            throw new UsuarioInactivoException();
        }

        if(socio.getLimiteDePrestamos() == prestamosEnCursoDelUsuario(socio).size()) {
            throw new UsuarioSinPrestamosDisponiblesException();
        }

        if(libro.getEstado() != Libro.Estado.DISPONIBLE){
            throw new LibroNoDisponibleException();
        }

    }

    public ArrayList<Prestamo> prestamosEnCursoDelUsuario(Usuario socio){
        return prestamos.stream()
                .filter(prestamo -> prestamo.getSocio() == socio && prestamo.getEstado() == Prestamo.Estado.EN_CURSO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void otorgarPrestamo(Integer idDelPrestamo, String dniDelUsuario, Integer idDelLibro, LocalDate fechaDeInicio,
                                        LocalDate fechaDeLimite, Integer cantidadDeRenovaciones){

        Usuario socio = obtenerUsuario(dniDelUsuario);

        Libro libro = catalogo.obtenerLibro(idDelLibro);

        validarPrestamo(socio, libro);

        Prestamo prestamo = new Prestamo(idDelPrestamo, socio, libro, fechaDeInicio, fechaDeLimite, cantidadDeRenovaciones);

        libro.prestar();
        prestamos.add(prestamo);

    }

    public void agregarLibroAlCatalogo(Libro libro){
        catalogo.agregarLibro(libro);
    }

    public void eliminarLibroDelCatalogo(Integer idDelLibro){
        Libro libro = catalogo.obtenerLibro(idDelLibro);

        if(libro.getEstado() == Libro.Estado.PRESTADO){
            throw new LibroNoDisponibleException();
        }

        catalogo.eliminarLibro(idDelLibro);
    }

    public Libro obtenerLibro(Integer idDelLibro){
        return catalogo.obtenerLibro(idDelLibro);
    }

    // getters

    public Map<String, Usuario> getSocios() {
        return Map.copyOf(socios);
    }

    public List<Prestamo> getPrestamos() {
        return List.copyOf(prestamos);
    }

}
