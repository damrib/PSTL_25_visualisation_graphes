package graph;

public interface GraphSettings {

    // Initialisation (à appeler en premier)

    /**
     * Initialise le graphe avec les données du fichier .csv
     * @param path Chemin du fichier .csv à charger
     * @param mode Mode de similitude à utiliser
     * @param community Mode de détection de communautés à utiliser
     * @return les données du fichier .csv
     * @see GraphData.SimilitudeMode
     * @see GraphData.NodeCommunity
     */
    double[][] init(String path, GraphData.SimilitudeMode mode, GraphData.NodeCommunity community);

    /**
     * Initialise la taille de l'écran du graphe
     * @param width Largeur de l'écran (en px)
     * @param height Hauteur de l'écran (en px)
     */
    void setScreenSize(double width, double height);




    // Mode de sélection

    /**
     * @return le mode actuel du graphe
     * @see GraphData.GraphMode
     */
    GraphData.GraphMode getMode();

    /**
     * Change le mode du graphe
     * @param mode Nouveau mode du graphe
     * @see GraphData.GraphMode
     */
    void setMode(GraphData.GraphMode mode);




    // Paramètres de la simulation

    /**
     * @return le seuil recommandé pour les arêtes
     */
    double getRecommendedThreshold();

    /**
     * @return le seuil recommandé pour les anti-arêtes
     */
    double getRecommendedAntiThreshold();

    /**
     * @return le seuil actuel pour les arêtes
     */
    double getThreshold();

    /**
     * @return le seuil actuel pour les anti-arêtes
     */
    double getAntiThreshold();

    /**
     * Change le seuil pour les arêtes
     * @param threshold Nouveau seuil pour les arêtes
     */
    void setThreshold(double threshold);

    /**
     * Change le seuil pour les anti-arêtes
     * @param antiThreshold Nouveau seuil pour les anti-arêtes
     */
    void setAntiThreshold(double antiThreshold);

    /**
     * @param upscale Facteur d'agrandissement pour le graphe
     */
    void setUpscale(int upscale);

    /**
     * @param size Taille initiale d'un sommet
     */
    void setInitialNodeSize(double size);

    /**
     * @param factor Facteur d'agrandissement selon le degré d'un sommet (0 pour que la taille soit identique pour tous les sommets, > 0  pour faire varier la taille proportionnellement au degré)
     */
    void setDegreeScaleFactor(double factor);

    /**
     * Affiche les sommets dont le degré est supérieur ou égal à degree
     * @param degree Degré minimum des sommets à afficher
     */
    void setMiniumDegree(int degree);

    /**
     * @param rate Intervalle de temps entre chaque mise à jour du graphe (en secondes)
     */
    void setRefreshRate(double rate);

    /**
     * @param hexaColor Couleur de fond du graphe (au format hexadécimal)
     */
    void setBackGroundColor(String hexaColor);




    // Options supplémentaires

    /**
     * Exporte le graphe en image PNG
     * @param path Chemin de l'image PNG à exporter
     */
    void exportToPng(String path);

}
