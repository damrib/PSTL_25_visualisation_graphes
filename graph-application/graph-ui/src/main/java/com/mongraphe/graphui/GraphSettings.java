package com.mongraphe.graphui;

import com.jogamp.opengl.GL4;
import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

/**
 * Définit les méthodes à appeler par l'inteface graphique pour modifier les
 * paramètres du graphe
 */
public interface GraphSettings {

  // -------------------------------------------------------------------------
  // Initialisation (à appeler avant le lancement de la simulation)
  // -------------------------------------------------------------------------

  /**
   * Initialise le graphe avec les données du fichier .csv
   * 
   * @param path      Chemin du fichier .csv à charger
   * @param mode      Mode de similitude à utiliser
   * @param community Mode de détection de communautés à utiliser
   * @return les données du fichier .csv
   * @see GraphData.SimilitudeMode
   * @see GraphData.NodeCommunity
   */
  double[][] initGraph(String path, GraphData.SimilitudeMode mode, GraphData.NodeCommunity community);

  /**
   * Initialise la taille de l'écran du graphe
   * 
   * @param width  Largeur de l'écran (en px)
   * @param height Hauteur de l'écran (en px)
   */
  void setScreenSize(double width, double height);

  /**
   * Modifie la couleur de fond du graphe
   * 
   * @param color_r Composante rouge de la couleur
   * @param color_g Composante verte de la couleur
   * @param color_b Composante bleue de la couleur
   */
  void setBackgroundColor(float color_r, float color_g, float color_b);

  /**
   * @param upscale Facteur d'agrandissement pour le graphe
   */
  void setUpscale(int upscale);

  /**
   * @param size Taille initiale d'un sommet
   */
  void setInitialNodeSize(double size);

  /**
   * @param factor Facteur d'agrandissement selon le degré d'un sommet (0 pour que
   *               la taille soit identique pour tous les sommets, > 0 pour faire
   *               varier la taille proportionnellement au degré)
   */
  void setDegreeScaleFactor(double factor);

  // -------------------------------------------------------------------------
  // Mode de sélection
  // -------------------------------------------------------------------------

  /**
   * @return le mode actuel du graphe
   * @see GraphData.GraphMode
   */
  GraphData.GraphMode getMode();

  /**
   * Change le mode du graphe
   * 
   * @param mode Nouveau mode du graphe
   * @see GraphData.GraphMode
   */
  void setMode(GraphData.GraphMode mode);

  // -------------------------------------------------------------------------
  // Paramètres de la simulation
  // -------------------------------------------------------------------------

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
   * Le seuil de stabilité indique quand le graphe doit s'arrêter (si le mouvement
   * est inférieur au seuil et que suffisamment de temps s'est écoulé, alors le
   * graphe s'arrête de bouger)
   * 
   * @param threshold Nouveau seuil à appliquer
   */
  void setStabilizedThreshold(double threshold);

  /**
   * Le seuil d'attraction correspond à la distance minimum pour appliquer une
   * force d'attraction entre deux points
   * 
   * @param threshold Seuil d'attraction entre les sommets
   */
  void setAttractionThreshold(double threshold);

  /**
   * @param freq Fréquence à laquelle les clusters sont mis à jour
   */
  void setUpdatedFrequence(int freq);

  /**
   * @param friction Friction à appliquer
   */
  void setNewFriction(double friction);

  /**
   * Choisir le mode de répulsion à utiliser pour mettre à jour les positions
   * 
   * @param mode Mode de répulsion à utiliser
   * @see GraphData.RepulsionMode
   */
  void setRepulsionMode(GraphData.RepulsionMode mode);

  /**
   * @param antiedge_repulsion Force de répulsion des anti-arêtes
   */
  void setAntiEdgesRepulsion(double antiedge_repulsion);

  /**
   * @param attraction_coeff Force d'attraction entre les sommets
   */
  void setAttractionCoefficient(double attraction_coeff);

  /**
   * @param threshold Seuil de répulsion entre les sommets
   */
  void setRepulsionThreshold(double threshold);

  /**
   * @param amortissement Amortissement à appliquer (facteur dictant comment la
   *                      friction évolue après chaque mise à jour du graphe)
   */
  void setNewAmortissement(double amortissement);

  /**
   * @param new_number_of_clusters Nombre de clusters à considérer
   */
  void setNbClusters(int new_number_of_clusters);

  /**
   * @return le degré minimum des sommets à afficher
   */
  int getMinimumDegree();

  /**
   * Affiche les sommets dont le degré est supérieur ou égal à degree
   * 
   * @param degree Degré minimum des sommets à afficher
   */
  void setMinimumDegree(int degree);

  // -------------------------------------------------------------------------
  // Options supplémentaires
  // -------------------------------------------------------------------------

  /**
   * Exporte le graphe en image PNG
   * 
   * @param path Chemin de l'image PNG à exporter
   */
  void exportToPng(GL4 gl, String path);

/**
 * @param path
 * @param mode
 * @param community
 * @return
 */
double[][] initGraphCsv(String path, SimilitudeMode mode, NodeCommunity community);

/**
 * @param width
 * @param height
 */
void setScreenSize(int width, int height);

/**
 * @param isEnabled
 */
void enableKmeans(boolean isEnabled);

/**
 * @return
 */
int[] getHistogramme();

/**
 * @param path
 * @param community
 */
void initGraphDot(String path, NodeCommunity community);

}