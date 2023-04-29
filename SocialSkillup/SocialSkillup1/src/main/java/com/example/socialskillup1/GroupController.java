package com.example.socialskillup1;

import java.io.IOException;
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
    private ImageView groupImageView;
    @FXML
    private Label groupNameLabel;
    @FXML
    private TextArea intentionsTextArea;
    @FXML
    private ListView<String> newsFeedListView;
    @FXML
    private Button settingsButton;
    @FXML
    private Button groupChatButton;



    @FXML
    public void addNewsFeedItem() {
        ObservableList<String> items = newsFeedListView.getItems();
        items.add("added new item");
    }

    @FXML
    public void showSettings() {

    }

    @FXML
    public void showGroupChat() {

    }

    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        Parent loginParent = (Parent)FXMLLoader.load(this.getClass().getResource("maincont.fxml"));
        Scene loginScene = new Scene(loginParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
}
