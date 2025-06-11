package Grupo05.dominio;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Empleado {

    private int id;
    private Integer tipoDeHorarioId;
    private Integer puestoTrabajoId;
    private String dui;
    private String nombre;
    private String apellido;
    private int telefono;
    private String correo;
    private byte estado;
    private double salario;
    private LocalDateTime fechacontra;


    public Empleado(){
        this.fechacontra = LocalDateTime.now();
    }

    public Empleado(int id, Integer tipoDeHorarioId, Integer puestoTrabajoId, String dui, String nombre, String apellido, int telefono, String corre,
                    byte estado, double salario, String fechaContraStr){
        this.id = id;
        this.tipoDeHorarioId = tipoDeHorarioId;
        this.puestoTrabajoId = puestoTrabajoId;
        this.dui = dui;
        this.nombre = nombre;
        this.apellido =  apellido;
        this.telefono = telefono;
        this.correo = corre;
        this.estado = estado;
        this.salario = salario;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.fechacontra = LocalDateTime.parse(fechaContraStr, formatter);



    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTipoDeHorarioId() {
        return tipoDeHorarioId;
    }

    public void setTipoDeHorarioId(Integer tipoDeHorarioId) {
        this.tipoDeHorarioId = tipoDeHorarioId;
    }

    public Integer getPuestoTrabajoId() {
        return puestoTrabajoId;
    }

    public void setPuestoTrabajoId(Integer puestoTrabajoId) {
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
