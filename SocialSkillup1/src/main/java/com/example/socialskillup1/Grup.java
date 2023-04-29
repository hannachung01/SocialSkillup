package com.example.socialskillup1;
import java.sql.*;
import java.util.ArrayList;

public class Grup {
    private int IDGrup;
    private String numeGrup;
    private String pozaGrup;
    private String scop;
    ArrayList<MembruGrup> membri;
    static ArrayList<Grup> toateGrupurile;

    public Grup(int IDGrup, String numeGrup, String pozaGrup, String scop) throws SQLException {
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
            MembruGrup mem = MembruGrup.extrageMembruGrup(rs, true);
            /*
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
            MembruGrup mem = new MembruGrup(n, rol);*/
            membri.add(mem);
        }
    }

    public static void populeazaToateGrupurile() throws SQLException {
        String query = "SELECT * FROM Grupuri";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        toateGrupurile = new ArrayList<>();
        while (rs.next()) {
            int idgrup = rs.getInt("IDGrup");
            String numegrup = rs.getString("NumeGrup");
            String poza=rs.getString("PozaGrup");
            String scop=rs.getString("Scop");
            ArrayList<MembruGrup> membri;
            Grup g = new Grup(idgrup, numegrup, poza, scop);
            toateGrupurile.add(g);
        }
    }

    public static Grup lookupGrup(int grupid)
    {
        if (toateGrupurile.isEmpty()) return null;
        for (Grup g : toateGrupurile)
        {
            if (g.getIDGrup() == grupid) return g;
        }
        return null;
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
