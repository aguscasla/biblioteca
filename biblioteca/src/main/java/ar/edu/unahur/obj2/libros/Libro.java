package ar.edu.unahur.obj2.libros;

import java.time.LocalDate;
import java.util.Objects;

import ar.edu.unahur.obj2.exceptions.LibroInhabilitadoException;

public class Libro {

    private static Integer siguienteId = 1;

    private String titulo;
    private String autor;
    private Categoria categoria;
    private LocalDate fechaDePublicacion;
    private Estado estado;
    private Integer id = 0;
    private Integer cantidadDePaginas;
    private Idioma idioma;

    public enum Estado {
        DISPONIBLE,
        PRESTADO,
        EN_REPARACION,
        INHABILITADO
    }

    public enum Idioma {
        INGLES,
        CASTELLANO,
        PORTUGUES,
        ITALIANO,
        FRANCES
    }

    public enum Categoria {
        ACADEMICO,
        ARTE_Y_DISENO,
        AUTOAYUDA,
        BIOGRAFIA,
        CIENCIA,
        CIENCIA_FICCION,
        COMIC_Y_MANGA,
        DESARROLLO_PERSONAL,
        ECONOMIA_Y_NEGOCIOS,
        ENSAYO,
        FANTASIA,
        FILOSOFIA,
        GASTRONOMIA,
        HISTORIA,
        INFANTIL_Y_JUVENIL,
        INFORMATICA_Y_TECNOLOGIA,
        LITERATURA,
        MANUALES_Y_TECNICOS,
        MISTERIO_Y_POLICIAL,
        NOVELA_HISTORICA,
        POESIA_Y_TEATRO,
        ROMANCE,
        SALUD_Y_BIENESTAR,
        TERROR
    }

    public Libro(String titulo, String autor, Categoria categoria, LocalDate fechaDePublicacion,
            Integer cantidadDePaginas, Idioma idioma) {

        if(cantidadDePaginas <= 0){
            throw new IllegalArgumentException("La cantidad de paginas es invalida");
        }
                
        this.titulo = Objects.requireNonNull(titulo, "El titulo es obligatorio");
        this.autor = Objects.requireNonNull(autor, "El autor es obligatorio");
        this.categoria = Objects.requireNonNull(categoria, "La categoria es obligatoria");
        this.fechaDePublicacion = Objects.requireNonNull(fechaDePublicacion, "La fecha de publicacion es obligatoria");
        this.cantidadDePaginas = Objects.requireNonNull(cantidadDePaginas, "La cantidad de paginas es obligatoria");
        this.idioma = Objects.requireNonNull(idioma, "El idioma es obligatorio");

        this.estado = Estado.DISPONIBLE;
        this.id = siguienteId++;
    }

    public void habilitar(){
        if(estado == Estado.INHABILITADO){
            throw new LibroInhabilitadoException("El libro se encuentra inhabilitado");
        }

        estado = Estado.DISPONIBLE;
    }

    public void prestar(){
        this.estado = Estado.PRESTADO;
    }

    public void reparar(){
        this.estado = Estado.EN_REPARACION;
    }

    public void inhabilitar(){
        this.estado = Estado.INHABILITADO;
    }

    public Estado getEstado(){
        return estado;
    }

    public Integer getId(){
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public LocalDate getFechaDePublicacion() {
        return fechaDePublicacion;
    }

    public Integer getCantidadDePaginas() {
        return cantidadDePaginas;
    }

    public Idioma getIdioma() {
        return idioma;
    } 

}
