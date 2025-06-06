package Grupo05.dominio;

import java.math.BigDecimal;

public class PuestoTrabajo {
    private int id;
    private String nombrePuesto;
    private BigDecimal salarioBase;
    private BigDecimal valorxHora;
    private BigDecimal valorExtra;
    private byte estado;

    public PuestoTrabajo() {
    }

    public PuestoTrabajo(int id, String nombrePuesto, BigDecimal salarioBase,
                         BigDecimal valorxHora, BigDecimal valorExtra, byte estado) {
        this.id = id;
        this.nombrePuesto = nombrePuesto;
        this.salarioBase = salarioBase;
        this.valorxHora = valorxHora;
        this.valorExtra = valorExtra;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePuesto() {
        return nombrePuesto;
    }

    public void setNombrePuesto(String nombrePuesto) {
        this.nombrePuesto = nombrePuesto;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getValorxHora() {
        return valorxHora;
    }

    public void setValorxHora(BigDecimal valorxHora) {
        this.valorxHora = valorxHora;
    }

    public BigDecimal getValorExtra() {
        return valorExtra;
    }

    public void setValorExtra(BigDecimal valorExtra) {
        this.valorExtra = valorExtra;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    // Método para obtener el estado como String
    public String getStrEstado() {
        String str = "";
        switch (estado) {
            case 1:
                str = "ACTIVO";
                break;
            case 2:
                str = "INACTIVO";
                break;
            default:
                str = "DESCONOCIDO";
        }
        return str;
    }

    // Método toString para representación del objeto
    @Override
    public String toString() {
        return "PuestoTrabajo{" +
                "id=" + id +
                ", nombrePuesto='" + nombrePuesto + '\'' +
                ", salarioBase=" + salarioBase +
                ", valorxHora=" + valorxHora +
                ", valorExtra=" + valorExtra +
                ", estado=" + getStrEstado() +
                '}';
    }
}