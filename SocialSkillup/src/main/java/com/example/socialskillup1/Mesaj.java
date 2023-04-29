package com.example.socialskillup1;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mesaj {
    private int senderID;
    private String continut;
    private LocalDateTime timestamp;

    public Mesaj( int senderID, String continut, LocalDateTime timestamp) {
        this.senderID = senderID;
        this.continut = continut;
        this.timestamp = timestamp;
    }

   public String shortString()
   {
       String query = "SELECT * FROM Conturi WHERE IDUtilizator = ?";
       Connection conn = null;
       String sender = "";
       try {
           conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");

           PreparedStatement pst = conn.prepareStatement(query);
           pst.setString(1, String.valueOf(senderID));
           ResultSet rs = pst.executeQuery();
           if (rs.next())
           {
               sender= rs.getString("Username");
           }
           pst.close();
           conn.close();
           rs.close();

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
       String timp = timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
       String s = String.format("%s %s: ", timp, sender)+continut;
       return s;
   }
}
