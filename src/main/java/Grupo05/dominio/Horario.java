package Grupo05.dominio;

public class Horario {
    private int id;
    private String nombreHorario;

    public Horario() {
    }

    public Horario(int id, String nombreHorario) {
        this.id = id;
        this.nombreHorario = nombreHorario;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreHorario() {
        return nombreHorario;
    }

    public void setNombreHorario(String nombreHorario) {
        this.nombreHorario = nombreHorario;
    }

    // Método toString para representación del objeto
    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", nombreHorario='" + nombreHorario + '\'' +
                '}';
    }
}