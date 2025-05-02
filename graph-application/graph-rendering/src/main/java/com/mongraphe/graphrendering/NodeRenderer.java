package com.mongraphe.graphrendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.awt.*;
import com.mongraphe.graphcore.Node;
import com.mongraphe.model.NodeProperties;

/**
 * Classe pour le rendu des nœuds du graphe avec attributs dynamiques.
 */
public class NodeRenderer {

    /**
     * Rendu d'un nœud sur le canevas.
     * @param node Le nœud à rendre.
     * @param gc Le contexte graphique pour dessiner.
     */
    public void render(Node node, GraphicsContext gc) {
        // Récupérer les propriétés du nœud (y compris les dynamiques)
        double x = node.getX();
        double y = node.getY();
        int size = (int) node.getAttribute(NodeProperties.SIZE); // Taille dynamique
        String colorStr = (String) node.getAttribute(NodeProperties.COLOR); // Couleur dynamique
        String label = (String) node.getAttribute(NodeProperties.LABEL); // Étiquette dynamique

        // Définir la couleur de remplissage du nœud
        Color color = Color.web(colorStr != null ? colorStr : "blue"); // Couleur par défaut "blue"
        gc.setFill(color);

        // Dessiner le nœud (un cercle pour le moment)
        gc.fillOval(x - size / 2, y - size / 2, size, size);

        // Afficher l'étiquette du nœud si présente
        if (label != null) {
            gc.setFill(Color.BLACK);
            gc.fillText(label, x + size / 2 + 5, y);
        }
    }
}

