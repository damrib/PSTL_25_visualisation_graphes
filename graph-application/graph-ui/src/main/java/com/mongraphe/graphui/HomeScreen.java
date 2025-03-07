package com.mongraphe.graphui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mongraphe/graphui/home_screen.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Accueil");
        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
