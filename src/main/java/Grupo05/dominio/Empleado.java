package Grupo05.dominio;

import java.time.LocalDateTime;

public class Empleado {

    int Id;
    int tipoDeHorarioId;
    int puestoTrabajoId;
    String dui;
    String nombre;
    String apellido;
    int telefono;
    String correo;
    byte estado;
    double salario;
    LocalDateTime fechacontra;
    String usuario;
    String passwordHash;


    public Empleado(){

    }

    public Empleado(int Id, int tipoDeHorarioId, int puestoTrabajoId, String dui, String nombre, String apellido, int telefono, String corre,
                    byte estado, double salario, LocalDateTime fechacontra, String usuario, String passwordHash){
        this.Id = Id;
        this.tipoDeHorarioId = tipoDeHorarioId;
        this.puestoTrabajoId = puestoTrabajoId;
        this.dui = dui;
        this.nombre = nombre;
        this.apellido =  apellido;
        this.telefono = telefono;
        this.correo = corre;
        this.estado = estado;
        this.salario = salario;
        this.fechacontra = fechacontra;
        this.usuario = usuario;
        this.passwordHash = passwordHash;


    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = Id;
    }

    public Integer getTipoDeHorarioId() {
        return tipoDeHorarioId;
    }

    public void setTipoDeHorarioId(int tipoDeHorarioId) {
        this.tipoDeHorarioId = tipoDeHorarioId;
    }

    public Integer getPuestoTrabajoId() {
        return puestoTrabajoId;
    }

    public void setPuestoTrabajoId(int puestoTrabajoId) {
        this.puestoTrabajoId = puestoTrabajoId;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public LocalDateTime getFechacontra() {
        return fechacontra;
    }

    public void setFechacontra(LocalDateTime fechacontra) {
        this.fechacontra = fechacontra;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStrEstatus(){
        String str="";
        switch (estado) {
            case 1:
                str = "ACTIVO";
                break;
            case 2:
                str = "INACTIVO";
                break;
            default:
                str = "";
        }
        return str;
    }
}
