package com.mongraphe.graphui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FenetreGrapheController {

    @FXML private Label fileNameLabel;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setFileName(String fileName) {
        fileNameLabel.setText("Fichier chargé : " + fileName);
    }

    @FXML
    private void handleClose() {
        stage.close();
    }
}
