package Grupo05.dominio;

import java.util.Date;

public class PagoEmpleado {
    private int id;
    private int empleadoId;
    private Date fechaPago;
    private int horasTrabajadas;
    private double valorHora;
    private double totalPago;

    public PagoEmpleado() {
    }

    public PagoEmpleado(int id, int empleadoId, Date fechaPago, int horasTrabajadas, double valorHora, double totalPago) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.fechaPago = fechaPago;
        this.horasTrabajadas = horasTrabajadas;
        this.valorHora = valorHora;
        this.totalPago = totalPago;
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

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(int horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public double getValorHora() {
        return valorHora;
    }

    public void setValorHora(double valorHora) {
        this.valorHora = valorHora;
    }

    public double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(double totalPago) {
        this.totalPago = totalPago;
    }

    @Override
    public String toString() {
        return "PagoEmpleado{" +
                "id=" + id +
                ", empleadoId=" + empleadoId +
                ", fechaPago=" + fechaPago +
                ", horasTrabajadas=" + horasTrabajadas +
                ", valorHora=" + valorHora +
                ", totalPago=" + totalPago +
                '}';
    }
}