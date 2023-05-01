package com.example.socialskillup1;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class GroupSearchController {
    @FXML
    private ListView groupList;
    private Cont contCurent;
    public void setContCurent(Cont cc)
    {
        contCurent = cc;
    }

    public void paginaUpdate() throws SQLException {
        populeazaListaGrupuri();
    }

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
    public void handleGroupList(MouseEvent event) throws IOException, SQLException {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Object selectedItem = groupList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {//trei linii sa afle ce grup este de fapt selectat pentru ca listview este doar string-uri :((
                int i = groupList.getSelectionModel().getSelectedIndex();
                int grupID = contCurent.grupuri.get(i).getIDlinkedGrup();
                Grup grupCurent = Grup.lookupGrup(grupID);
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
