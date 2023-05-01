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
    public String shortString() throws SQLException {
        String sendernume = Cont.lookupCont(senderID).getUsername();
        String timp = timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String s = String.format("%s %s: ", timp, sendernume)+continut;
        return s;
    }
}
