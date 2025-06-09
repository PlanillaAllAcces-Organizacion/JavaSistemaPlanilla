package Grupo05.Persistencia;

public class Descuento {
    private int id;
    private String nombre;
    private double valor;
    private Byte estado;     // Puede ser null
    private byte operacion;
    private byte planilla;

    // Constructor vacío
    public Descuento() {}

    // Constructor con todos los campos
    public Descuento(int id, String nombre, double valor, Byte estado, byte operacion, byte planilla) {
        this.id = id;
        this.nombre = nombre;
        this.valor = valor;
        this.estado = estado;
        this.operacion = operacion;
        this.planilla = planilla;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Byte getEstado() {
        return estado;
    }

    public void setEstado(Byte estado) {
        this.estado = estado;
    }

    public byte getOperacion() {
        return operacion;
    }

    public void setOperacion(byte operacion) {
        this.operacion = operacion;
    }

    public byte getPlanilla() {
        return planilla;
    }

    public void setPlanilla(byte planilla) {
        this.planilla = planilla;
    }

    // Método toString (opcional)
    @Override
    public String toString() {
        return "Descuento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", valor=" + valor +
                ", estado=" + estado +
                ", operacion=" + operacion +
                ", planilla=" + planilla +
                '}';
    }
}