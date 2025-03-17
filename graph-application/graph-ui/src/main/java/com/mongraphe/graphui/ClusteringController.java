package com.mongraphe.graphui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import com.mongraphe.graphlayout.Graph;
import com.mongraphe.graphlayout.GraphData;
import com.mongraphe.graphlayout.GraphData.NodeCommunity;
import com.mongraphe.graphlayout.GraphData.SimilitudeMode;

public class ClusteringController {

    @FXML
    private TextArea infoTextArea;

    @FXML
    private ComboBox<String> clusteringComboBox;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button cancelButton;

    private File fichier;
    private SimilitudeMode measureCode;
    private double upThreshold;
    private double downThreshold;
    Graph contenuVisuel;

    @FXML
    private void initialize() {
        // Ajouter des options pour les méthodes de clustering
        clusteringComboBox.getItems().addAll(
            "Louvain",
            "Louvain par composantes",
            "Leiden",
            "Leiden CPM",
            "Couleurs spéciales"
        );
        clusteringComboBox.setValue("Louvain"); // Valeur par défaut

        // Message par défaut
        infoTextArea.setText("Veuillez choisir une méthode de clustering avant de continuer.");
    }

    /**
     * Initialise les données provenant de EchantillonageController.
     */
    public void initData(File fichier, SimilitudeMode measureCode, double upThreshold, double downThreshold) {
        this.fichier = fichier;
        this.measureCode = measureCode;
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold;

        // Mise à jour de l'affichage avec les valeurs reçues
        infoTextArea.setText("Mesure de similarité: " + measureCode +
                             "\nSeuils: " + downThreshold + " - " + upThreshold +
                             "\n\nVeuillez choisir un algorithme de clustering.");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Echantillonage.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur précédent et lui renvoyer les données
            EchantillonageController controller = loader.getController();
            controller.initData(fichier, measureCode);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Echantillonnage");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNext() {
        try {
            String selectedMethod = clusteringComboBox.getValue();
            NodeCommunity methodCode = getMethodeCode(selectedMethod);

            // Charger la vue de visualisation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Visualisation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la vue de visualisation
            VisualisationController controller = loader.getController();

            // Passer les données au contrôleur
            controller.initData(fichier, measureCode, upThreshold, downThreshold, methodCode);

            // Afficher la nouvelle vue
            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Convertit le nom de la méthode sélectionnée en un code numérique.
     */
    private GraphData.NodeCommunity getMethodeCode(String method) {
        return switch (method) {
            case "Louvain" -> GraphData.NodeCommunity.LOUVAIN;
            case "Louvain par composantes" -> GraphData.NodeCommunity.LOUVAIN_PAR_COMPOSANTE;
            case "Leiden" -> GraphData.NodeCommunity.LEIDEN;
            case "Leiden CPM" -> GraphData.NodeCommunity.LEIDEN_CPM;
            case "Couleurs spéciales" -> GraphData.NodeCommunity.COULEURS_SPECIALES;
            default -> null; // Valeur par défaut en cas d'erreur
        };
    }
}
