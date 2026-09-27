package ar.edu.unahur.obj2.usuarios;

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

    public Usuario(String nombre, String apellido, String dni, Integer edad, String telefono, String correoElectronico,
            Integer limiteDePrestamos) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.edad = edad;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.limiteDePrestamos = limiteDePrestamos;

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

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

}
