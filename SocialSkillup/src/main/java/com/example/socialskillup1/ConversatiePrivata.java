package com.example.socialskillup1;

import java.util.ArrayList;
public class ConversatiePrivata {
    int IDConversatiePrivata;
    ArrayList<Cont> participanti;
    ArrayList<Mesaj> mesaje;

    public ConversatiePrivata(int IDConversatiePrivata, ArrayList<Cont> participanti, ArrayList<Mesaj> mesaje) {
        this.IDConversatiePrivata = IDConversatiePrivata;
        this.participanti = participanti;
        this.mesaje = mesaje;
    }

    public int getIDConversatiePrivata() {
        return IDConversatiePrivata;
    }
}
