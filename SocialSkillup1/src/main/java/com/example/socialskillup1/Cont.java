package com.example.socialskillup1;
import java.util.ArrayList;

public class Cont {
    private int IDUtilizator;
    private String name;
    private String username;
    private int nivel;
    String pozaPath;
    //implementeaza stickere mai incolo
    //implementeaza inventar mai incolo
    //implementeaza grupuri
    //implementeaza mesaje
    ArrayList<Cont> prieteni;

    public Cont(int IDUtilizator, String name, String username, int nivel, String pozaPath) {
        this.IDUtilizator = IDUtilizator;
        this.name = name;
        this.username = username;
        this.nivel = nivel;
        this.pozaPath = pozaPath;
    }

    public int getIDUtilizator() {
        return IDUtilizator;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public int getNivel() {
        return nivel;
    }

    public String getPozaPath() {
        return pozaPath;
    }

    public ArrayList<Cont> getPrieteni() {
        return prieteni;
    }

    public void setIDUtilizator(int IDUtilizator) {
        this.IDUtilizator = IDUtilizator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setPozaPath(String pozaPath) {
        this.pozaPath = pozaPath;
    }

    public void setPrieteni(ArrayList<Cont> prieteni) {
        this.prieteni = prieteni;
    }
}
