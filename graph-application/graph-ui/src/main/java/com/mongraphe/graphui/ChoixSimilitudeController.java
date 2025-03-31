package com.mongraphe.graphui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.mongraphe.graphui.GraphData.SimilitudeMode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

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
    private SimilitudeMode currentMeasure;

    @FXML
    public void initialize() {
        similarityMeasureComboBox.setItems(FXCollections.observableArrayList(
                "Corrélation", "Distance Cosinus", "Distance Euclidienne",
                "Norme L1", "Norme Linf", "KL divergence"));

        similarityMeasureComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        this.currentMeasure = getMeasureCode(newVal);
                        if (onMeasureSelectedCallback != null) {
                            onMeasureSelectedCallback.accept(currentMeasure);
                        }
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

    public void setSelectedMeasure(SimilitudeMode measure) {
        this.currentMeasure = measure;
        if (measure != null) {
            String measureName = getMeasureName(measure);
            similarityMeasureComboBox.setValue(measureName);
        }
    }

    private String getMeasureName(SimilitudeMode measure) {
        return switch (measure) {
            case CORRELATION -> "Corrélation";
            case DISTANCE_COSINE -> "Distance Cosinus";
            case DISTANCE_EUCLIDIENNE -> "Distance Euclidienne";
            case NORME_L1 -> "Norme L1";
            case NORME_LINF -> "Norme Linf";
            case KL_DIVERGENCE -> "KL divergence";
            default -> "Corrélation";
        };
    }

    private void afficherContenu() {
        if (fichier != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
                String ligne;
                int rowCount = 0;
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                List<String> headers = new ArrayList<>();

                while (((ligne = br.readLine()) != null) && rowCount < 15) {
                    // Limiter à 100 lignes pour éviter de surcharger l'affichage
                    String[] valeurs = ligne.split(",");

                    if (rowCount == 0) {
                        headers.addAll(Arrays.asList(valeurs));
                    } else {
                        ObservableList<String> row = FXCollections.observableArrayList(valeurs);
                        data.add(row);
                    }
                    rowCount++;
                }

                if (!data.isEmpty()) {
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

    private SimilitudeMode getMeasureCode(String measure) {
        return switch (measure) {
            case "Corrélation" -> SimilitudeMode.CORRELATION;
            case "Distance Cosinus" -> SimilitudeMode.DISTANCE_COSINE;
            case "Distance Euclidienne" -> SimilitudeMode.DISTANCE_EUCLIDIENNE;
            case "Norme L1" -> SimilitudeMode.NORME_L1;
            case "Norme Linf" -> SimilitudeMode.NORME_LINF;
            case "KL divergence" -> SimilitudeMode.KL_DIVERGENCE;
            default -> null;
        };
    }
}