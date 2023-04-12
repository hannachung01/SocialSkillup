package com.example.socialskillup1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import javafx.scene.paint.Color;

public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField userTextBox, nameTextBox, emailTextBox;
    @FXML
    private PasswordField pwTextBox, pw2TextBox;
    @FXML
    private Label msg;
    @FXML
    private TextField userLoginTextBox;

    public void switchToSignup(ActionEvent e) throws IOException
    {
        FXMLLoader signup = new FXMLLoader(Main.class.getResource("signup.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(signup.load());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLogin(ActionEvent e) throws IOException
    {
        FXMLLoader login = new FXMLLoader(Main.class.getResource("login.fxml"));
        scene = new Scene(login.load());
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    public void checkLogin(ActionEvent e) throws IOException
    {

        /* Aici o sa redirectioneze la pagina maincont unde sa avem cont loghat.
        FXMLLoader login = new FXMLLoader(Main.class.getResource("maincont.fxml"));
        scene = new Scene(login.load());
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();*/

    }

    public boolean checkPassword(String pw1, String pw2)
    {
        if (!pw1.equals(pw2))
        {
            msg.setText("Passwords don't match.");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
            return false;
        }
        //code else if to check password is at least 4 characters or any other verification you want.
        return true;
    }

    public void checkSignup(ActionEvent e) throws IOException, ClassNotFoundException, SQLException
    {
        Connection conn;
        Class.forName("org.sqlite.JDBC"); //trebuie sa adaugam sqlite-jdbc-3.41.2.1.jar in proiect
        String query = "SELECT * FROM Conturi WHERE Username=?";
        conn = DriverManager.getConnection("jdbc:sqlite:conturi.db"); //trebuie sa pune jdbc:sqlite: in fata de path
        String username = userTextBox.getText().toString();
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, username);
        ResultSet rezultate =pst.executeQuery();
        String pw = pwTextBox.getText().toString();
        String pw2 = pw2TextBox.getText().toString();
        if (rezultate.next() == true)
        {
            msg.setText("Username is already in use.");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
        }
        else if (userTextBox.getText().toString() == null || nameTextBox.getText().toString() == null || emailTextBox.getText().toString() == null)
        {
            msg.setText("Please fill in all the fields.");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
        }
        else if (checkPassword(pw, pw2))
        {
            String update = "INSERT INTO Conturi(Username, Nume, Parola, Email) VALUES(?, ?, ?, ?)";
            pst = conn.prepareStatement(update);
            pst.setString(1, username);
            pst.setString(2, nameTextBox.getText().toString());
            pst.setString(3, pwTextBox.getText().toString());
            pst.setString(4, emailTextBox.getText().toString());
            pst.executeUpdate();
            msg.setText("Account created.");
            msg.setTextFill(Color.GREEN);
            msg.setVisible(true);
        }
        rezultate.close();
        pst.close();
       conn.close();
    }
}