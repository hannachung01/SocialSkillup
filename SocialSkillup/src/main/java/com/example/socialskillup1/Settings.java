package com.example.socialskillup1;

//import com.sun.javafx.menu.MenuItemBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Settings {
    @FXML
    private TextField usernameField, emailField;
    @FXML
    private Button saveUsernameButton, savePasswordButton, saveEmailButton, setImageButton, cancelButton;
    @FXML
    private Label userLabel, passwordLabel, emailLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView pozaProfil;

    Cont cont;

    public void setAccountInfo(Cont contCurent, ImageView pozaprofil) {
        usernameField.setText(contCurent.getUsername());
        cont=contCurent;


        saveUsernameButton.setOnAction(event -> {
            String newUsername = usernameField.getText();

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:Conturi.db");
                String updateQuery = "UPDATE Conturi SET Username = ? WHERE IDUtilizator = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setString(1, newUsername);
                preparedStatement.setInt(2, contCurent.getIDUtilizator());
                preparedStatement.executeUpdate();

                contCurent.setUsername(newUsername);
                userLabel.setText("Name changed successfully!");
                userLabel.setTextFill(Color.GREEN);
                userLabel.setVisible(true);
                conn.close();
            } catch (SQLException e) {
                if (e.getErrorCode() == 19) {
                    userLabel.setText("Username already exists!");
                    userLabel.setTextFill(Color.RED);
                    userLabel.setVisible(true);
                } else {
                    e.printStackTrace();
                }
            }
        });

        saveEmailButton.setOnAction(event -> {
            String newEmail = emailField.getText();

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:Conturi.db");
                String updateQuery = "UPDATE Conturi SET Email = ? WHERE IDUtilizator = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setString(1, newEmail);
                preparedStatement.setInt(2, contCurent.getIDUtilizator());
                preparedStatement.executeUpdate();


                emailLabel.setText("Email changed successfully!");
                emailLabel.setTextFill(Color.GREEN);
                emailLabel.setVisible(true);
                conn.close();
            } catch (SQLException e) {
                if (e.getErrorCode() == 19) {
                    emailLabel.setText("Email already exists!");
                    emailLabel.setTextFill(Color.RED);
                    emailLabel.setVisible(true);
                } else {
                    e.printStackTrace();
                }
            }
        });


        savePasswordButton.setOnAction(event -> {
            String newPassword = passwordField.getText();
            if(newPassword.length() < 4) {
                passwordLabel.setText("Password is too short!");
                passwordLabel.setTextFill(Color.RED);
                passwordLabel.setVisible(true);
                return;
            }
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:Conturi.db");
                String updateQuery = "UPDATE Conturi SET Parola = ? WHERE IDUtilizator = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setString(1, newPassword);
                preparedStatement.setInt(2, contCurent.getIDUtilizator());
                preparedStatement.executeUpdate();


                passwordLabel.setText("Password changed successfully!");
                passwordLabel.setTextFill(Color.GREEN);
                passwordLabel.setVisible(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        setImageButton.setOnAction(event -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        // Filter
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:Conturi.db");
                String updateQuery = "UPDATE Conturi SET Poza = ? WHERE IDUtilizator = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setString(1, imagePath);
                preparedStatement.setInt(2, cont.getIDUtilizator());
                preparedStatement.executeUpdate();
                conn.close();

                cont.setPozaPath(imagePath);

                Image image = new Image(imagePath);
                pozaProfil.setImage(image);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        });


    }


    @FXML
    private void switchToMainCont(ActionEvent e) throws IOException, SQLException {
        FXMLLoader mainCont = new FXMLLoader(Main.class.getResource("maincont.fxml"));
        Scene scene = new Scene(mainCont.load());
        MainContController mcc = mainCont.getController();
        mcc.setContCurent(cont);
        mcc.updateInfo();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
