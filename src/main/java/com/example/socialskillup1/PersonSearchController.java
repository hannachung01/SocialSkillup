package com.example.socialskillup1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.LocalDate.now;

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
    @FXML
    private ChoiceBox<String> filtrare;
    @FXML
    private TextField cautaCuvinte;
    @FXML
    private CheckBox check;
    @FXML
    private ListView<String> rezultate;
    @FXML
    private ListView<String> pending;
    @FXML
    private Button inviteButton;
    @FXML
    private Button blockButton;
    private Button inviteButton;
    @FXML
    private Button blockButton;
    private Cont contCurent;
    private Cont contCautat;
    public void setContCurent(Cont cc)
    {
        contCurent = cc;
    }

    public void setContCautat(Cont cc) {contCautat = cc;}

    public ArrayList<Cont> rez = new ArrayList<>(); //retin rezultatele pentru cautare ca sa pot sa postez detalii despre persoana
    public ArrayList<Cont> cereri = new ArrayList<>(); //retin informatie despre cereri

    public void paginaUpdate() throws SQLException {
        filtrare.getItems().addAll("Username", "Name", "Description");
        filtrare.setValue("Username");
        updateProfile();
        populeazaListaPrieteni();
        populeazaListPending();

    }

    public void updateProfile() throws SQLException {

        if (contCautat != null)
        {
            Name.setText(contCautat.getUsername());
            nivelLabel.setText("Nivel: " + Integer.toString(((int)(contCautat.getXP())/100+1)));
            descriereLabel.setText(contCautat.getDescriere());
            Image im = new Image(contCautat.getPozaPath());
            pozaProfil.setImage(im);
            int relatie = contCurent.ceRelatie(contCautat.getIDUtilizator());
            if (relatie == 2)
            {
                inviteButton.setDisable(true);
                inviteButton.setText("Friend Invite Sent");
                blockButton.setDisable(false);
                blockButton.setText("Block");
            }
            else if (relatie == 1)
            {
                inviteButton.setDisable(true);
                inviteButton.setText("Already Friends");

                blockButton.setDisable(false);
                blockButton.setText("Block");
            }
            else if (relatie ==-1)
            {
                inviteButton.setDisable(true);
                inviteButton.setText("Can't befriend blocked.");

            }
            else if (relatie ==-1)
            {
                blockButton.setDisable(true);
                blockButton.setText("Already blocked");
            }
            else
            {
                inviteButton.setDisable(false);
                inviteButton.setText("Send Friend Request");

            }
        }
        else
        {
            inviteButton.setDisable(false);
            inviteButton.setText("Send Friend Request");
            blockButton.setDisable(false);
            blockButton.setText("Block");


            }
        }
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

    private void populeazaListPending() throws SQLException {
        cereri.clear();
        ObservableList<String> items = pending.getItems();
        items.clear();
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        String query = "SELECT * FROM Relatii WHERE ESTEPRIETEN = 2 AND IDCONTALTUIA = ?";
        PreparedStatement pst =  conn.prepareStatement(query);
        pst.setString(1, Integer.toString(contCurent.getIDUtilizator()));
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Cont c = new Cont(rs);
            cereri.add(c);
            items.add(c.getUsername());
        }
    }

    @FXML
    public void cautare(ActionEvent e) throws SQLException
    {
        ObservableList<String> items = rezultate.getItems();
        items.clear();
        String cautat = cautaCuvinte.getText();
        LocalDateTime limita=LocalDateTime.now().minusWeeks(1);
        String query;
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        if (!cautat.isBlank()) {
            switch (filtrare.getSelectionModel().getSelectedIndex()) {
                case 0:
                    query = "SELECT * FROM Conturi WHERE Username = ?";
                    break;
                case 1:
                    query = "SELECT * FROM Conturi WHERE Nume =?";
                    break;
                case 2:
                    query = "SELECT * FROM Conturi"; // cautam folosind .contains in java, pentru ca momentan, SQL nu recunoaste % ca litere multiple
                    break;
                default:
                    query = "SELECT * FROM Conturi";
                    break;
            }
        }
        else query = "SELECT * FROM Conturi";
        PreparedStatement ps=conn.prepareStatement(query);
        Statement s;
        ResultSet rs1;
        if (filtrare.getSelectionModel().getSelectedIndex()==0 || filtrare.getSelectionModel().getSelectedIndex()==1)
        {
            ps.setString(1, cautat);
            rs1 = ps.executeQuery();
        }
        else
        {
            s=conn.createStatement();
            rs1=s.executeQuery(query);
        }
        while (rs1.next())  //acum filtru pentru timp daca este bifat
        {
            boolean exclus = false;
            int id;
            id = rs1.getInt("IDUtilizator");
            Cont c = Cont.lookupCont(id);
            if (check.isSelected()) {
                if (c.conversatii != null) {
                    int ultimulindex = c.conversatii.size() - 1;
                    int IDultimaconversatie = c.conversatii.get(ultimulindex).getIDConversatiePrivata();
                    ConversatiePrivata cp = c.lookupConversatie(IDultimaconversatie);
                    LocalDateTime recentMesaj = cp.mesaje.get(cp.mesaje.size() - 1).getTimestamp();
                    if (recentMesaj.compareTo(limita) < 0) exclus = true;
                }
            }
            if (contCurent.ceRelatie(id) == -1) exclus = true; //verific daca este blocat
            if (filtrare.getSelectionModel().getSelectedIndex() == 2) // verific daca este o parte de descriere
            {
                String des = rs1.getString("Descriere");
                if (des.contains(cautat)) exclus = false; else exclus = true;

              }
            if (exclus != true && id != contCurent.getIDUtilizator()) {
                rez.add(c);
                int i = rez.size() - 1;
                items.add(rez.get(i).getUsername());
            }
        }
            rs1.close();
            ps.close();
            conn.close();
        }

        @FXML
        public void handleRezultati(MouseEvent event) throws SQLException{
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Object selectedItem = rezultate.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    int i = rezultate.getSelectionModel().getSelectedIndex();
                    contCautat = rez.get(i);
                    updateProfile();
                }
            }
        }
    @FXML
    public void handlePending(MouseEvent event) throws SQLException{
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Object selectedItem = pending.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int i = pending.getSelectionModel().getSelectedIndex();
                contCautat = cereri.get(i);
                updateProfile();
            }
        }
    }
        @FXML
        public void handleRezultati(MouseEvent event) throws SQLException{
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Object selectedItem = rezultate.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    int i = rezultate.getSelectionModel().getSelectedIndex();
                    contCautat = rez.get(i);
                    updateProfile();
                }
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
            int r = st.executeUpdate(query1);
            String query2= "UPDATE Relatii SET estePrieten = 0 WHERE (IDContPrincipal = "+contCautat.getIDUtilizator() + " AND IDContAltuia = " + contCurent.getIDUtilizator() + " AND estePrieten = 1);";
            r= st.executeUpdate(query2);
            st.close();
            conn.close();
            populeazaListaPrieteni();//cumva merge, dar este un exception thrown de tip argument mismatch... nu stiu de unde... dar programul continua sa ruleze bine
            updateProfile();
        }
    }

    @FXML
    public void trimiteInvite(ActionEvent e) throws SQLException
    {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        String query1= "UPDATE Relatii SET estePrieten = 2 WHERE (IDContPrincipal = "+contCurent.getIDUtilizator() + " AND IDContAltuia = " + contCautat.getIDUtilizator();
        inviteButton.setDisable(true);
        inviteButton.setText("Friend Invite Sent");
        Statement st = conn.createStatement();
        int r= st.executeUpdate(query1);
        st.executeUpdate(query1);
        st.close();
        conn.close();
    }
    @FXML
    public void handleBlockOnProfile(ActionEvent e) throws SQLException {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
            String query1= "UPDATE Relatii SET estePrieten = -1 WHERE (IDContPrincipal = "+contCurent.getIDUtilizator() + " AND IDContAltuia = " + contCautat.getIDUtilizator() + " AND estePrieten = 1);";
            Statement st = conn.createStatement();
            int r= st.executeUpdate(query1);
            String query2= "UPDATE Relatii SET estePrieten = 0 WHERE (IDContPrincipal = "+contCautat.getIDUtilizator() + " AND IDContAltuia = " + contCurent.getIDUtilizator() + " AND estePrieten = 1);";
            r= st.executeUpdate(query2);
            st.executeUpdate(query1);
            String query2= "UPDATE Relatii SET estePrieten = 0 WHERE (IDContPrincipal = "+contCautat.getIDUtilizator() + " AND IDContAltuia = " + contCurent.getIDUtilizator() + " AND estePrieten = 1);";
            st.executeUpdate(query2);
            st.close();
            conn.close();
            populeazaListaPrieteni();//cumva merge, dar este un exception thrown de tip argument mismatch... nu stiu de unde... dar programul continua sa ruleze bine
            updateProfile();
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
                updateProfile();
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
