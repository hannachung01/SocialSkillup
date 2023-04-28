package com.example.socialskillup1;

public class MembruGrup extends Cont {
    private GrupRol rol;
    public MembruGrup(int IDUtilizator, String name, String username, int nivel, String pozaPath, GrupRol gr) {
        super(IDUtilizator, name, username, nivel, pozaPath);
        this.rol = gr;
    }
    public MembruGrup(Cont c, GrupRol gr) {
        super(c.getIDUtilizator(), c.getName(), c.getUsername(), c.getNivel(), c.getPozaPath());
        this.rol = gr;
    }

    public GrupRol getRol() {
        return rol;
    }
    public void setRol(GrupRol rol) {
        this.rol = rol;
    }
}
