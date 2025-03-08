package com.mongraphe.graphui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FenetreGrapheController {
	
    @FXML private TableView<ObservableList<String>> tableView;
    @FXML private Button continuerButton;
    @FXML private Button annulerButton;
    @FXML private TextArea fichierContent;
    
    private File fichier;

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
            Scene scene = new Scene(root, 500, 350);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleContinuer() {
        System.out.println("Continuer avec le graphe...");
        // Ajouter ici l'action suivante
    }
}

