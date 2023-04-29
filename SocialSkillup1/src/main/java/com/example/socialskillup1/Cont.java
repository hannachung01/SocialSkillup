package com.example.socialskillup1;
import java.sql.*;
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
    ArrayList<MembruGrup> grupuri;
    public static Cont lookupCont(int IDUtilizator) throws SQLException
    {
        String query = "SELECT * FROM Conturi WHERE IDUtilizator = ?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, String.valueOf(IDUtilizator));
        ResultSet rs = pst.executeQuery();
        if (rs.next())
        {
            Cont c = new Cont(rs);
            return c;
        }
        else return null;
    }

    public Cont(int IDUtilizator, String name, String username, int nivel, String pozaPath) {
        this.IDUtilizator = IDUtilizator;
        this.name = name;
        this.username = username;
        this.nivel = nivel;
        this.pozaPath = pozaPath;
    }

    public Cont(ResultSet rs) throws SQLException { //resultset de la tabel Conturi
        this.IDUtilizator = rs.getInt("IDUtilizator");
        this.username = rs.getString("Username");
        this.name = rs.getString("Nume");
        this.nivel = rs.getInt("Nivel");
        this.pozaPath = rs.getString("Poza");
    }

    public Cont() {
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

    public void adaugaPrieteni(int prietenContID) {
    }
    public void populeazaGrupuri() throws SQLException {
        grupuri = new ArrayList<>();
        String query = "SELECT * FROM MembriiGrupelor WHERE IDUtilizator = ?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, String.valueOf(IDUtilizator));
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            MembruGrup mem = MembruGrup.extrageMembruGrup(rs, false); //false pentru ca folosit ca parte de lista de grupuri
            grupuri.add(mem);
        }
    }
    public void populeazaPrieteni() throws SQLException
    {
        prieteni = new ArrayList<>();
        String query = "SELECT * FROM Relatii WHERE IDContPrincipal = ? AND estePrieten = 1";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, String.valueOf(IDUtilizator));
        ResultSet rs = pst.executeQuery();
        while (rs.next())
        {
            int idCautat = rs.getInt("IDContAltuia");
            Cont p = cautaContPeID(idCautat);
            if (p != null) prieteni.add(p);
        }
    }
    public Cont cautaContPeID(int id) throws SQLException
    {
        String query = "SELECT * FROM Conturi WHERE IDUtilizator = ?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, String.valueOf(id));
        ResultSet rs = pst.executeQuery();
        if (rs.next())
        {
            int idutil = rs.getInt("IDUtilizator");
            String username = rs.getString("Username");
            String nume = rs.getString("Nume");
            int nivel = rs.getInt("Nivel");
            String pozapath = rs.getString("Poza");
            Cont r = new Cont(idutil, nume, username, nivel, pozapath);
            return r;
        }
        else return null;
    }

}
