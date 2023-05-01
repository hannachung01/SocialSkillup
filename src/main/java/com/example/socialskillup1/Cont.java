package com.example.socialskillup1;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cont {
    private int IDUtilizator;
    private String name;
    private String username;
    private int xp;
    private String password;
    private String email;
    String pozaPath;
    private String descriere;

    //implementeaza stickere mai incolo
    //implementeaza inventar mai incolo
    ArrayList<Cont> prieteni;
    ArrayList<MembruGrup> grupuri;
    ArrayList<ConversatiePrivata> conversatii;

    public Cont(int IDUtilizator, String name, String username, int xp, String pozaPath, String descriere) {
        this.IDUtilizator = IDUtilizator;
        this.name = name;
        this.username = username;
        this.xp = xp;
        this.pozaPath = pozaPath;
        this.descriere = descriere;
    }

    public Cont(ResultSet rs) throws SQLException { //resultset de la tabel Conturi
        this.IDUtilizator = rs.getInt("IDUtilizator");
        this.username = rs.getString("Username");
        this.name = rs.getString("Nume");
        this.xp = rs.getInt("XP");
        this.pozaPath = rs.getString("Poza");
        this.password = rs.getString("Parola");
        this.email = rs.getString("Email");
        this.descriere = rs.getString("Descriere");
    }

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
            rs.close();
            pst.close();
            conn.close();
            return c;
        }
        else
        {
            rs.close();
            pst.close();
            conn.close();
            return null;
        }

    }
    public ConversatiePrivata lookupConversatie(int convid)
    {
        for (ConversatiePrivata c : conversatii)
        {
            if (c.getIDConversatiePrivata() == convid)
            {
                return c;
            }
        }
        return null;
    }
    public int getIDUtilizator() {
        return IDUtilizator;
    }
    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public int getXp() {
        return xp;
    }

    public String getPozaPath() {
        return pozaPath;
    }

    public ArrayList<Cont> getPrieteni() {
        return prieteni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setPozaPath(String pozaPath) {
        this.pozaPath = pozaPath;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
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
        rs.close();
        pst.close();
        conn.close();
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
            Cont p = lookupCont(idCautat);
            if (p != null) prieteni.add(p);
        }
    }

    public void populeazaConversatii() throws SQLException{
        conversatii = new ArrayList<>();
        String query = "SELECT * FROM ConversatiiPrivateParticipanti WHERE IDParticipant = ?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, String.valueOf(IDUtilizator));
        ResultSet rs = pst.executeQuery(); //da o lista de conversatii
        while (rs.next()) //parcurge prin fiecare conversatie
        {


            ConversatiePrivata cp;
            ArrayList<Cont> participanti = new ArrayList<Cont>();
            int idconv = rs.getInt("IDConversatie");
            String query2 = "SELECT * FROM ConversatiiPrivateParticipanti WHERE IDConversatie = ? AND ";
            PreparedStatement pst2 = conn.prepareStatement(query2);
            pst2.setString(1, String.valueOf(idconv));
            ResultSet rs2 = pst2.executeQuery();
            while (rs2.next())
            {
                int IDPart = rs2.getInt("IDParticipant");
                Cont c = lookupCont(IDPart);
                participanti.add(c);
            }
            ArrayList<Mesaj> mesaje =new ArrayList<>();
            String query3 = "SELECT * FROM MesajePrivate WHERE IDConversatie = ?";
            PreparedStatement pst3 = conn.prepareStatement(query3);
            pst3.setString(1, String.valueOf(idconv));
            ResultSet rs3 = pst3.executeQuery();
            while (rs3.next())
            {
                int senderID = rs3.getInt("SenderID");
                String continut = rs3.getString("Continut");
                LocalDateTime ts = LocalDateTime.parse(rs3.getString("Timestamp"));
                Mesaj m = new Mesaj(senderID, continut, ts);
                mesaje.add(m);
            }
            cp = new ConversatiePrivata(idconv, participanti, mesaje);
            conversatii.add(cp);
        }
    }
}
