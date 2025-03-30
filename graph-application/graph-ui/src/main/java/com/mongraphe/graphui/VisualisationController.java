package com.mongraphe.graphui;

import java.io.File;

import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class VisualisationController {
	
	@FXML
    private AnchorPane graphContainer; // Référence au conteneur dans le FXML

    private File fichier;
    private SimilitudeMode measureCode;
    private double upThreshold;
    private double downThreshold;
    private NodeCommunity methodCode;
    private Graph contenuVisuel;

    public void initData(File fichier, SimilitudeMode measureCode, double upThreshold, double downThreshold, NodeCommunity methodCode) {
        this.fichier = fichier;
        this.measureCode = measureCode;
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold;
        this.methodCode = methodCode;

        // Initialiser et afficher le graphe
        initializeGraph();
    }

    private void initializeGraph() {
        // Initialiser le graphe avec les paramètres reçus
        contenuVisuel = new Graph();
        contenuVisuel.init(fichier.getAbsolutePath(), measureCode, methodCode);
        contenuVisuel.setScreenSize(3000, 3000);
        contenuVisuel.setRefreshRate(1);
        contenuVisuel.setUpscale(5);
        contenuVisuel.setInitialNodeSize(3);
        contenuVisuel.setDegreeScaleFactor(0.3);

        // Afficher le graphe dans la scène JavaFX
        displayGraph();
    }

    private void displayGraph() {
        // Implémentez cette méthode pour afficher le graphe dans la scène
    	// Récupérer le panneau racine du graphe
        Pane graphRoot = contenuVisuel.getGraphRoot(); // Supposons que Graph ait une méthode getGraphRoot()

        // Ajouter le graphe au conteneur
        graphContainer.getChildren().clear(); // Nettoyer le conteneur
        graphContainer.getChildren().add(graphRoot); // Ajouter le graphe
    }
}