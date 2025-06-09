package Grupo05.dominio;

public class AsignacionBonos {
    private int id;
    private int empleadoId;
    private int bonoId;
    private byte estado;

    // Constructor vacío
    public AsignacionBonos() {
    }

    // Constructor con parámetros
    public AsignacionBonos(int id, int empleadoId, int bonoId, byte estado) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.bonoId = bonoId;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public int getBonoId() {
        return bonoId;
    }

    public void setBonoId(int bonoId) {
        this.bonoId = bonoId;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    // Método para verificar si la asignación está activa
    public boolean isActivo() {
        return estado == 1;
    }

    @Override
    public String toString() {
        return "AsignacionBono{" +
                "id=" + id +
                ", empleadoId=" + empleadoId +
                ", bonoId=" + bonoId +
                ", estado=" + estado +
                '}';
    }
}