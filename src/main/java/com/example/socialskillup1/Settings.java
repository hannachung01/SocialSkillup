package com.example.socialskillup1;

//import com.sun.javafx.menu.MenuItemBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Settings {
    @FXML
    private TextField usernameField, emailField;
    @FXML
    private Button saveUsernameButton, savePasswordButton, saveEmailButton, cancelButton;
    @FXML
    private Label userLabel, passwordLabel, emailLabel;
    @FXML
    private PasswordField passwordField;

    public void setAccountInfo(Cont contCurent) {
        usernameField.setText(contCurent.getUsername());

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

                contCurent.setEmail(newEmail);
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

                contCurent.setPassword(newPassword);
                passwordLabel.setText("Password changed successfully!");
                passwordLabel.setTextFill(Color.GREEN);
                passwordLabel.setVisible(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        /*cancelButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("maincont.fxml"));
                Parent root = loader.load();

                MainContController mainContController = loader.getController();

                mainContController.setContCurent(contCurent);

                Scene scene = new Scene(root);
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });*/
    }

    public void setProfileImage(String pozaprofil) {
    }

    @FXML
    private void switchToMainCont(ActionEvent event) throws IOException {
        Parent mainContParent = FXMLLoader.load(getClass().getResource("maincont.fxml"));
        Scene mainContScene = new Scene(mainContParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(mainContScene);
        currentStage.show();
    }

}
