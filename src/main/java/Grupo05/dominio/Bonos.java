package Grupo05.dominio;

public class Bonos {
    private int id;
    private String nombreBono;
    private double valor;
    private byte estado;
    private byte operacion;

    public Bonos() {
    }

    public Bonos(int id, String nombreBono, double valor, byte estado, byte operacion) {
        this.id = id;
        this.nombreBono = nombreBono;
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
        return "Bono{" +
                "id=" + id +
                ", nombreBono='" + nombreBono + '\'' +
                ", valor=" + valor +
                ", estado=" + estado +
                ", operacion=" + operacion +
                '}';
    }
}