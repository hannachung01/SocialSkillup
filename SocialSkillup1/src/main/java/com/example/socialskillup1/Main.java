package com.example.socialskillup1;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.image.*;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main extends Application {
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader root = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene intro = new Scene(root.load(), Color.BLACK);
        Image icon = new Image("logo.jpg");
        stage.getIcons().add(icon);
        stage.setWidth(300);
        stage.setHeight(500);
        stage.setTitle("Welcome to SocialSkillup!");
        stage.setScene(intro);
        stage.show();
        PauseTransition pauza = new PauseTransition(Duration.seconds(2));
        pauza.play();
        FXMLLoader fl2 = new FXMLLoader(Main.class.getResource("login.fxml"));;
        Scene scene2 = new Scene(fl2.load(), 320, 500);
        pauza.setOnFinished(event -> {
            stage.setTitle("Login");
            stage.setScene(scene2);
        });
    }
}
