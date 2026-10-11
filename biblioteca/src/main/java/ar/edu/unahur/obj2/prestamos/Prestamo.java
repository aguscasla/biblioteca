package ar.edu.unahur.obj2.prestamos;

import java.time.LocalDate;
import java.util.Objects;

import ar.edu.unahur.obj2.exceptions.FechaInvalidaException;
import ar.edu.unahur.obj2.exceptions.PrestamoFinalizadoException;
import ar.edu.unahur.obj2.exceptions.PrestamoSinRenovacionesDisponiblesException;
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

        this.id = Objects.requireNonNull(id, "El id es obligatorio");
        this.socio = Objects.requireNonNull(socio, "El socio es obligatorio");
        this.ejemplar = Objects.requireNonNull(ejemplar, "El ejemplar es obligatorio");
        this.fechaDeInicio = Objects.requireNonNull(fechaDeInicio, "La fecha de inicio es obligatoria");
        this.fechaLimite = Objects.requireNonNull(fechaLimite, "La fecha de inicio es obligatoria");
        this.cantidadDeRenovaciones = Objects.requireNonNull(cantidadDeRenovaciones, "La cantidad de renovaciones es obligatoria");
        this.estado = Estado.EN_CURSO;

        if(cantidadDeRenovaciones < 0){
            throw new IllegalArgumentException("La cantidad de renovaciones es invalida");
        }

        if(fechaDeInicio.isEqual(fechaLimite) || fechaDeInicio.isAfter(fechaLimite)){
            throw new FechaInvalidaException("La fecha no es valida");
        }

        if(id <= 0){
            throw new IllegalArgumentException("El id es invalido");
        }

    }

    public Boolean estaVencido(LocalDate fechaActual){
        return fechaActual.isAfter(fechaLimite);
    }

    public void descontarRenovacion() {
        if(cantidadDeRenovaciones == 0){
            throw new PrestamoSinRenovacionesDisponiblesException("El prestamo no cuenta con renovaciones disponibles");
        }

        this.cantidadDeRenovaciones -= 1;
    }

    public void finalizarPrestamo(){
        if(estado == Estado.FINALIZADO){
            throw new PrestamoFinalizadoException("El prestamo se encuentra finalizado");
        }
        
        estado = Estado.FINALIZADO;
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

    public Libro getEjemplar(){
        return ejemplar;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getFechaDeInicio() {
        return fechaDeInicio;
    }
    
}
