package Grupo05.Persistencia;

public class Bono {
    private int id;
    private String nombreBono;
    private double valor;
    private Byte estado;      // Puede ser null, por eso Byte (no byte)
    private byte operacion;
    private byte planilla;

    // Constructor vacío (requerido para algunas librerías/frameworks como Hibernate)
    public Bono() {}

    // Constructor con todos los campos
    public Bono(int id, String nombreBono, double valor, Byte estado, byte operacion, byte planilla) {
        this.id = id;
        this.nombreBono = nombreBono;
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

    public String getNombreBono() {
        return nombreBono;
    }

    public void setNombreBono(String nombreBono) {
        this.nombreBono = nombreBono;
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

    // Método toString (opcional para imprimir objetos fácilmente)
    @Override
    public String toString() {
        return "Bono{" +
                "id=" + id +
                ", nombreBono='" + nombreBono + '\'' +
                ", valor=" + valor +
                ", estado=" + estado +
                ", operacion=" + operacion +
                ", planilla=" + planilla +
                '}';
    }
}