package com.mongraphe.graphui;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EchantillonageController {

    @FXML
    private TextArea infoTextArea;
    @FXML
    private TextField upThresholdField;
    @FXML
    private TextField downThresholdField;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;
    @FXML
    private Button cancelButton;

    private File fichier;
    private int measureCode;

    /**
     * Initialise la fenêtre avec les données passées depuis ChoixSimilitudeController.
     */
    public void initData(File fichier, int measureCode) {
        this.fichier = fichier;
        this.measureCode = measureCode;

        // Affichage des informations dans le TextArea
        infoTextArea.setText("Mesure de similarité sélectionnée: " + fichier + 
                             "\nCode associé: " + measureCode + 
                             "\n\nVeuillez entrer les seuils pour l'échantillonnage.");
    }


    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChoixSimilitude.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer les données
            ChoixSimilitudeController controller = loader.getController();
            controller.setFichier(fichier); // Renvoyer le fichier pour éviter de le perdre

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Mesure de similarité");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNext() {
        try {
            double upThreshold = Double.parseDouble(upThresholdField.getText());
            double downThreshold = Double.parseDouble(downThresholdField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Clustering.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer les informations
            ClusteringController controller = loader.getController();
            controller.initData(fichier, measureCode, upThreshold, downThreshold);

            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setTitle("Clustering");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (NumberFormatException e) {
            infoTextArea.appendText("\n\nErreur: Veuillez entrer des valeurs numériques valides.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        // Fermer la fenêtre
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleConfirm() {
        try {
            double upThreshold = Double.parseDouble(upThresholdField.getText());
            double downThreshold = Double.parseDouble(downThresholdField.getText());

            // Mise à jour du TextArea avec les seuils saisis
            infoTextArea.appendText("\nSeuils sélectionnés:");
            infoTextArea.appendText("\n - Up threshold: " + upThreshold);
            infoTextArea.appendText("\n - Down threshold: " + downThreshold);

            System.out.println("Seuils confirmés: Up=" + upThreshold + ", Down=" + downThreshold);

            // Ajouter ici la logique de traitement avec les seuils

        } catch (NumberFormatException e) {
            infoTextArea.appendText("\n\nErreur: Veuillez entrer des valeurs numériques valides.");
        }
    }
}
