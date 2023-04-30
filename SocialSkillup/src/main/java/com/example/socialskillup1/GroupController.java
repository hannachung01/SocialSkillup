package com.example.socialskillup1;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Stage;

public class GroupController {
    @FXML
    private Label groupName;
    @FXML
    private Label goalText;
    @FXML
    private Label deadlineText;
    @FXML
    private ImageView pozaprofil;
    @FXML
    private ListView listaMembri;

    @FXML
    public void showSettings() {

    }

    @FXML
    public void showGroupChat() {

    }

    private Cont contCurent;
    private Grup grupCurent;

    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        Parent loginParent = (Parent)FXMLLoader.load(this.getClass().getResource("maincont.fxml"));
        Scene loginScene = new Scene(loginParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void setContGrup(Cont cc, Grup cg)
    {
        contCurent = cc;
        grupCurent = cg;
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

    public void populeazaListaMembrii() throws SQLException {
        ObservableList<String> items = listaMembri.getItems();
        grupCurent.populeazaMembriiLista();
        for (MembruGrup m : grupCurent.membri) items.add(m.getName());
    }

    public void handleBack() throws IOException, SQLException {
        FXMLLoader maincont = new FXMLLoader(Main.class.getResource("maincont.fxml"));
        Scene groupScene = new Scene(maincont.load());
        MainContController mcc = maincont.getController();
        mcc.setContCurent(contCurent);
        mcc.updateInfo();
    }

    public void toSocial() throws IOException {
        FXMLLoader social = new FXMLLoader(Main.class.getResource("socialpage.fxml"));
        Scene socialscene = new Scene(social.load());
        GrupSocialController gsc = social.getController();
        gsc.setVariables(contCurent, grupCurent);

    }
}
