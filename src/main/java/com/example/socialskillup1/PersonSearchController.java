package com.example.socialskillup1;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import javafx.scene.control.Label;

public class PersonSearchController {
    @FXML
    private ListView groupList;
    @FXML
    private Label Name;
    @FXML
    private Label nivelLabel;
    @FXML
    private Label descriereLabel;
    @FXML
    private ImageView pozaProfil;
    @FXML
    private ListView friendList;

    private Cont contCurent;
    private Grup grupCurent;
    private Cont contCautat;
    public void setContCurent(Cont cc)
    {
        contCurent = cc;
    }

    public void setContCautat(Cont cc) {contCautat = cc;}
    public void paginaUpdate() throws SQLException {
        if (contCautat != null)
        {
            Name.setText(contCautat.getUsername());
            nivelLabel.setText("Nivel: " + Integer.toString((int)(contCautat.getXP()/100)+1));
            descriereLabel.setText(contCautat.getDescriere());
            Image im = new Image(contCautat.getPozaPath());
            pozaProfil.setImage(im);
        }
        populeazaListaPrieteni();

    }

    private void populeazaListaPrieteni() throws SQLException { //pune grupurile utilizatorului in ListView
        ObservableList<String> items = friendList.getItems();
        items.clear();
        contCurent.populeazaPrieteni();
        for (Cont c : contCurent.prieteni)
        {
            items.add(c.getUsername());
        }
    }
    @FXML
    public void handleBlock(ActionEvent e) throws SQLException {
        int i = friendList.getSelectionModel().getSelectedIndex();
        if (i >= 0)
        {
            contCautat = contCurent.prieteni.get(i);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
            String query1= "UPDATE Relatii SET estePrieten = -1 WHERE (IDContPrincipal = "+contCurent.getIDUtilizator() + " AND IDContAltuia = " + contCautat.getIDUtilizator() + " AND estePrieten = 1);";
            Statement st = conn.createStatement();
            st.executeUpdate(query1);
            String query2= "UPDATE Relatii SET estePrieten = 0 WHERE (IDContPrincipal = "+contCautat.getIDUtilizator() + " AND IDContAltuia = " + contCurent.getIDUtilizator() + " AND estePrieten = 1);";
            st.executeUpdate(query2);
            st.close();
            conn.close();
            populeazaListaPrieteni();//cumva merge, dar este un exception thrown de tip argument mismatch... nu stiu de unde... dar programul continua sa ruleze bine
        }
    }
    @FXML
    public void handleDelete(ActionEvent e) throws SQLException {
        int i = friendList.getSelectionModel().getSelectedIndex();
        if (i >= 0) contCautat = contCurent.prieteni.get(i);
            //stergem din lista de prieteni in db, trebuie sa stergem ambele inregistrari
            Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
            String query = "DELETE FROM Relatii WHERE (IDContPrincipal = ? AND IDContAltuia = ? AND estePrieten = 1) OR (IDContPrincipal = ? AND IDContAltuia = ? AND estePrieten = 1)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, Integer.toString(contCurent.getIDUtilizator()));
            pst.setString(2, Integer.toString(contCautat.getIDUtilizator()));
            pst.setString(3, Integer.toString(contCautat.getIDUtilizator()));
            pst.setString(4, Integer.toString(contCurent.getIDUtilizator()));
            int c = pst.executeUpdate();
            pst.close();
            conn.close();
            populeazaListaPrieteni(); //automat se schimba dupa cum este la baza de date
    }

    @FXML
    public void handleFriendList(MouseEvent event) throws IOException, SQLException {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Object selectedItem = friendList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int i = friendList.getSelectionModel().getSelectedIndex();
                contCautat = contCurent.prieteni.get(i);
                Name.setText(contCautat.getUsername());
                nivelLabel.setText("Nivel: " + Integer.toString(((int)(contCautat.getXP())/100+1)));
                descriereLabel.setText(contCautat.getDescriere());
                Image im = new Image(contCautat.getPozaPath());
                pozaProfil.setImage(im);
            }
        }
    }
    @FXML
    public void mesajPrieten(ActionEvent e) throws IOException, SQLException {
            int i = friendList.getSelectionModel().getSelectedIndex();
            if (i >= 0)
            {
                contCautat = contCurent.prieteni.get(i);
                FXMLLoader mesajView = new FXMLLoader(Main.class.getResource("mesajview.fxml"));
                Scene scene = new Scene(mesajView.load());
                MesajController mc = mesajView.getController();
                mc.setContCurent(contCurent);
                for (ConversatiePrivata c: contCurent.conversatii)
                {
                    for (Cont cont : c.participanti)
                    {
                        if (cont.getIDUtilizator() == contCautat.getIDUtilizator())
                            mc.setConvCurent(c);
                    }
                }
                mc.updateInfo();
                Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            else
            {
                FXMLLoader mesajView = new FXMLLoader(Main.class.getResource("mesajview.fxml"));
                Scene scene = new Scene(mesajView.load());
                MesajController mc = mesajView.getController();
                mc.setContCurent(contCurent);
                mc.updateInfo();
                Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }

    }
    public void inapoiLaMain(ActionEvent e) throws IOException, SQLException {
        FXMLLoader mainCont = new FXMLLoader(Main.class.getResource("maincont.fxml"));
        Scene scene = new Scene(mainCont.load());
        MainContController mcc = mainCont.getController();
        mcc.setContCurent(contCurent);
        mcc.updateInfo();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void switchToSettings(ActionEvent event) throws IOException {
        String pozaprofil = contCurent.getPozaPath();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Parent settingsParent = loader.load();
        Settings settingsController = loader.getController();
        settingsController.setAccountInfo(contCurent);
        settingsController.setProfileImage(pozaprofil);
        Scene settingsScene = new Scene(settingsParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(settingsScene);
        window.show();
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
}
