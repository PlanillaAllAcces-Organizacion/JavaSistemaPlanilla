package Grupo05.dominio;

public class AsignacionBonos {
    private int id;
    private int empleadoId;
    private int bonoId;

    // Constructor vacío
    public AsignacionBonos() {
    }

    // Constructor con parámetros
    public AsignacionBonos(int id, int empleadoId, int bonoId) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.bonoId = bonoId;
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

    @Override
    public String toString() {
        return "AsignacionBono{" +
                "id=" + id +
                ", empleadoId=" + empleadoId +
                ", bonoId=" + bonoId +
                '}';
    }
}