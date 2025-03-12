package com.mongraphe.graphlayout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;

import com.mongraphe.graphcore.EdgeInterm;

public class Graph {

    static {
        String libnative = System.getProperty("user.dir") + "/out/libnative.so";
        System.load(libnative);
    }

    // Méthodes natives
    public native void updatePositions();
    public native int[] getCommunitites();
    public native float[][] getClusterColors();
    public native EdgeInterm[] getEdges();
    public native Vertex[] getPositions();
    public native void startsProgram(String filename);
    public native void freeAllocatedMemory();

    public static final int WIDTH = 1500;
    public static final int HEIGHT = 800;

    private Timeline timeline;

    // Méthode principale pour obtenir le Pane avec le graphe
    //Ajouter les autres arguments ici
    public AnchorPane getGraphPane(String filename) {
        // Vérification du nom de fichier
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Le nom du fichier ne peut pas être nul ou vide.");
        }

        // Initialiser l'AnchorPane
        AnchorPane root = new AnchorPane();

        // Initialiser le programme avec le fichier
        startsProgram(filename);

        // Récupérer et ajouter les sommets
        List<Vertex> vertices = getVertices();
        if (vertices != null) {
            addVerticesToPane(root, vertices);
        } else {
            System.err.println("Aucun sommet récupéré.");
        }

        // Récupérer et ajouter les arêtes
        EdgeInterm[] edgesInterm = getEdges();
        if (edgesInterm != null) {
            addEdgesToPane(root, vertices, edgesInterm);
        } else {
            System.err.println("Aucune arête récupérée.");
        }

        // Configurer la mise à jour dynamique du graphe
        setupGraphUpdate(root);

        return root;
    }

    // Méthode pour récupérer les sommets à partir des données natives
    private List<Vertex> getVertices() {
        return List.of(getPositions());
    }

    // Méthode pour ajouter les sommets au Pane
    private void addVerticesToPane(Pane root, List<Vertex> vertices) {
        root.getChildren().addAll(vertices);
    }

    // Méthode pour ajouter les arêtes au Pane
    private void addEdgesToPane(Pane root, List<Vertex> vertices, EdgeInterm[] edgesInterm) {
        for (int i = 0; i < edgesInterm.length; i++) {
            Edge e = new Edge(vertices.get(edgesInterm[i].getStart()), vertices.get(edgesInterm[i].getEnd()));
            root.getChildren().add(e);
        }
    }

    // Méthode pour configurer la mise à jour périodique du graphe
    private void setupGraphUpdate(Pane root) {
        timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.05), event -> {
            updatePositions();
            updateGraph(root);
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Méthode pour mettre à jour le graphe à chaque itération de la timeline
    private void updateGraph(Pane root) {
        // Efface les anciennes positions (les sommets et les arêtes)
        root.getChildren().clear();
        Vertex.resetCount();

        // Ajouter les nouveaux sommets et arêtes
        List<Vertex> updatedVertices = getVertices();
        EdgeInterm[] updatedEdgesInterm = getEdges();
        addEdgesToPane(root, updatedVertices, updatedEdgesInterm);
        addVerticesToPane(root, updatedVertices);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String filename = args[0];

            // Appel à la méthode pour obtenir le Pane du graphe avec le fichier passé en argument
            Graph graph = new Graph();
            Pane graphPane = graph.getGraphPane(filename);
            // Vous pouvez maintenant utiliser graphPane dans votre application sans ouvrir de fenêtre
            // Par exemple, vous pouvez l'ajouter à une autre interface graphique ou l'intégrer dans une autre application
        } else {
            System.out.println("Aucun argument fourni.");
        }
    }
}
