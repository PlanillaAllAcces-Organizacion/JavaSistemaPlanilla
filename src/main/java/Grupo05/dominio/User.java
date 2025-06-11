package Grupo05.dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private int id;
    private String name;
    private String passwordHash;
    private byte status;
    private LocalDateTime fechaCreado;

    public User() {
        this.fechaCreado = LocalDateTime.now();
    }

    public User(int id, String name, String passwordHash, String fechaCreadoStr, byte status) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.status = status;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.fechaCreado = LocalDateTime.parse(fechaCreadoStr, formatter);


    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getFechaCreado() {
        return fechaCreado;
    }

    public byte getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFechaCreado(LocalDateTime fechaCreado) {
        this.fechaCreado = fechaCreado;
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