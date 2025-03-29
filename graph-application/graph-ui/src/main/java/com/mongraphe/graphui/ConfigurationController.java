package com.mongraphe.graphui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.mongraphe.graphui.GraphData.SimilitudeMode;
import com.mongraphe.graphui.ClusteringController;
import com.mongraphe.graphui.GraphData.NodeCommunity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationController {

    @FXML private BorderPane mainBorderPane;
    @FXML private Button precedentButton;
    @FXML private Button suivantButton;
    @FXML private Button terminerButton;
    @FXML private Button annulerButton;
     @FXML private TableView<ObservableList<String>> tableView;
    @FXML private Button continuerButton;
    @FXML private TextArea fichierContent;
    @FXML private ComboBox<String> similarityMeasureComboBox;


    private File fichier;
    private ClusteringController clusteringController;

    private SimilitudeMode currentMeasure;

    @FXML
    public void initialize() {
        if (fichier != null) {
            System.out.println("Fichier reçu : " + fichier.getAbsolutePath());
        } else { System.out.println("il est null");}

        loadView("/fxml/ChoixSimilitude.fxml");
    }

    @FXML
    private void handlePrecedent() {
        // Logique pour revenir à la vue précédente
        loadView("/fxml/ChoixSimilitude.fxml");
    }

    @FXML
    private void handleSuivant() {
        // Logique pour passer à la vue suivante
        loadView("/fxml/Clustering.fxml");
    }

    @FXML
    private void handleTerminer() {
        // Récupérer la méthode sélectionnée
        NodeCommunity selectedMethod = clusteringController.getSelectedMethod();
        
        if (selectedMethod == null) {
            System.out.println("Veuillez sélectionner une méthode de clustering");
            return;
        }

        // Passer à la visualisation avec tous les paramètres
        loadVisualisationView(
            fichier,
            currentMeasure,
            0.0,
            0.0,
            selectedMethod
        );
    }

    private void loadVisualisationView(File file, SimilitudeMode measure, 
                                double upThresh, double downThresh, 
                                NodeCommunity method) {
    try {
        FXMLLoader loader = new FXMLLoader();
        // Charge depuis le bon chemin (relatif au classpath)
        loader.setLocation(getClass().getResource("/fxml/Visualisation.fxml"));
        Parent root = loader.load();
        
        Graph controller = loader.getController();
        controller.initData(file, measure, upThresh, downThresh, method);
        
        Stage stage = (Stage) mainBorderPane.getScene().getWindow();
        stage.setScene(new Scene(root, 2500, 800));
        stage.show();
        
    } catch (IOException e) {
        System.err.println("Erreur de chargement du FXML:");
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Impossible de charger l'interface");
        alert.setContentText("Le fichier Visualisation.fxml est introuvable ou corrompu.");
        alert.showAndWait();
    }
}

    @FXML
    private void handleAnnuler() {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) annulerButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ConfigurationController(File fichier) {
        this.fichier = fichier;
    }


  

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ChoixSimilitudeController) {
                ChoixSimilitudeController choixController = (ChoixSimilitudeController) controller;
                choixController.setFichier(fichier);
                
                // Pour enregistrez le callback
                choixController.setOnMeasureSelected(this::handleMeasureSelected);
            } else if (controller instanceof ClusteringController) {
                this.clusteringController = (ClusteringController) controller;
                clusteringController.initData(fichier, currentMeasure, 0.0, 0.0);
            }

            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMeasureSelected(SimilitudeMode measure) {
        this.currentMeasure = measure;
        System.out.println("Mesure sélectionnée reçue: " + measure);
            }

    public void setFichier(File fichier) {
        this.fichier = fichier;
        System.out.println("Fichier reçu dans ConfigurationController: " + fichier);
    }


}