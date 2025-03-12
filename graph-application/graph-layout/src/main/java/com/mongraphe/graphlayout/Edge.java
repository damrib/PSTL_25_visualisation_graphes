package com.mongraphe.graphcore;

import com.mongraphe.model.EdgeProperties;

/**
 * Classe représentant une arête dans un graphe.
 */
public class Edge {
    
    private Node source;  // Le nœud source de l'arête
    private Node target;  // Le nœud cible de l'arête
    private EdgeProperties properties;  // Les propriétés dynamiques de l'arête
    
    /**
     * Constructeur de l'arête.
     * @param source Le nœud source de l'arête.
     * @param target Le nœud cible de l'arête.
     */
    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
        this.properties = new EdgeProperties(source, target);  // Initialisation des propriétés
    }

    /**
     * Récupère le nœud source de l'arête.
     * @return Le nœud source.
     */
    public Node getSource() {
        return source;
    }

    /**
     * Récupère le nœud cible de l'arête.
     * @return Le nœud cible.
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Définit le nœud source de l'arête.
     * @param source Le nœud source.
     */
    public void setSource(Node source) {
        this.source = source;
    }

    /**
     * Définit le nœud cible de l'arête.
     * @param target Le nœud cible.
     */
    public void setTarget(Node target) {
        this.target = target;
    }

    /**
     * Ajoute un attribut à l'arête via les propriétés de l'arête.
     * @param key Le nom de l'attribut.
     * @param value La valeur de l'attribut.
     */
    public void setAttribute(String key, Object value) {
        properties.setAttribute(key, value);
    }

    /**
     * Récupère un attribut de l'arête via les propriétés de l'arête.
     * @param key Le nom de l'attribut.
     * @return La valeur de l'attribut.
     */
    public Object getAttribute(String key) {
        return properties.getAttribute(key);
    }

    /**
     * Récupère toutes les propriétés de l'arête sous forme de Map.
     * @return Les propriétés de l'arête.
     */
    public EdgeProperties getProperties() {
        return properties;
    }
}


