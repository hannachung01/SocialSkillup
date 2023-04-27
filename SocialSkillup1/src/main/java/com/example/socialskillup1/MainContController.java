package com.example.socialskillup1;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainContController {
    @FXML
    private ListView<String> groupList;
    @FXML
    private TextField newGroupField;

    @FXML
    private Label usernameLabel, levelLabel;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    //Special method called when the controller is loaded
    public void initialize() {
        //aici o sa punem numele utilizatorului
        String username = "Name";
        setUsername(username);
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
