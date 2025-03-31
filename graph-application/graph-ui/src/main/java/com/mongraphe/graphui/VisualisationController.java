package com.mongraphe.graphui;

import java.io.File;

import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class VisualisationController {

    @FXML
    private AnchorPane graphContainer;
    @FXML
    private ScrollPane graphScrollPane;
    @FXML
    private BorderPane graphContentPane;

    private File fichier;
    private SimilitudeMode measureCode;
    private double upThreshold;
    private double downThreshold;
    private NodeCommunity methodCode;
    private Graph contenuVisuel;
    private Scale scaleTransform;

    public void initData(File fichier, SimilitudeMode measureCode,
            double upThreshold, double downThreshold,
            NodeCommunity methodCode) {
        this.fichier = fichier;
        this.measureCode = measureCode;
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold;
        this.methodCode = methodCode;

        initializeGraph();
        setupGraphContainer();
    }

    private void initializeGraph() {
        contenuVisuel = new Graph();
        contenuVisuel.init(fichier.getAbsolutePath(), measureCode, methodCode);

        // Configuration du graphe avec des tailles proportionnelles
        double containerWidth = graphContentPane.getWidth();
        double containerHeight = graphContentPane.getHeight();

        contenuVisuel.setScreenSize(containerWidth * 1.5, containerHeight * 1.5);
        contenuVisuel.setRefreshRate(1);
        contenuVisuel.setUpscale(5);
        contenuVisuel.setInitialNodeSize(3);
        contenuVisuel.setDegreeScaleFactor(0.3);
        /*
         * contenuVisuel.setMaxZoom(2.0); // Limite le zoom maximum
         * 
         * ntenuVisuel.setMinZoom(0.5); // Limite le zoom minimum
         */

        displayGraph();
    }

    private void displayGraph() {
        Pane graphRoot = contenuVisuel.getGraphRoot();

        // Configurer les contraintes de taille
        graphRoot.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        graphRoot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        graphContainer.getChildren().clear();
        graphContainer.getChildren().add(graphRoot);

        // Configurer les ancres pour remplir l'espace
        AnchorPane.setTopAnchor(graphRoot, 0.0);
        AnchorPane.setBottomAnchor(graphRoot, 0.0);
        AnchorPane.setLeftAnchor(graphRoot, 0.0);
        AnchorPane.setRightAnchor(graphRoot, 0.0);

        // Forcer le conteneur à respecter les limites
        graphContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            contenuVisuel.constrainGraphToBounds();
        });

        graphContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            contenuVisuel.constrainGraphToBounds();
        });
    }

    private void setupGraphContainer() {
        // Liaison de la taille du conteneur à la taille du ScrollPane
        graphContainer.prefWidthProperty().bind(
                graphScrollPane.widthProperty().subtract(20)); // 20px pour les barres de défilement
        graphContainer.prefHeightProperty().bind(
                graphScrollPane.heightProperty().subtract(20));

        // Écouteur pour le redimensionnement
        graphContentPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (contenuVisuel != null) {
                contenuVisuel.setScreenSize(newVal.doubleValue() * 1.5,
                        graphContentPane.getHeight() * 1.5);
            }
        });

        graphContentPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (contenuVisuel != null) {
                contenuVisuel.setScreenSize(graphContentPane.getWidth() * 1.5,
                        newVal.doubleValue() * 1.5);
            }
        });
    }

    private void setupZoomAndPan() {
        // Gestion du zoom avec la molette de la souris
        graphContainer.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();

            if (deltaY < 0) {
                zoomFactor = 0.95; // Zoom out
            }

            // Limites du zoom
            double currentScale = scaleTransform.getX();
            double newScale = currentScale * zoomFactor;

            if (newScale >= 0.1 && newScale <= 3.0) {
                scaleTransform.setX(newScale);
                scaleTransform.setY(newScale);

                // Ajuster la position pour zoomer vers le pointeur
                event.consume();
            }
        });

        // Gestion du déplacement avec drag
        final double[] dragAnchor = new double[2];
        graphContainer.setOnMousePressed(event -> {
            dragAnchor[0] = event.getSceneX();
            dragAnchor[1] = event.getSceneY();
        });

        graphContainer.setOnMouseDragged(event -> {
            graphScrollPane.setHvalue(graphScrollPane.getHvalue() +
                    (dragAnchor[0] - event.getSceneX()) / (graphContainer.getWidth() * scaleTransform.getX()));
            graphScrollPane.setVvalue(graphScrollPane.getVvalue() +
                    (dragAnchor[1] - event.getSceneY()) / (graphContainer.getHeight() * scaleTransform.getY()));
            dragAnchor[0] = event.getSceneX();
            dragAnchor[1] = event.getSceneY();
            event.consume();
        });

    }
}