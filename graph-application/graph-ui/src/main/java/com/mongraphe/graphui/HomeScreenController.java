package com.mongraphe.graphui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

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
        	    new FileChooser.ExtensionFilter("Graph Files", "*.csv", "*.gexf", "*.gml", "*.graphml")
        	);

        Stage stage = (Stage) openFileLink.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            openFenetreGraphe(selectedFile);
        } else {
            System.out.println("Aucun fichier sélectionné.");
        }
    }

    private void openFenetreGraphe(File file) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/fxml/FenetreGraphe.fxml"));
            Scene scene = new Scene(loader.load());

            // Récupérer le contrôleur et passer le fichier sélectionné
            FenetreGrapheController controller = loader.getController();
            controller.setFileName(file.getName());

            // Création d'une nouvelle fenêtre (Stage)
            Stage fenetreGrapheStage = new Stage();
            fenetreGrapheStage.setTitle("Résultat du graphe");
            fenetreGrapheStage.setScene(scene);
            controller.setStage(fenetreGrapheStage);

            // Affichage de la nouvelle fenêtre
            fenetreGrapheStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
