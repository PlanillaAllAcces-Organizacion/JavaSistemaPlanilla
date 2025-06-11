package Grupo05.dominio;

public class Descuentos {
    private int id;
    private String nombre;
    private double valor;
    private byte estado;
    private byte operacion;

    public Descuentos() {
    }

    public Descuentos(int id, String nombre, double valor, byte estado, byte operacion) {
        this.id = id;
        this.nombre = nombre;
        this.valor = valor;
        this.estado = estado;
        this.operacion = operacion;
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

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public byte getOperacion() {
        return operacion;
    }

    public void setOperacion(byte operacion) {
        this.operacion = operacion;
    }



    // Método toString para representación del objeto
    @Override
    public String toString() {
        return "Descuento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", valor=" + valor +
                ", estado=" + estado +
                ", operacion=" + operacion +
                '}';
    }
}