package com.example.socialskillup1;
import java.time.LocalDateTime;

public class Mesaj {
    private int senderID;
    private String continut;
    private LocalDateTime timestamp;

    public Mesaj( int senderID, String continut, LocalDateTime timestamp) {
        this.senderID = senderID;
        this.continut = continut;
        this.timestamp = timestamp;
    }
}
