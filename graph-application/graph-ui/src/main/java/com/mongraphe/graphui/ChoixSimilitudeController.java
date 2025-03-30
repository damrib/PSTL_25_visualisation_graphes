package com.mongraphe.graphui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.font.GraphicAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.mongraphe.graphui.GraphData.SimilitudeMode;

public class ChoixSimilitudeController {
	
    @FXML private TableView<ObservableList<String>> tableView;
    @FXML private Button continuerButton;
    @FXML private Button annulerButton;
    @FXML private TextArea fichierContent;
    @FXML private ComboBox<String> similarityMeasureComboBox;
    
    private File fichier;

    @FXML
    public void initialize() {
        similarityMeasureComboBox.setItems(FXCollections.observableArrayList(
            "Corrélation", 
            "Distance Cosinus", 
            "Distance Euclidienne", 
            "Norme L1", 
            "Norme Linf", 
            "KL divergence"
        ));
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

                while ((ligne = br.readLine()) != null && rowCount < 10) {
                    String[] valeurs = ligne.split(",");
                    ObservableList<String> row = FXCollections.observableArrayList(valeurs);
                    data.add(row);
                    rowCount++;
                }

                if (!data.isEmpty()) {
                    // Création des colonnes dynamiquement
                    tableView.getColumns().clear();
                    for (int i = 0; i < data.get(0).size(); i++) {
                        TableColumn<ObservableList<String>, String> column = new TableColumn<>("Col " + (i + 1));
                        final int colIndex = i;
                        column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex)));
                        tableView.getColumns().add(column);
                    }
                }

                tableView.setItems(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    @FXML
    private void handleContinuer() {
        // Récupérer l'élément sélectionné dans la ComboBox
        String selectedMeasure = similarityMeasureComboBox.getValue();
        
        // Convertir le texte sélectionné en un code correspondant
        SimilitudeMode measureCode = getMeasureCode(selectedMeasure);

        // Debug: Afficher la mesure sélectionnée
        System.out.println("Mesure sélectionnée: " + selectedMeasure + " (Code: " + measureCode + ")");

        // Charger et afficher la fenêtre d'échantillonnage
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Echantillonage.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la fenêtre Echantillonnage
            EchantillonageController controller = loader.getController();

            // Passer les informations à la nouvelle fenêtre
            controller.initData(fichier, measureCode);

            // Créer une nouvelle scène et afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Échantillonnage");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

            // Fermer la fenêtre actuelle (si nécessaire)
            ((Stage) continuerButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
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

