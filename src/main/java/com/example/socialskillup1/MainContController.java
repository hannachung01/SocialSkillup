package com.example.socialskillup1;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import java.util.EventObject;

public class MainContController {
    @FXML
    private ListView<String> groupList;
    @FXML
    private ListView<String> prietenList;

    @FXML
    private ListView<String> conversatieList;
    @FXML
    private TextField newGroupField;

    @FXML
    private Label usernameLabel, levelLabel;

    @FXML
    private ImageView pozaprofil;
    @FXML
    private Stage stg;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    //ca sa aiba acces la contCurent de la login
    private Cont contCurent;
    private Grup grupCurent;
    private ArrayList<Grup> toateGrupurile;
    private Settings settingsController;
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
    public void initialize() throws SQLException, IOException {
        //nu putem sa incarcam numele utilizatorului inca, pentru ca inca nu pot sa fac pass de datele, am creat updateInfo()
        Grup.populeazaToateGrupurile();
    }
    public void updateInfo() throws SQLException { //aici se incarca informatia personala despre contCurent
        String username = contCurent.getUsername();
        setUsername(username);
        Image pozaprofil = new Image(contCurent.getPozaPath());
        getPozaprofil().setImage(pozaprofil);
        getLevelLabel().setText(String.valueOf((int)contCurent.getXP()/100+1));
        contCurent.populeazaGrupuri(); //populeaza lista de grupuri la care apartin utilizatorul actual in cont
        populeazaListaGrupuri(); //folosind lista de grupuri de la utilizator, populeaza lista de grupuri pe view
        contCurent.populeazaPrieteni();
        populeazaListaPrieteni();
        contCurent.populeazaConversatii();
        populeazaListaConversatii();
    }
    @FXML
    private void populeazaListaGrupuri() throws SQLException { //pune grupurile utilizatorului in ListView
        ObservableList<String> items = groupList.getItems();
        contCurent.populeazaGrupuri();
        for (MembruGrup m : contCurent.grupuri)
        {   int idGrup = m.getIDlinkedGrup();
            Grup g = Grup.lookupGrup(idGrup);
            items.add(g.getNumeGrup());
        }
    }

    @FXML
    private void populeazaListaPrieteni() throws SQLException{
        ObservableList<String> items = prietenList.getItems();
        contCurent.populeazaPrieteni();
        for (Cont prieten : contCurent.prieteni)
        {
            items.add(prieten.getUsername());
        }
    }

    @FXML
    private void populeazaListaConversatii() throws SQLException{
        ObservableList<String> items = conversatieList.getItems();
        contCurent.populeazaConversatii();
        for (ConversatiePrivata conversatie : contCurent.conversatii)
        {
            String participanti = "";
            for (Cont participant : conversatie.participanti)
            {
                if (participant.getIDUtilizator()!=contCurent.getIDUtilizator())
                    participanti += participant.getUsername();
            }
            items.add(participanti);
        }
    }
    @FXML
    public void laMesajView(ActionEvent e) throws IOException, SQLException {
        FXMLLoader mesajView = new FXMLLoader(Main.class.getResource("mesajview.fxml"));
        Scene scene = new Scene(mesajView.load());
        MesajController mc = mesajView.getController();
        mc.setContCurent(contCurent);
        mc.updateInfo();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void laGroup(ActionEvent e) throws IOException, SQLException {
        FXMLLoader groupGeneralView = new FXMLLoader(Main.class.getResource("groupsearch.fxml"));
        Scene scene = new Scene(groupGeneralView.load());
        GroupSearchController gsc = groupGeneralView.getController();
        gsc.setContCurent(contCurent);
        gsc.paginaUpdate();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void laOameni(ActionEvent e) throws IOException, SQLException {
        FXMLLoader personSearchView = new FXMLLoader(Main.class.getResource("personsearch.fxml"));
        Scene scene = new Scene(personSearchView.load());
        PersonSearchController psc = personSearchView.getController();
        psc.setContCurent(contCurent);
        psc.paginaUpdate();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleGroupList(MouseEvent event) throws IOException, SQLException {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            String selectedItem = groupList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {//trei linii sa afle ce grup este de fapt selectat pentru ca listview este doar string-uri :((
                int i = groupList.getSelectionModel().getSelectedIndex();
                int grupID = contCurent.grupuri.get(i).getIDlinkedGrup();
                grupCurent = Grup.lookupGrup(grupID);
                FXMLLoader grupMain = new FXMLLoader(Main.class.getResource("group.fxml"));
                Scene groupScene = new Scene(grupMain.load());
                GroupController gc = grupMain.getController();
                gc.setContGrup(contCurent, grupCurent);
                gc.updateInfo();
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(groupScene);
                window.show();
            }
        }
    }

    @FXML
    public void handleConversatieList(MouseEvent event) throws IOException, SQLException {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            String selectedItem = conversatieList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int i = conversatieList.getSelectionModel().getSelectedIndex();
                int convID = contCurent.conversatii.get(i).getIDConversatiePrivata();
                ConversatiePrivata convCurent = contCurent.lookupConversatie(convID);
                FXMLLoader mesajView = new FXMLLoader(Main.class.getResource("mesajview.fxml"));
                Scene groupScene = new Scene(mesajView.load());
                MesajController mc = mesajView.getController();
                mc.setContCurent(contCurent);
                mc.setConvCurent(convCurent);
                mc.updateInfo();
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(groupScene);
                window.show();
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
    @FXML
    private void switchToSettings(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Parent settingsParent = loader.load();
        Settings settingsController = loader.getController();
        settingsController.setAccountInfo(contCurent);
        String pozapath = contCurent.getPozaPath();
        settingsController.setProfileImage(pozapath);
        Scene settingsScene = new Scene(settingsParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(settingsScene);
        window.show();
    }
}
