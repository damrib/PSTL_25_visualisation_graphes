package com.mongraphe.graphui.render;

import static com.mongraphe.graphui.Graph.minimumDegree;

import com.mongraphe.graphui.Graph;
import com.mongraphe.graphui.VisualisationController;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class GraphOptionsController {
    /*
     ** Gère l'action du bouton "Appliquer le degré minimum".
     * Met à jour le degré minimum et rafraîchit le graphe.
     * Le degré minimum est défini par un curseur.
     * Le texte affiché est mis à jour en conséquence.
     */

    @FXML
    private Label degreeMinValue;
    @FXML
    private ComboBox<String> sizeFilterComboBox;
    @FXML
    private CheckBox showIsolatedNodesCheckbox;

    @FXML
    private Slider degreeMinSlider;

    private Graph graphController;

    public void setGraphController(Graph controller) {
        this.graphController = controller;
    }

    /**
     * Gère l'action du bouton "Démarrer".
     */
    @FXML
    private void handleStartButton() {
        System.out.println("Bouton démarrer cliqué !");
        System.out.println("Bouton démarrer cliqué !");

        /*
         * graphInit();
         * graphContainer.getChildren().clear(); // Nettoyer le conteneur
         * graphContainer.getChildren().add(root);
         */

    }

    @FXML
    private void applyMinDegree() {
        int newDegreeMin = (int) degreeMinSlider.getValue();
        minimumDegree.set(newDegreeMin);
        degreeMinValue.setText(String.valueOf(newDegreeMin));
        System.out.println("Nouveau degré minimum : " + newDegreeMin);

        // Mettre à jour le graphe en fonction du nouveau degré min
        /* refreshGraph(); */
    }

    // Réinitialiser les paramètres du graphe
    @FXML
    private void resetGraphSettings() {
        degreeMinSlider.setValue(3);
        degreeMinValue.setText("3");
        minimumDegree.set(3);

        sizeFilterComboBox.getSelectionModel().select("Tous");
        showIsolatedNodesCheckbox.setSelected(true);

        // Rafraîchir l'affichage
        /* refreshGraph(); */
    }

}
