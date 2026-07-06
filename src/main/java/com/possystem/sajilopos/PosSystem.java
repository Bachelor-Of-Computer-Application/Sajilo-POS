package com.possystem.sajilopos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PosSystem extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/auth/login.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Sajilo POS");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(600);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}