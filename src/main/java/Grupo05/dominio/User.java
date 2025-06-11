package Grupo05.dominio;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String name;
    private String passwordHash;
    private byte status;
    LocalDateTime fechaCreado;

    public User() {

    }

    public User(int id, String name, String passwordHash, String email, byte status, LocalDateTime fechaCreado) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.status = status;
        this.fechaCreado = fechaCreado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFechaCreado(LocalDateTime fechaCreado) { this.fechaCreado = fechaCreado; }

    public LocalDateTime getFechaCreado() {
        return fechaCreado;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getStrEstatus(){
        String str="";
        switch (status) {
            case 1:
                str = "ACTIVO";
                break;
            case 2:
                str = "INACTIVO";
                break;
            default:
                str = "";
        }
        return str;
    }
}

