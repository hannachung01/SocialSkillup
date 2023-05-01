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
        import javafx.scene.layout.AnchorPane;
        import javafx.scene.layout.Pane;
        import javafx.stage.Stage;
        import org.sqlite.SQLiteConnection;
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
    private TextField pCautat;
    @FXML
    private TextArea mesajCamp;
    /*@FXML
    private ListView<ConversatiePrivata> listView;*/
    public void setContCurent(Cont c)
    {
        contCurent = c;
    }
    public void setConvCurent(ConversatiePrivata c) {convCurent= c;};
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
        if (convCurent != null) incarcaConvo();
        conversatieList.setOnMouseClicked(event -> {
            if(event.getClickCount() ==2)
            {
                int index = conversatieList.getSelectionModel().getSelectedIndex();
                String nouNotificare = notificare.getText();
                if (index >= 0) {
                    convCurent = contCurent.conversatii.get(index);
                    try {
                        incarcaConvo();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        );
    }

    public void incarcaConvo() throws SQLException {
            String nouNotificare = "You are chatting with ";
            if (convCurent.participanti.get(0).getIDUtilizator() != contCurent.getIDUtilizator()) {
                nouNotificare = nouNotificare + convCurent.participanti.get(0).getName();
            } else nouNotificare = nouNotificare + convCurent.participanti.get(1).getName();
            Image im = new Image(convCurent.participanti.get(0).getPozaPath());
            imageCurent.setImage(im);
            Image im2 = new Image(convCurent.participanti.get(1).getPozaPath());
            imageAltul.setImage(im2);
            updateConvo();
         notificare.setText(nouNotificare);
    }

    public void updateConvo() throws SQLException {
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
        convCurent = null;
        mcc.setContCurent(contCurent);
        mcc.updateInfo();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void populeazaListaConversatii() throws SQLException{
        conversatieList.getItems().clear();
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
            conn2.close();
        }
    }

    @FXML
    public void handleConversatieList(MouseEvent event) throws IOException, SQLException {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            String selectedItem = conversatieList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int i = conversatieList.getSelectionModel().getSelectedIndex();
                int convID = contCurent.conversatii.get(i).getIDConversatiePrivata();
                convCurent = contCurent.lookupConversatie(convID);
                populeazaListaConversatii();
            }
        }
    }


    @FXML
    private void cautaPersoana(ActionEvent e) throws SQLException {
        int nouConversatie=0;
        String cautat = pCautat.getText();
        String query = "SELECT * FROM Conturi WHERE Nume = ?"; //mai intai, cautam persoana si extragem datele pe care sunt strict necesare.
        Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, cautat);
        ResultSet rs = pst.executeQuery();
        int idCautat = 0;
        if (rs.next()) {
            idCautat = rs.getInt("IDUtilizator");
            String poza = rs.getString("Poza");
        }
        rs.close();
        pst.close();
        if (idCautat == 0) notificare.setText("We couldn't find this person in our records.");
        else if (idCautat == contCurent.getIDUtilizator()) notificare.setText("Stop trying to talk to yourself.");
        else {
            Cont altul = Cont.lookupCont(idCautat);
            String path =altul.getPozaPath();
            imageAltul.setImage(new Image(path));
            notificare.setText("You are chatting with " + altul.getName());
            PreparedStatement pst2;
            //acum verificam daca deja avem o conversatie cu ea
            String query2 = "SELECT * FROM ConversatiiPrivateParticipanti WHERE IDConversatie IN (SELECT IDConversatie FROM ConversatiiPrivateParticipanti WHERE IDParticipant = ?) AND IDParticipant = ?";
            pst2 = conn.prepareStatement(query2);
            pst2.setString(1, String.valueOf(idCautat));
            pst2.setString(2, String.valueOf(contCurent.getIDUtilizator()));
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                int idconv = rs2.getInt("IDConversatie");
                convCurent = contCurent.lookupConversatie(idconv);
                if (convCurent != null) incarcaConvo();
              }
            else {
                String query4 = "SELECT MAX(IDConversatie) FROM ConversatiiPrivateParticipanti";
                Statement st = conn.createStatement();
                ResultSet rs4 = st.executeQuery(query4);
                nouConversatie = rs4.getInt(1);
                rs4.close();
                st.close();
            }
            pst2.close();
            rs2.close();
        }
        conn.close();
        if (nouConversatie != 0) //am facut separat ca sa nu am problema de blocaj ca este deschis SELECT in timp ce incerc INSERT
        {
             //prima oara, trebuie sa creeze campuri pe IDConversatie in baza de date si Cont.conversatii in program
            Connection conn2 = DriverManager.getConnection("jdbc:sqlite:conturi.db");
            String query3 = "INSERT INTO ConversatiiPrivateParticipanti (IDConversatie, IDParticipant) VALUES ((SELECT MAX(IDConversatie)+1 FROM ConversatiiPrivateParticipanti), ?), ((SELECT MAX(IDConversatie)+1 FROM ConversatiiPrivateParticipanti), ?)";
            PreparedStatement ps3 = conn2.prepareStatement(query3);
            ps3.setString(1, String.valueOf(idCautat));
            ps3.setString(2, String.valueOf(contCurent.getIDUtilizator()));
            ps3.executeUpdate();
            ps3.close();
            ArrayList<Cont> participanti = new ArrayList<>();
            participanti.add(Cont.lookupCont(idCautat));
            participanti.add(contCurent);
            ArrayList <Mesaj> mesaje = new ArrayList<>();
            ConversatiePrivata n = new ConversatiePrivata(nouConversatie, participanti, mesaje);
            contCurent.conversatii.add(n);
            convCurent = contCurent.conversatii.get(contCurent.conversatii.size()-1);
            populeazaListaConversatii();
            updateConvo();
            conn2.close();
        }
    }
}

