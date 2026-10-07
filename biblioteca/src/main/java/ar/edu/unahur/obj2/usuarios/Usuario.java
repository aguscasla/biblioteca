package ar.edu.unahur.obj2.usuarios;

import java.util.Objects;

public class Usuario {

    private String nombre;
    private String apellido;
    private String dni;
    private Integer edad;
    private String telefono;
    private String correoElectronico;
    private Integer limiteDePrestamos;
    private Estado estado;

    public enum Estado {
        ACTIVO,
        INHABILITADO,
        BAJA_VOLUNTARIA,
        NO_REGISTRADO
    }

    public Usuario(String nombre, String apellido, String dni, Integer edad, String telefono, String correoElectronico) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.apellido = Objects.requireNonNull(apellido, "El apellido es obligatorio");
        this.dni = Objects.requireNonNull(dni, "El dni es obligatorio");
        this.edad = Objects.requireNonNull(edad, "La edad es obligatorio");
        this.telefono = Objects.requireNonNull(telefono, "El telefono es obligatorio");
        this.correoElectronico = Objects.requireNonNull(correoElectronico, "El correo electronico es obligatorio");

        this.limiteDePrestamos = 0;
        this.estado = Estado.NO_REGISTRADO;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public Integer getLimiteDePrestamos() {
        return limiteDePrestamos;
    }

    public Estado getEstado() {
        return estado;
    }

    public void darDeAlta(){
        this.estado = Estado.ACTIVO;
    }

    public void inhabilitar(){
        this.estado = Estado.INHABILITADO;
    }

    public void darBajaVoluntaria(){
        this.estado = Estado.BAJA_VOLUNTARIA;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

}
