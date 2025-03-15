package com.mongraphe.graphui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenController {

    @FXML private Hyperlink newProjectLink;
    @FXML private Hyperlink openFileLink;
    
    @FXML
    private void handleNewProject() {
        System.out.println("Nouveau projet créé!");
        // Ajouter ici le chargement d'une nouvelle scène pour un nouveau projet
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers Graphiques", "*.csv", "*.dot")
        );
        Stage stage = (Stage) openFileLink.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            ouvrirFenetreGraphe(stage, selectedFile);
        }
    }

    private void ouvrirFenetreGraphe(Stage stage, File fichier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChoixSimilitude.fxml"));
            Parent root = loader.load();

            // Passer le fichier au contrôleur de FenetreGraphe
            ChoixSimilitudeController controller = loader.getController();
            controller.setFichier(fichier);

            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("Mesure de similarité");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
