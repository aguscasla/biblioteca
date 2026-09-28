package ar.edu.unahur.obj2.prestamos;

import java.time.LocalDate;

import ar.edu.unahur.obj2.libros.Libro;
import ar.edu.unahur.obj2.usuarios.Usuario;

public class Prestamo {

    private Integer id;
    private Usuario socio;
    private Libro ejemplar;
    private LocalDate fechaDeInicio;
    private LocalDate fechaLimite;
    private LocalDate fechaDeDevolucion;
    private Integer cantidadDeRenovaciones;
    private Estado estado;

    public enum Estado {
        FINALIZADO,
        EN_CURSO
    }

    public Prestamo(Integer id, Usuario socio, Libro ejemplar, LocalDate fechaDeInicio, LocalDate fechaLimite,
            Integer cantidadDeRenovaciones) {
        this.id = id;
        this.socio = socio;
        this.ejemplar = ejemplar;
        this.fechaDeInicio = fechaDeInicio;
        this.fechaLimite = fechaLimite;
        this.cantidadDeRenovaciones = cantidadDeRenovaciones;
        this.estado = Estado.EN_CURSO;
    }

    public Boolean estaVencido(LocalDate fechaActual){
        return fechaActual.isAfter(fechaLimite);
    }

    public void descontarRenovacion() {
        this.cantidadDeRenovaciones -= 1;
    }

    public Estado getEstado(){
        return estado;
    }

    public Usuario getSocio() {
        return socio;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public LocalDate getFechaDeDevolucion() {
        return fechaDeDevolucion;
    }

    public Integer getCantidadDeRenovaciones() {
        return cantidadDeRenovaciones;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
}
