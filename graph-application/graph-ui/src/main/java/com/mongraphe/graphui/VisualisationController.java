package com.mongraphe.graphui;

import java.io.File;

import com.mongraphe.graphlayout.Graph;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class VisualisationController {

    @FXML
    private AnchorPane centerPane; // Référence à la partie centrale du fichier FXML

    private Graph graph; // Instance de la classe Graph

    private File fichier; // Fichier contenant les données du graphe
    private int measureCode; // Code de mesure de similarité
    private double upThreshold; // Seuil supérieur
    private double downThreshold; // Seuil inférieur
    private int methodCode; // Code de méthode

    @FXML
    public void initialize() {
        // Initialiser le graphe
        graph = new Graph();
        graph.setFilename(fichier.getAbsolutePath());
    }

    /**
     * Charge le graphe dans la partie centrale de la fenêtre.
     */
    private void loadGraph() {
//        if (fichier != null && fichier.exists()) {
//            // Initialiser et afficher le graphe dans le centerPane
//            graph.initializeGraph(centerPane, fichier.getAbsolutePath());
//        } else {
//            System.err.println("Le fichier spécifié est invalide ou n'existe pas.");
//        }
    	
    	
    }

    /**
     * Gère l'action du bouton "Démarrer".
     */
    @FXML
    private void handleStartButton() {
        loadGraph(); // Charger le graphe lorsque le bouton est cliqué
    }

    /**
     * Initialise les données nécessaires pour afficher le graphe.
     *
     * @param fichier       Le fichier contenant les données du graphe.
     * @param measureCode   Le code de mesure de similarité.
     * @param upThreshold   Le seuil supérieur.
     * @param downThreshold Le seuil inférieur.
     * @param methodCode    Le code de méthode.
     */
    public void initData(File fichier, int measureCode, double upThreshold, double downThreshold, int methodCode) {
        this.fichier = fichier;
        this.measureCode = measureCode;
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold;
        this.methodCode = methodCode;
    }
}