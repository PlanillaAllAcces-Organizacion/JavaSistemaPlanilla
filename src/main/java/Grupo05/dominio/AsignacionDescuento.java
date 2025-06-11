package Grupo05.dominio;

public class AsignacionDescuento {

    private int id;
    private int empleadoId;
    private int Descuentos;

    public AsignacionDescuento() {
    }

    public AsignacionDescuento(int id, int empleadoId, int descuentos) {
        this.id = id;
        this.empleadoId = empleadoId;
        Descuentos = descuentos;

    }

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

    public int getDescuentos() {
        return Descuentos;
    }

    public void setDescuentos(int descuentos) {
        Descuentos = descuentos;
    }
}