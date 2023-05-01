package com.example.socialskillup1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GrupSocialController {
    @FXML
    private Label groupName;
    @FXML
    private Label goalText;
    @FXML
    private Label deadlineText;
    @FXML
    private ImageView pozaprofil;

    Cont contCurent;
    Grup grupCurent;
    public void setVariables(Cont cc, Grup gc)
    {
        contCurent = cc;
        grupCurent = gc;
    }
    public void updateInfo()
    {
        groupName.setText(grupCurent.getNumeGrup());
        String s = "Goal: " + grupCurent.getScop();
        goalText.setText(s);
        LocalDateTime deadline = grupCurent.getPanaCand();
        deadlineText.setText("By: " + deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        String path = grupCurent.getPozaGrup();
        Image im = new Image(path);
        pozaprofil.setImage(im);
    }
}
