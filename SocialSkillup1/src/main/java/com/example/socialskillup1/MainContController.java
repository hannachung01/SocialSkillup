package com.example.socialskillup1;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainContController {
    @FXML
    private ListView<String> groupList;
    @FXML
    private TextField newGroupField;

    @FXML
    private Label usernameLabel, levelLabel;

    @FXML
    private ImageView pozaprofil;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    //ca sa aiba acces la contCurent de la login
    private Cont contCurent;
    private ArrayList<Grup> toateGrupurile;
    public void setContCurent(Cont contCurent)
    {
        this.contCurent = contCurent;
    }

    public Label getLevelLabel() {
        return levelLabel;
    }

    public ImageView getPozaprofil() {
        return pozaprofil;
    }


    //Special method called when the controller is loaded
    public void initialize() throws SQLException {
        //nu putem sa incarcam numele utilizatorului inca, pentru ca inca nu pot sa fac pass de datele, am creat updateInfo()
        Grup.populeazaToateGrupurile();
    }
    public void updateInfo() throws SQLException { //aici se incarca informatia personala despre contCurent
        String username = contCurent.getUsername();
        setUsername(username);
        Image pozaprofil = new Image("profil1.png");
        getPozaprofil().setImage(pozaprofil);
        getLevelLabel().setText(String.valueOf(contCurent.getNivel()));
        contCurent.populeazaGrupuri(); //populeaza lista de grupuri la care apartin utilizatorul actual in cont
        System.out.println("Finished generating user's groups.");
        populeazaListaGrupuri(); //folosind lista de grupuri de la utilizator, populeaza lista de grupuri pe view
        System.out.println("Added user's groups to view.");
    }
    @FXML
    private void populeazaListaGrupuri() throws SQLException {
        ObservableList<String> items = groupList.getItems();
        contCurent.populeazaGrupuri();
        for (MembruGrup m : contCurent.grupuri)
        {   int idGrup = m.getIDlinkedGrup();
            Grup g = Grup.lookupGrup(idGrup);
            items.add(g.getNumeGrup());
        }
    }

    @FXML
    private void addGroupToList() {
        //va trebui sa salvam grupurile
        ObservableList<String> items = groupList.getItems();
        String newGroup = newGroupField.getText().trim();
        if (!newGroup.isEmpty() && !items.contains(newGroup)) {
            items.add(newGroup);
            newGroupField.clear();
        }
    }

    @FXML
    public void handleGroupList(MouseEvent event) throws IOException {
        //temporar
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            String selectedItem = groupList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("group.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setWidth(300);
                stage.setHeight(500);
                stage.show();
            }
        }
    }
}
