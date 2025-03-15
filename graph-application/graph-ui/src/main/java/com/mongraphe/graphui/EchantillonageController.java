package com.mongraphe.graphui;

import java.io.File;

import javafx.fxml.FXML;
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
    private void handleNext() {
        System.out.println("Next clicked.");
        // Implémenter la logique pour passer à l'étape suivante
    }

    @FXML
    private void handleBack() {
        System.out.println("Back clicked.");
        // Implémenter la logique pour revenir en arrière
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
