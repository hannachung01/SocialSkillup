package com.example.socialskillup1;
import java.sql.*;
import java.util.ArrayList;

public class Grup {
    private int IDGrup;
    private String numeGrup;
    private String pozaGrup;
    private String scop;
    ArrayList<MembruGrup> membri;

    public Grup(int IDGrup, String numeGrup, String pozaGrup, String scop, GrupRol rol) throws SQLException {
        this.IDGrup = IDGrup;
        this.numeGrup = numeGrup;
        this.pozaGrup = pozaGrup;
        this.scop = scop;
        populeazaMembriiLista();
    }

    public void populeazaMembriiLista() throws SQLException {
        membri = new ArrayList<>();
            String query = "SELECT * FROM MembriiGrupelor WHERE IDGrup = ?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, String.valueOf(IDGrup));
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
        }
            Cont n = new Cont(rs);
            int gr = rs.getInt("grupRol");
            GrupRol rol;
            switch (gr)
            {
                case 0:
                    rol = GrupRol.LEAD;
                    break;
                case 1:
                    rol = GrupRol.MID;
                    break;
                default:
                    rol = GrupRol.MEMBER;

            }
            MembruGrup mem = new MembruGrup(n, rol);
            membri.add(mem);
        }
    }

    public int getIDGrup() {
        return IDGrup;
    }

    public String getNumeGrup() {
        return numeGrup;
    }

    public String getPozaGrup() {
        return pozaGrup;
    }

    public String getScop() {
        return scop;
    }

    public void setIDGrup(int IDGrup) {
        this.IDGrup = IDGrup;
    }

    public void setNumeGrup(String numeGrup) {
        this.numeGrup = numeGrup;
    }

    public void setPozaGrup(String pozaGrup) {
        this.pozaGrup = pozaGrup;
    }

    public void setScop(String scop) {
        this.scop = scop;
    }

}
