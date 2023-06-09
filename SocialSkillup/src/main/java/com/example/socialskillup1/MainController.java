package com.example.socialskillup1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

import javafx.scene.paint.Color;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField userTextBox, nameTextBox, emailTextBox;
    @FXML
    private PasswordField pwTextBox, pw2TextBox;
    @FXML
    private Label msg, msg2;
    @FXML
    private TextField userLoginTextBox, parolaLoginTextBox;

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

    public void checkLogin(ActionEvent e) throws IOException {
        String username = userLoginTextBox.getText();
        String password = parolaLoginTextBox.getText();
        String query = "SELECT * FROM Conturi WHERE Username = ? AND Parola = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Username and password match
                // Aici o sa redirectioneze la pagina maincont unde sa avem cont loghat.
                // Descarca datele pentru utilizator...
                /*
                int userID = rs.getInt("IDUtilizator");
                String un = rs.getString("Username");
                String nume = rs.getString("Nume");
                int nivel = rs.getInt("Nivel");
                String poza = rs.getString("Poza");
                Cont contCurent = new Cont(userID, un, nume, nivel, poza);*/
                Cont contCurent = new Cont(rs);
                FXMLLoader login = new FXMLLoader(Main.class.getResource("maincont.fxml"));;
                //ca sa trimita un obiect mai departe la celalalt controller
                scene = new Scene(login.load());
                MainContController mcc = login.getController();
                mcc.setContCurent(contCurent);
                mcc.updateInfo();
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                rs.close();
                pst.close();
                conn.close();
            } else {
                msg.setText("Username/password do not match");
                msg.setTextFill(Color.RED);
                msg.setVisible(true);
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
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
        else if (pw1.length() < 4)
        {
            msg.setText("Password is too short.");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
            return false;
        }
        return true;
    }

    public void checkSignup(ActionEvent e) throws IOException, ClassNotFoundException, SQLException
    {
        Connection conn;
        Class.forName("org.sqlite.JDBC"); //trebuie sa adaugam sqlite-jdbc-3.41.2.1.jar in proiect
        String query = "SELECT * FROM Conturi WHERE Username=?";
        conn = DriverManager.getConnection("jdbc:sqlite:conturi.db"); //trebuie sa pune jdbc:sqlite: in fata de path
        String username = userTextBox.getText().toString();
        String name = nameTextBox.getText().toString();
        String email = emailTextBox.getText().toString();
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, username);
        ResultSet rezultate = pst.executeQuery();
        String pw = pwTextBox.getText().toString();
        String pw2 = pw2TextBox.getText().toString();
        if (rezultate.next())
        {
            msg.setText("Username is already in use.");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
        }
        else if (username.length() == 0 || name.length() == 0 || email.length() == 0)
        {
            msg.setText("Please fill in all the fields.");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
        }
        else if (checkPassword(pw, pw2))
        {
            String update = "INSERT INTO Conturi(Username, Nume, Parola, Email, Nivel, Poza) VALUES(?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(update);
            pst.setString(1, username);
            pst.setString(2, nameTextBox.getText().toString());
            pst.setString(3, pwTextBox.getText().toString());
            pst.setString(4, emailTextBox.getText().toString());
            pst.setInt(5, 1);
            pst.setString(6, "profil1.png");
            pst.executeUpdate();
            msg.setText("Account created.");
            msg.setTextFill(Color.GREEN);
            msg.setVisible(true);
        }
        rezultate.close();
        pst.close();
       conn.close();
    }

    public class PasswordGenerator {
        private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final Random random = new Random();

        public static String generateRandomPassword(int length) {
            StringBuilder password = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            return password.toString();
        }
    }
    @FXML
    private void handleForgotPassword() {
        String usernameOrEmail = userLoginTextBox.getText().trim();
        if (usernameOrEmail.isEmpty()) {
            msg.setText("Username or Email required");
            msg.setTextFill(Color.RED);
            msg.setVisible(true);
            return;
        }

        // Query the database for the provided username or email
        String query = "SELECT * FROM Conturi WHERE Username = ? OR Email = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:conturi.db");
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, usernameOrEmail);
            pst.setString(2, usernameOrEmail);
            ResultSet result = pst.executeQuery();
            if (result.next()) {
                String email = result.getString("Email");
                //Generate a new random password for the user
                String newPwd = PasswordGenerator.generateRandomPassword(5);

                // Update the password in the db.
                String updateQuery = "UPDATE Conturi SET Parola = ? WHERE Email = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newPwd);
                    updateStmt.setString(2, email);
                    updateStmt.executeUpdate();
                }
                // Send email to user
                sendEmail(email, "Password Reset " + newPwd, "Your new password is: " + newPwd);
            } else {
                msg.setText("Username or Email is invalid");
                msg.setTextFill(Color.RED);
                msg.setVisible(true);
            }
            pst.close();
            conn.close();
            result.close();
        } catch (SQLException e) {
            System.out.println(e);
        }


    }

    private void sendEmail(String to, String subject, String body) {
        String from = "socialskillup@gmail.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            msg.setText("Email sent");
            msg.setTextFill(Color.GREEN);
            msg.setVisible(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}