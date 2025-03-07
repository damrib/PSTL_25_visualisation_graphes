package com.mongraphe.model;

import java.util.HashMap;
import java.util.Map;

import com.mongraphe.graphcore.Node;

public class EdgeProperties {
	public static final String WEIGHT = "weight";
    public static final String TYPE = "type"; 
    
    private final Map<String, Object> attributes = new HashMap<>();
        
    // Attributs spécifiques à l'arête
    private Node source;  // Le nœud source de l'arête
    private Node target;  // Le nœud cible de l'arête

    public EdgeProperties(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Ajoute un attribut à l'arête.
     * @param key Le nom de l'attribut.
     * @param value La valeur de l'attribut.
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Récupère un attribut de l'arête.
     * @param key Le nom de l'attribut.
     * @return La valeur de l'attribut.
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Récupère tous les attributs de l'arête.
     * @return Les attributs sous forme de Map.
     */
    public Map<String, Object> getAttributes() {
        return attributes;
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
}
