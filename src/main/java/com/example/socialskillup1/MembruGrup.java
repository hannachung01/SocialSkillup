package com.example.socialskillup1;
import java.sql.*;

public class MembruGrup extends Cont {
    private GrupRol rol;
    private int IDLinkedGrup;
    boolean orientatMembru = true; //pastreaza informatie despre daca este parte de lista de membri sau lista de grupuri, membri = true, grupuri = false

    public MembruGrup(int IDUtilizator, String name, String username, int xp, String pozaPath, String descriere, GrupRol gr, int IDLinkedGrup, boolean orientatMembru) {
        super(IDUtilizator, name, username, xp, pozaPath, descriere);
        this.rol = gr;
        this.IDLinkedGrup = IDLinkedGrup;
        this.orientatMembru = orientatMembru;
    }
    public MembruGrup(Cont c, GrupRol gr, int IDLinkedGrup, boolean orientatMembru) {
        super(c.getIDUtilizator(), c.getName(), c.getUsername(), c.getXP(), c.getPozaPath(), c.getDescriere());
        this.rol = gr;
        this.IDLinkedGrup = IDLinkedGrup;
        this.orientatMembru = orientatMembru;
    }
    public int getIDlinkedGrup() {
        return IDLinkedGrup;
    }

    public void setIDlinkedGrup(int IDlinkedGrup) {
        this.IDLinkedGrup = IDlinkedGrup;
    }

    public boolean isOrientatMembru() {
        return orientatMembru;
    }

    public void setOrientatMembru(boolean orientatMembru) {
        this.orientatMembru = orientatMembru;
    }

    public GrupRol getRol() {
        return rol;
    }
    public void setRol(GrupRol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        if (orientatMembru) return super.getUsername();
        else
        {
            Grup g = Grup.lookupGrup(IDLinkedGrup);
            return g.getNumeGrup();
        }
    }

    public static MembruGrup extrageMembruGrup (ResultSet rs, boolean orientatMembru) throws SQLException {
        //primit resultset cu toti membri dintr-un grup sau toate grupuri din membru, depinde de cine a chema
        // "SELECT * FROM MembriiGrupelor WHERE IDGrup = ?";
        int gr = rs.getInt("GrupRol");
        int IDGrup = rs.getInt("IDGrup");
        int idmem = rs.getInt("IDUtilizator");
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
        Cont n = Cont.lookupCont(idmem);
        MembruGrup mem = new MembruGrup(n, rol, IDGrup, orientatMembru);
        return mem;
    }

}
