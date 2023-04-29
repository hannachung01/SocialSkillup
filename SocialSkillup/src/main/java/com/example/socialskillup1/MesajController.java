package com.example.socialskillup1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class MesajController {


    private Cont contCurent;
    private ConversatiePrivata convCurent;
    @FXML
    private ListView<String> mesajDisplay;
    @FXML
    private ListView<String> prietenList;
    @FXML
    private ListView<String> conversatieList;
    @FXML
    private Label notificare;
    @FXML
    private TextArea mesajCamp;
    /*@FXML
    private ListView<ConversatiePrivata> listView;*/
    public void setContCurent(Cont c)
    {
        contCurent = c;
    }
    @FXML
    public ImageView imageCurent;

    @FXML
    public ImageView imageAltul;
    public void updateInfo() throws SQLException {
        contCurent.populeazaConversatii();
        populeazaListaConversatii();
        //creeazaLista();
        Image imC = new Image(contCurent.getPozaPath());
        imageCurent.setImage(imC);
        conversatieList.setOnMouseClicked(event -> {
                 if(event.getClickCount() ==2)
                 {
                     int index = conversatieList.getSelectionModel().getSelectedIndex();
                     String nouNotificare = notificare.getText();
                     if (index >= 0) {
                         convCurent = contCurent.conversatii.get(index);
                         nouNotificare = "You are chatting with ";
                         if (convCurent.participanti.get(0).getIDUtilizator() != contCurent.getIDUtilizator())
                         {
                             nouNotificare = nouNotificare + convCurent.participanti.get(0).getName();
                         }
                         else nouNotificare = nouNotificare + convCurent.participanti.get(1).getName();
                         Image im = new Image(convCurent.participanti.get(0).getPozaPath());
                         imageCurent.setImage(im);
                         Image im2 = new Image(convCurent.participanti.get(1).getPozaPath());
                         imageAltul.setImage(im2);
                         updateConvo();
                         }
                     notificare.setText(nouNotificare);
                 }
            }
        );
    }
    public void updateConvo()
    {
        mesajDisplay.getItems().clear();
        for (Mesaj m : convCurent.mesaje) {
            ObservableList<String> items = mesajDisplay.getItems();
            items.add(m.shortString());
        }
        int lastIndex =mesajDisplay.getItems().size() - 1;
        mesajDisplay.scrollTo(lastIndex);
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
    private void adaugaMesaj() throws SQLException{
        if (convCurent == null) notificare.setText("Alege pe cineva ca sa ai o conversatie.");
        else {
            int senderID = contCurent.getIDUtilizator();
            String continut = mesajCamp.getText();
            LocalDateTime ts = LocalDateTime.now();
            Mesaj nouMesaj = new Mesaj(senderID, continut, ts);
            convCurent.mesaje.add(nouMesaj);
            mesajDisplay.getItems().add(nouMesaj.shortString());
            mesajDisplay.scrollTo(mesajDisplay.getItems().size()-1);
            mesajCamp.clear();
           // updateConvo();
            String query = "INSERT INTO MesajePrivate (IDConversatie, SenderID, Continut, Timestamp) VALUES(?,?,?,?)";
            Connection conn2 = DriverManager.getConnection("jdbc:sqlite:conturi.db");
            PreparedStatement pst = conn2.prepareStatement(query);
            pst.setString(1, String.valueOf(convCurent.IDConversatiePrivata));
            pst.setString(2,String.valueOf(senderID));
            pst.setString(3, String.valueOf(continut));
            pst.setString(4, String.valueOf(ts));
            pst.executeUpdate();
            pst.close();
        }
    }


/*
    @FXML
    private void creeazaLista() {
        ObservableList<ConversatiePrivata> cp = FXCollections.observableArrayList();
        for (ConversatiePrivata convo : contCurent.conversatii) cp.add(convo);
        ListView<ConversatiePrivata> listView = new ListView<ConversatiePrivata>(cp);
        listView.setCellFactory(new Callback<ListView<ConversatiePrivata>, ListCell<ConversatiePrivata>>() {
            @Override
            public ListCell<ConversatiePrivata> call(ListView<ConversatiePrivata> param) {
                return new ListCell<ConversatiePrivata>() {
                    @Override
                    protected void updateItem(ConversatiePrivata convo, boolean empty) {
                        super.updateItem(convo, empty);
                        if (empty || convo == null) {
                            setText(null);
                        } else {
                            String displayConvo = "";
                            for (Cont c : convo.participanti)
                            {
                                if (c.getIDUtilizator() != contCurent.getIDUtilizator())
                                    displayConvo = displayConvo + c.getUsername() + " ";
                            }
                            setText(displayConvo);
                        }
                    }
                };
            }
        });
        listView.setItems(cp);
    }
*/

    /*
    @FXML
    public void handleConversatieList(MouseEvent event) throws IOException {
        //temporar
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            int index = conversatieList.getSelectionModel().getSelectedIndex();
            System.out.println(index);
            if (index >= 0) {
                ConversatiePrivata cp = contCurent.conversatii.get(index);
                for (Mesaj m: cp.mesaje)
                {
                    ObservableList<String> items = mesajDisplay.getItems();
                    items.add(m.toString());
                }
            }
        }
    }*/
}
