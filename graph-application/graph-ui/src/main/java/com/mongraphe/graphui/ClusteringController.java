package com.mongraphe.graphui;

import java.io.File;

import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class ClusteringController {

    @FXML
    private TextArea infoTextArea;
    @FXML
    private ComboBox<String> clusteringComboBox;

    private File fichier;
    private SimilitudeMode measureCode;
    private double upThreshold;
    private double downThreshold;
    private NodeCommunity selectedMethod;

    @FXML
    private void initialize() {
        clusteringComboBox.getItems().addAll(
                "Louvain",
                "Louvain par composantes",
                "Leiden",
                "Leiden CPM",
                "Couleurs spéciales");

        clusteringComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        this.selectedMethod = getMethodeCode(newVal);
                    }
                });
    }

    public void initData(File fichier, SimilitudeMode measureCode, double upThreshold, double downThreshold) {
        this.fichier = fichier;
        this.measureCode = measureCode;
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold;

        infoTextArea.setText("Mesure de similarité: " + measureCode +
                "\nSeuils: " + downThreshold + " - " + upThreshold +
                "\n\nVeuillez choisir un algorithme de clustering.");
    }

    public NodeCommunity getSelectedMethod() {
        return selectedMethod;
    }

    public void setSelectedMethod(NodeCommunity method) {
        this.selectedMethod = method;
        if (method != null) {
            String methodName = getMethodName(method);
            clusteringComboBox.setValue(methodName);
        }
    }

    public double getUpThreshold() {
        return upThreshold;
    }

    public double getDownThreshold() {
        return downThreshold;
    }

    public void setFile(File fichier) {
        this.fichier = fichier;
    }

    private NodeCommunity getMethodeCode(String method) {
        return switch (method) {
            case "Louvain" -> NodeCommunity.LOUVAIN;
            case "Louvain par composantes" -> NodeCommunity.LOUVAIN_PAR_COMPOSANTE;
            case "Leiden" -> NodeCommunity.LEIDEN;
            case "Leiden CPM" -> NodeCommunity.LEIDEN_CPM;
            case "Couleurs spéciales" -> NodeCommunity.COULEURS_SPECIALES;
            default -> null;
        };
    }

    private String getMethodName(NodeCommunity method) {
        return switch (method) {
            case LOUVAIN -> "Louvain";
            case LOUVAIN_PAR_COMPOSANTE -> "Louvain par composantes";
            case LEIDEN -> "Leiden";
            case LEIDEN_CPM -> "Leiden CPM";
            case COULEURS_SPECIALES -> "Couleurs spéciales";
            default -> "Louvain";
        };
    }
}