package com.mongraphe.graphui;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.mongraphe.graphui.GraphData.SimilitudeMode;

public class ChoixSimilitudeController {

    @FXML
    private TableView<ObservableList<String>> tableView;
    @FXML
    private Button continuerButton;
    @FXML
    private Button annulerButton;
    @FXML
    private TextArea fichierContent;
    @FXML
    private ComboBox<String> similarityMeasureComboBox;

    private File fichier;
    private Consumer<SimilitudeMode> onMeasureSelectedCallback;

    @FXML
    public void initialize() {
        similarityMeasureComboBox.setItems(FXCollections.observableArrayList(
                "Corrélation", "Distance Cosinus", "Distance Euclidienne",
                "Norme L1", "Norme Linf", "KL divergence"));

        // listener pour détecter les changements de sélection
        similarityMeasureComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null && onMeasureSelectedCallback != null) {
                        SimilitudeMode mode = getMeasureCode(newVal);
                        onMeasureSelectedCallback.accept(mode);
                    }
                });
    }

    public void setOnMeasureSelected(Consumer<SimilitudeMode> callback) {
        this.onMeasureSelectedCallback = callback;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
        afficherContenu();
    }

    private void afficherContenu() {
        if (fichier != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
                String ligne;
                int rowCount = 0;

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                List<String> headers = new ArrayList<>(); // Pour stocker les en-têtes de colonnes

                while ((ligne = br.readLine()) != null) {
                    String[] valeurs = ligne.split(",");

                    if (rowCount == 0) {
                        // La première ligne contient les en-têtes
                        headers.addAll(Arrays.asList(valeurs));
                    } else {
                        // Les lignes suivantes contiennent les données
                        ObservableList<String> row = FXCollections.observableArrayList(valeurs);
                        data.add(row);
                    }

                    rowCount++;
                }

                if (!data.isEmpty()) {
                    // Création des colonnes dynamiquement avec les en-têtes
                    tableView.getColumns().clear();
                    for (int i = 0; i < headers.size(); i++) {
                        TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers.get(i));
                        final int colIndex = i;
                        column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
                                param.getValue().get(colIndex)));
                        tableView.getColumns().add(column);
                    }
                }

                tableView.setItems(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convertit le nom de la mesure sélectionnée en un code numérique.
     */
    private GraphData.SimilitudeMode getMeasureCode(String measure) {
        return switch (measure) {
            case "Corrélation" -> GraphData.SimilitudeMode.CORRELATION;
            case "Distance Cosinus" -> GraphData.SimilitudeMode.DISTANCE_COSINE;
            case "Distance Euclidienne" -> GraphData.SimilitudeMode.DISTANCE_EUCLIDIENNE;
            case "Norme L1" -> GraphData.SimilitudeMode.NORME_L1;
            case "Norme Linf" -> GraphData.SimilitudeMode.NORME_LINF;
            case "KL divergence" -> GraphData.SimilitudeMode.KL_DIVERGENCE;
            default -> null; // Valeur par défaut en cas d'erreur
        };
    }

}
