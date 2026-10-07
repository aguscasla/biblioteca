package ar.edu.unahur.obj2.biblioteca;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

public class Biblioteca {

    private ArrayList<Libro> catalogo = new ArrayList<>();
    private Map<String, Usuario> socios = new HashMap<>();
    private ArrayList<Prestamo> prestamos = new ArrayList<>();
    private Clock reloj;

    public Biblioteca(Clock reloj) {
        this.reloj = reloj;
    }

    public void asociarUsuario(Usuario usuario, Integer limiteDePrestamos){
    
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null.");
        }

        if (socios.containsKey(usuario.getDni())) {
            throw new UsuarioYaRegistradoException("El usuario ya se encuentra registrado.");
        }

        usuario.setEstado(Usuario.Estado.ACTIVO);
        usuario.setLimiteDePrestamos(limiteDePrestamos);
        socios.put(usuario.getDni(), usuario);

    }

    public void reasociarUsuario(String dniUsuario){
        Usuario usuario = socios.get(dniUsuario);

        if(usuario == null){
            throw new UsuarioNoRegistradoException("El usuario no se encuentra registrado.");
        }

        if(usuario.getEstado() != Usuario.Estado.BAJA_VOLUNTARIA){
            throw new UsuarioNoReasociableException("Solamente se permite reasociar a los usuarios con baja voluntaria");
        }

        usuario.setEstado(Usuario.Estado.ACTIVO);

    }

    public void desasociarUsuario(String dniUsuario){
        Usuario usuario = socios.get(dniUsuario);

        if(usuario == null){
            throw new UsuarioNoRegistradoException("El usuario no se encuentra registrado.");
        }

        if(usuario.getEstado() != Usuario.Estado.ACTIVO){
            throw new UsuarioInactivoException("El usuario no se encuentra activo.");
        }

        usuario.setEstado(Usuario.Estado.BAJA_VOLUNTARIA);
    }

    public void inhabilitarUsuario(String dniUsuario){
        Usuario usuario = socios.get(dniUsuario);

        if(usuario == null){
            throw new UsuarioNoRegistradoException("El usuario no se encuentra registrado.");
        }

        if(usuario.getEstado() == Usuario.Estado.INHABILITADO){
            throw new UsuarioInhabilitadoException("El usuario ya se encuentra inhabilitado.");
        }

        usuario.setEstado(Usuario.Estado.INHABILITADO);
    }

    public Usuario obtenerUsuario(String dniDelUsuario){
        Usuario usuario = socios.get(dniDelUsuario);

        if(usuario == null){
            throw new UsuarioNoRegistradoException("El usuario no se encuentra registrado");
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

    public void renovarPrestamo(Prestamo prestamo){
        LocalDate hoy = LocalDate.now(reloj);

        if(prestamo.getEstado() != Prestamo.Estado.EN_CURSO){
            throw new PrestamoFinalizadoException("El prestamo debe estar en curso.");
        }

        if(prestamo.estaVencido(hoy)){
            throw new PrestamoVencidoException("El prestamo se encuentra vencido.");
        }

        if(!(!hoy.isBefore(prestamo.getFechaLimite().minusDays(2)) && !hoy.isAfter(prestamo.getFechaLimite()))){
            throw new PrestamoNoRenovableException("El prestamo no puede ser renovado fuera de fecha.");
        }

        if(prestamo.getCantidadDeRenovaciones() == 0){
            throw new PrestamoSinRenovacionesDisponiblesException("El prestamo no cuenta con renovaciones disponibles.");
        }
        
        prestamo.setFechaLimite(prestamo.getFechaLimite().plusDays(7));
        prestamo.descontarRenovacion();

    }

    public void finalizarPrestamo(Prestamo prestamo){
        LocalDate hoy = LocalDate.now(reloj);
        Libro ejemplarDevuelto = prestamo.getEjemplar();

        prestamo.setEstado(Prestamo.Estado.FINALIZADO);
        prestamo.setFechaDeDevolucion(hoy);
        ejemplarDevuelto.cambiarEstado(Libro.Estado.DISPONIBLE);

    }

    public void validarPrestamo(Prestamo prestamo, Usuario socio){
        ArrayList<Prestamo> prestamos = obtenerPrestamosDelUsuario(socio);

        if(estaInfringiendoLasNormas(socio)){
            throw new UsuarioInhabilitadoException("El usuario no se encuentra habilitado para solicitar un prestamo.");
        }

        if(socio.getEstado() != Usuario.Estado.ACTIVO){
            throw new UsuarioInactivoException("El usuario no se encuentra activo");
        }

        if(socio.getLimiteDePrestamos() == prestamosEnCursoDelUsuario(socio).size()) {
            throw new UsuarioSinPrestamosDisponiblesException("El usuario alcanzo el limite de prestamos.");
        }

        if(prestamo.getEjemplar() == null){
            throw new LibroInexistenteException("El libro no existe.");
        }

        if(prestamo.getEjemplar().getEstado() != Libro.Estado.DISPONIBLE){
            throw new LibroNoDisponibleException("El libro no se encuentra disponible.");
        }

    }

    public ArrayList<Prestamo> prestamosEnCursoDelUsuario(Usuario socio){
        return prestamos.stream()
                .filter(prestamo -> prestamo.getSocio() == socio && prestamo.getEstado() == Prestamo.Estado.EN_CURSO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Prestamo otorgarPrestamo(Integer idDelPrestamo, Usuario socio, Libro ejemplar, LocalDate fechaDeInicio,
                                        LocalDate fechaDeLimite, Integer cantidadDeRenovaciones){

        Prestamo prestamo = new Prestamo(idDelPrestamo, socio, ejemplar, fechaDeInicio, fechaDeLimite, cantidadDeRenovaciones);

        validarPrestamo(prestamo, socio);
        ejemplar.cambiarEstado(Libro.Estado.PRESTADO);
        prestamos.add(prestamo);

        return prestamo;
    }

    public ArrayList<Libro> getCatalogo() {
        return catalogo;
    }

    public Map<String, Usuario> getSocios() {
        return socios;
    }

    public ArrayList<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setReloj(Clock reloj){
        this.reloj = reloj;
    }

}
