package com.mongraphe.graphui;

import javafx.application.Platform;
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

import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

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
        // Méthodes de clustering
        clusteringComboBox.getItems().addAll(
                "Louvain",
                "Louvain par composantes",
                "Leiden",
                "Leiden CPM",
                "Couleurs spéciales");
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

    /**
     * Récupère la méthode de clustering sélectionnée
     */
    public NodeCommunity getSelectedMethod() {
        String selected = clusteringComboBox.getValue();
        return getMethodeCode(selected);
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
