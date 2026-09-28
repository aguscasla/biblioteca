package ar.edu.unahur.obj2.libros;

import java.time.LocalDate;

public class Libro {

    private static Integer siguienteId = 1;

    private String titulo;
    private String autor;
    private Categoria categoria;
    private LocalDate fechaDePublicacion;
    private Estado estado;
    private Integer id = 0;
    private Integer cantidadDepaginas;
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
            Integer cantidadDepaginas, Idioma idioma) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.fechaDePublicacion = fechaDePublicacion;
        this.cantidadDepaginas = cantidadDepaginas;
        this.idioma = idioma;

        this.estado = Estado.DISPONIBLE;
        this.id = siguienteId++;
    }

    public void cambiarEstado(Estado estado){
        this.estado = estado;
    }

    public Estado getEstado(){
        return estado;
    }

    public Integer getId(){
        return id;
    }

}
