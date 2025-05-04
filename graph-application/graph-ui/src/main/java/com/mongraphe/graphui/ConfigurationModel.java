package com.mongraphe.graphui;

import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;
import java.io.File;

public class ConfigurationModel {
    private File fichier;
    private SimilitudeMode measureCode;
    private double upThreshold;
    private double downThreshold;
    private NodeCommunity methodCode;

    // Getters et setters
    public File getFichier() {
        return fichier;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
    }

    public SimilitudeMode getMeasureCode() {
        return measureCode;
    }

    public void setMeasureCode(SimilitudeMode measureCode) {
        this.measureCode = measureCode;
    }

    public double getUpThreshold() {
        return upThreshold;
    }

    public void setUpThreshold(double upThreshold) {
        this.upThreshold = upThreshold;
    }

    public double getDownThreshold() {
        return downThreshold;
    }

    public void setDownThreshold(double downThreshold) {
        this.downThreshold = downThreshold;
    }

    public NodeCommunity getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(NodeCommunity methodCode) {
        this.methodCode = methodCode;
    }
}