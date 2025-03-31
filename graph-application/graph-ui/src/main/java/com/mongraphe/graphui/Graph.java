package com.mongraphe.graphui;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Graph implements GraphSettings {

    @FXML
    private StackPane mainContentPane;
    @FXML
    private GridPane overviewPane;
    @FXML
    private AnchorPane dataPane;
    @FXML
    private AnchorPane previewPane;
    @FXML
    private AnchorPane graphContainer;
    @FXML
    private ToggleGroup viewToggleGroup;
    @FXML
    private ToggleGroup graphModeToggleGroup;

    public void handleQuit(ActionEvent event) {
        Platform.exit();
    }

    public void handleAbout(ActionEvent event) {
    }

    @FXML
    private void handleViewChange(ActionEvent event) {
        ToggleButton selected = (ToggleButton) viewToggleGroup.getSelectedToggle();
        if (selected == null)
            return;

        String viewType = (String) selected.getUserData();

        overviewPane.setVisible(false);
        dataPane.setVisible(false);
        previewPane.setVisible(false);

        switch (viewType) {
            case "overview":
                overviewPane.setVisible(true);
                break;
            case "data":
                dataPane.setVisible(true);
                break;
            case "preview":
                previewPane.setVisible(true);
                break;
        }
    }

    @FXML
    private void handleApplyGraphMode(ActionEvent event) {
        RadioButton selected = (RadioButton) graphModeToggleGroup.getSelectedToggle();
        if (selected == null)
            return;

        String mode = (String) selected.getUserData();

        switch (mode) {
            case "RUN":
                setMode(GraphData.GraphMode.RUN);
                break;
            case "SELECTION":
                setMode(GraphData.GraphMode.SELECTION);
                break;
            case "MOVE":
                setMode(GraphData.GraphMode.MOVE);
                break;
            case "DELETE":
                setMode(GraphData.GraphMode.DELETE);
                break;
        }

        System.out.println("Mode changé : " + mode);
    }

    /**
     * Gère l'action du bouton "Démarrer".
     */
    @FXML
    private void handleStartButton() {
        System.out.println("Bouton démarrer cliqué !");
        graphInit();
        graphContainer.getChildren().clear(); // Nettoyer le conteneur
        graphContainer.getChildren().add(root);
    }

    public Pane getGraphRoot() {
        return root;
    }

    static {
        String os = System.getProperty("os.name").toLowerCase();
        String libPath;

        if (os.contains("win")) {
            libPath = System.getProperty("user.dir") + "/out/windows/libnative.dll";
        } else if (os.contains("mac")) {
            libPath = System.getProperty("user.dir") + "/out/macos/libnative.dylib";
        } else { // Linux
            libPath = System.getProperty("user.dir") + "/out/linux/libnative.so";
        }

        System.load(libPath);
    }

    // Méthodes JNI
    public native boolean updatePositions();

    public native int[] getCommunities();

    public native float[][] getClusterColors();

    public native EdgeC[] getEdges();

    public native Vertex[] getPositions();

    public native double[][] startsProgram(String filename);

    public native Metadata computeThreshold(int modeSimilitude);

    public native Metadata initiliazeGraph(int modeCommunity, double threshold, double anti_threshold);

    // A modifier quand le graphe est en pause
    /**
     * Set the frequence at which the clusters are updated
     * 
     * @param saut
     */
    public native void setSaut(int saut);

    /**
     * Threshold indicating when the graph should be stopped
     * (if movement is less than threshold and it has been long enough then the
     * graph stops moving)
     * 
     * @param thresholdS
     */
    public native void setThresholdS(double thresholdS);

    /**
     * Set new friction *
     * 
     * @param friction
     **/
    public native void setFriction(double friction);

    // Modifiable pendant l'execution (avant prochain updatePositions)
    /**
     * sets the repulsion mode to be used by updatePositions
     * 
     * @param mode : repulsion by degree (0), repulsion by edges (1), repulsion by
     *             communities (2)
     */
    public native void setModeRepulsion(int mode);

    /**
     * sets force of anti-edges
     * 
     * @param antiedge_repulsion
     */
    public native void setAntiRepulsion(double antiedge_repulsion);

    /**
     * sets attraction force
     * 
     * @param attraction_coeff
     */
    public native void setAttractionCoeff(double attraction_coeff);

    /**
     * sets attraction threshold
     * 
     * @param thresholdA
     */
    public native void setThresholdA(double thresholdA);

    /**
     * sets new repulsion threshold
     * 
     * @param seuilrep
     */
    public native void setSeuilRep(double seuilrep);

    /**
     * the calculation depends on how big the window is
     * 
     * @param width  positive real number
     * @param height positive real number
     */
    public native void setDimension(double width, double height);

    /**
     * Set amortissement, factor dictating how the friction evolves after each
     * update of the graph
     * 
     * @param amortissement
     */
    public native void setAmortissement(double amortissement);

    public native void setNodePosition(int index, double x, double y);

    public native void SetNumberClusters(int new_number_of_clusters);

    public native void freeAllocatedMemory();

    public static double WIDTH = 1500; // Largeur de la fenêtre
    public static double HEIGHT = 800; // Hauteur de la fenêtre

    // Propriétés pour les différents modes du graphe
    public static final BooleanProperty isRunMode = new SimpleBooleanProperty(true);
    public static final BooleanProperty isSelectionMode = new SimpleBooleanProperty(false);
    public static final BooleanProperty isMoveMode = new SimpleBooleanProperty(false);
    public static final BooleanProperty isDeleteMode = new SimpleBooleanProperty(false);

    // Propriété pour le degré minimum des sommets
    public static final IntegerProperty minimumDegree = new SimpleIntegerProperty(0);

    // Propriété pour la fréquence de mise à jour du graphe
    public static final DoubleProperty updateFrequency = new SimpleDoubleProperty(1.0);

    // Propriété pour la couleur de fond
    public static final StringProperty background_color = new SimpleStringProperty("#000000");

    private Metadata init_metadata;
    private Metadata metadata;
    private Scene scene;
    private Timeline timeline;
    private Scale scale;
    private Translate translate;

    private File fichier;
    private SimilitudeMode measureCode;
    private double upThreshold;
    private double downThreshold;
    private NodeCommunity methodCode;
    private Pane root;

    private static final double MIN_ZOOM = 0;
    private static final double MAX_ZOOM = 10.0;
    private static final double ZOOM_FACTOR = 1.1;

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
    private DoubleProperty translateX = new SimpleDoubleProperty(0.0);
    private DoubleProperty translateY = new SimpleDoubleProperty(0.0);

    public void initData(File fichier, SimilitudeMode measureCode, double upThreshold, double downThreshold,
            NodeCommunity methodCode) {
        this.fichier = fichier;
        this.measureCode = measureCode;
        this.upThreshold = upThreshold;
        this.downThreshold = downThreshold;
        this.methodCode = methodCode;
    }

    /**
     * Méthode principale du graphe
     */
    public void graphInit() {

        root = new Pane();

        // Conteneur intermédiaire avec clip
        Pane clippingContainer = new Pane(root);
        graphContainer.getChildren().add(clippingContainer);

        // Configuration du clip
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(graphContainer.widthProperty());
        clip.heightProperty().bind(graphContainer.heightProperty());
        clippingContainer.setClip(clip);

        // Transformations
        scale = new Scale(1.0, 1.0);
        translate = new Translate(0, 0);
        root.getTransforms().addAll(translate, scale);

        // Contrôles des limites
        scale.xProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 0.1)
                scale.setX(0.1);
            if (root.getBoundsInLocal().getWidth() * newVal.doubleValue() < clippingContainer.getWidth()) {
                scale.setX(clippingContainer.getWidth() / root.getBoundsInLocal().getWidth());
            }
        });

        // Définir la couleur de fond
        background_color.addListener((obs, oldValue, newValue) -> {
            scene.setFill(Color.web(newValue));
        });

        // Ajouter les transformations de zoom et de translation
        scale = new Scale(1.0, 1.0);
        translate = new Translate(0, 0);
        root.getTransforms().addAll(translate, scale);

        // Ajouter les listeners pour les différents modes du graphe
        isRunMode.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                timeline.play();
            } else {
                timeline.stop();
            }
        });
        isMoveMode.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                enableDrag(root);
                enableZoom(root);
            } else {
                disableDrag(root);
                disableZoom(root);
            }
        });
        isDeleteMode.addListener((obs, oldValue, newValue) -> {
            // TODO
        });

        // Initialisation (provisoire, devra être appelé par l'interface graphique)
        testInit();

        // Récupérer les sommets
        List<Vertex> vertices = List.of(getPositions());

        // Récupérer les couleurs des clusters
        float[][] color = getClusterColors();

        // Récupérer les communautés de chaque sommet
        int[] communititesInterm = getCommunities();
        HashMap<Integer, Community> communities = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            int community_id = communititesInterm[i];
            if (!communities.containsKey(community_id)) {
                communities.put(community_id, new Community(community_id, color[i][0], color[i][1], color[i][2]));
            }
            vertices.get(i).setId(i); // Attribution d'un identifiant unique
            vertices.get(i).setGraph(this); // Attribution du graphe
            vertices.get(i).setCommunity(communities.get(community_id)); // Attribution de la communauté
        }

        System.out.println("\nCommunautés (" + communities.size() + ") :");
        for (Community c : communities.values())
            System.out.println("- " + c);
        System.out.println();

        // Récupérer les arêtes
        EdgeC[] edgesC = getEdges();
        for (EdgeC edgeC : edgesC) {
            Edge e = new Edge(vertices.get(edgeC.getStart()), vertices.get(edgeC.getEnd()), edgeC.getWeight());

            // Ajouter les arêtes à la racine
            root.getChildren().add(e);
        }

        // Ajuster les rayons des sommets selon leur degré
        for (Vertex v : vertices)
            v.updateRadius();

        // Ajouter les sommets à la racine
        root.getChildren().addAll(vertices);

        // Création du keyframe pour la mise à jour du graphe
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(updateFrequency.get()), event -> {

            // Met à jour les positions du graphe
            boolean is_running = updatePositions();

            // Cas où le graphe atteint un état stable
            if (!is_running) {
                System.out.println("Fin de la simulation");
                setMode(GraphData.GraphMode.SELECTION);
            }

            List<Vertex> updatedVertices = List.of(getPositions());
            for (int i = 0; i < updatedVertices.size(); i++) {
                // Mise à jour des coordonnées des sommets
                vertices.get(i).updatePosition(updatedVertices.get(i).getX(), updatedVertices.get(i).getY());

                // Mise à jour du degré minimum des sommets
                int min_degree = minimumDegree.get();
                if (vertices.get(i).getDegree() >= min_degree && !root.getChildren().contains(vertices.get(i))) {
                    root.getChildren().add(vertices.get(i));
                    for (Edge e : vertices.get(i).getEdges())
                        if (!root.getChildren().contains(e) && e.getStart().getDegree() >= min_degree
                                && e.getEnd().getDegree() >= min_degree)
                            root.getChildren().add(e);
                } else if (vertices.get(i).getDegree() < minimumDegree.get()) {
                    root.getChildren().remove(vertices.get(i));
                    for (Edge e : vertices.get(i).getEdges())
                        root.getChildren().remove(e);
                }

            }

            System.out.println("Refresh");
        });

        timeline = new Timeline();
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

    }

    /**
     * Exemple d'initialisation du graphe (à remplacer par l'interface graphique)
     */
    private void testInit() {

        // Initialisation du graphe avec le fichier à charger, la méthode de similitude
        // et la méthode de détection de communautés
        init(fichier.getAbsolutePath(), measureCode, methodCode);

        setScreenSize(WIDTH, HEIGHT); // Taille de l'écran du graphe
        setRefreshRate(1); // Fréquence de mise à jour du graphe (en secondes)
        setBackGroundColor("#000000"); // Couleur de fond du graphe (au format hexadécimal)
        setUpscale(5); // Facteur d'agrandissement pour le graphe
        setInitialNodeSize(3); // Taille initiale d'un sommet
        setDegreeScaleFactor(0.3); // Facteur d'agrandissement selon le degré d'un sommet
    }

    /**
     * Exemple d'actions sur le graphe (en attendant l'interface graphique)
     * 
     * @param root Racine du graphe
     */
    private void testActions(Pane root) {

        Timeline tl1 = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {
                    setMode(GraphData.GraphMode.MOVE);
                    System.out.print("Switch to " + getMode());
                    System.out.println(" - Vous pouvez vous déplacer dans le graphe");
                }));
        tl1.setCycleCount(1);
        tl1.play();

        Timeline tl2 = new Timeline(
                new KeyFrame(Duration.seconds(20), e -> {
                    setMode(GraphData.GraphMode.SELECTION);
                    System.out.print("Switch to " + getMode());
                    System.out.println(" - Vous pouvez sélectionner et déplacer des sommets");
                }));
        tl2.setCycleCount(1);
        tl2.play();

        Timeline tl3 = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    setMode(GraphData.GraphMode.RUN);
                    System.out.print("Back to " + getMode());
                    System.out.println(" - Exécution du graphe (en mouvement)");
                }));
        tl3.setCycleCount(1);
        tl3.play();

        Timeline tl4 = new Timeline(
                new KeyFrame(Duration.seconds(40), e -> {
                    setMiniumDegree(10);
                    System.out.print("Remove nodes with degree < 10");
                }));
        tl4.setCycleCount(1);
        tl4.play();

        Timeline tl5 = new Timeline(
                new KeyFrame(Duration.seconds(45), e -> {
                    setBackGroundColor("#FFFFFF");
                    System.out.println("Change background color to white");
                }));
        tl5.setCycleCount(1);
        tl5.play();

        Timeline tl6 = new Timeline(
                new KeyFrame(Duration.seconds(50), e -> {
                    setMiniumDegree(0);
                    System.out.println("Reset minimum degree restriction");
                }));
        tl6.setCycleCount(1);
        tl6.play();

    }

    private void enableDrag(Pane root) {
        final double[] lastMouseX = { 0 };
        final double[] lastMouseY = { 0 };

        root.setOnMousePressed(event -> {
            lastMouseX[0] = event.getSceneX();
            lastMouseY[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - lastMouseX[0];
            double deltaY = event.getSceneY() - lastMouseY[0];

            translate.setX(translate.getX() + deltaX);
            translate.setY(translate.getY() + deltaY);

            lastMouseX[0] = event.getSceneX();
            lastMouseY[0] = event.getSceneY();
        });

    }

    private void disableDrag(Pane root) {
        root.setOnMousePressed(null);
        root.setOnMouseDragged(null);
    }

    private void enableZoom(Pane root) {
        root.setOnScroll(event -> {
            double zoomDelta = event.getDeltaY() > 0 ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            double oldZoom = zoomFactor.get();
            double newZoom = oldZoom * zoomDelta;

            // Appliquer les limites de zoom
            newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));

            if (newZoom != oldZoom) {
                // Calculer le point de zoom par rapport au conteneur
                double mouseX = event.getX();
                double mouseY = event.getY();

                // Ajuster la translation pour zoomer vers le point de la souris
                double scaleChange = newZoom / oldZoom;
                translateX.set(translateX.get() - (mouseX * (scaleChange - 1)));
                translateY.set(translateY.get() - (mouseY * (scaleChange - 1)));

                zoomFactor.set(newZoom);
                applyTransforms();
            }

            event.consume();
        });
    }

    private void applyTransforms() {
        scale.setX(zoomFactor.get());
        scale.setY(zoomFactor.get());
        translate.setX(translateX.get());
        translate.setY(translateY.get());
    }

    private void disableZoom(Pane root) {
        root.setOnScroll(null);
    }

    @Override
    public void constrainGraphToBounds() {
        // Calculer les limites visibles
        double visibleWidth = graphContainer.getWidth();
        double visibleHeight = graphContainer.getHeight();

        // Calculer les limites du contenu
        double contentWidth = root.getBoundsInLocal().getWidth() * zoomFactor.get();
        double contentHeight = root.getBoundsInLocal().getHeight() * zoomFactor.get();

        // Limiter la translation pour que le graphe ne sorte pas des limites
        double maxTranslateX = Math.max(0, contentWidth - visibleWidth) / 2;
        double minTranslateX = -maxTranslateX;
        double maxTranslateY = Math.max(0, contentHeight - visibleHeight) / 2;
        double minTranslateY = -maxTranslateY;

        translateX.set(Math.max(minTranslateX, Math.min(maxTranslateX, translateX.get())));
        translateY.set(Math.max(minTranslateY, Math.min(maxTranslateY, translateY.get())));

        applyTransforms();
    }

    // Initialisation

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
    @Override
    public double[][] init(String path, GraphData.SimilitudeMode mode, GraphData.NodeCommunity community) {
        // Appeler startsProgram avant d'utiliser les données natives
        double[][] data = startsProgram(path);

        // Déterminer le mode de similitude à utiliser
        int modeSimilitude = getModeSimilitude(mode);

        init_metadata = computeThreshold(modeSimilitude);
        if (init_metadata == null)
            throw new RuntimeException("Une erreur est survenue lors du calcul des seuils.");

        double recommendedThreshold = init_metadata.getEdgeThreshold();
        double recommendedAntiThreshold = init_metadata.getAntiThreshold();

        System.out.println("Seuil recommandé pour les arêtes : " + recommendedThreshold);
        System.out.println("Seuil recommandé pour les anti-arêtes : " + recommendedAntiThreshold);

        // Valeurs imposées pour le moment (à modifier)
        recommendedThreshold = 0.966;
        recommendedAntiThreshold = 0.6;

        // Déterminer le mode de détection de communautés à utiliser
        int modeCommunity = getModeCommunity(community);

        metadata = initiliazeGraph(modeCommunity, recommendedThreshold, recommendedAntiThreshold);

        return data;
    }

    /**
     * Initialise la taille de l'écran du graphe
     * 
     * @param width  Largeur de l'écran (en px)
     * @param height Hauteur de l'écran (en px)
     */
    @Override
    public void setScreenSize(double width, double height) {
        WIDTH = (int) width;
        HEIGHT = (int) height;
        // setDimension(WIDTH, HEIGHT); // TODO
    }

    // Mode de sélection

    /**
     * @return le mode actuel du graphe
     * @see GraphData.GraphMode
     */
    @Override
    public GraphData.GraphMode getMode() {
        if (isRunMode.get()) {
            return GraphData.GraphMode.RUN;
        } else if (isSelectionMode.get()) {
            return GraphData.GraphMode.SELECTION;
        } else if (isMoveMode.get()) {
            return GraphData.GraphMode.MOVE;
        } else if (isDeleteMode.get()) {
            return GraphData.GraphMode.DELETE;
        }
        return null;
    }

    /**
     * Définit le mode du graphe
     * 
     * @param mode le mode à définir
     * @see GraphData.GraphMode
     */
    @Override
    public void setMode(GraphData.GraphMode mode) {
        assert mode != null : "Mode non défini.";
        isRunMode.set(mode == GraphData.GraphMode.RUN);
        isSelectionMode.set(mode == GraphData.GraphMode.SELECTION);
        isMoveMode.set(mode == GraphData.GraphMode.MOVE);
        isDeleteMode.set(mode == GraphData.GraphMode.DELETE);
    }

    private void updateZoom(double zoomFactor) {
        /*
         * double newScaleX = scale.getX() * zoomFactor;
         * double newScaleY = scale.getY() * zoomFactor;
         * 
         * if (newScaleX >= 0.5 && newScaleX <= 5) {
         * scale.setX(newScaleX);
         * scale.setY(newScaleY);
         * }
         */

        // TODO
    }

    public void zoomIn() {
        // updateZoom(1.1);

        // TODO
    }

    public void zoomOut() {
        // updateZoom(0.9);

        // TODO
    }

    // Paramètres de la simulation

    /**
     * @return le seuil recommandé pour les arêtes
     */
    @Override
    public double getRecommendedThreshold() {
        if (init_metadata == null)
            throw new RuntimeException("Métadonnées non initialisées. Veuillez appeler init() avant.");
        return init_metadata.getEdgeThreshold();
    }

    /**
     * @return le seuil recommandé pour les anti-arêtes
     */
    @Override
    public double getRecommendedAntiThreshold() {
        if (init_metadata == null)
            throw new RuntimeException("Métadonnées non initialisées. Veuillez appeler init() avant.");
        return init_metadata.getAntiThreshold();
    }

    /**
     * @return le seuil actuel pour les arêtes
     */
    @Override
    public double getThreshold() {
        if (metadata == null)
            throw new RuntimeException("Métadonnées non initialisées. Veuillez appeler init() avant.");
        return metadata.getEdgeThreshold();
    }

    /**
     * @return le seuil actuel pour les anti-arêtes
     */
    @Override
    public double getAntiThreshold() {
        if (metadata == null)
            throw new RuntimeException("Métadonnées non initialisées. Veuillez appeler init() avant.");
        return metadata.getAntiThreshold();
    }

    /**
     * Change le seuil pour les arêtes
     * 
     * @param threshold Nouveau seuil pour les arêtes
     */
    @Override
    public void setThreshold(double threshold) {
        // TODO
    }

    /**
     * Change le seuil pour les anti-arêtes
     * 
     * @param antiThreshold Nouveau seuil pour les anti-arêtes
     */
    @Override
    public void setAntiThreshold(double antiThreshold) {
        // TODO
    }

    /**
     * @param upscale Facteur d'agrandissement pour le graphe
     */
    @Override
    public void setUpscale(int upscale) {
        assert upscale > 0 : "Facteur d'agrandissement invalide.";
        Vertex.upscale = upscale;
    }

    /**
     * @param size Taille initiale d'un sommet
     */
    @Override
    public void setInitialNodeSize(double size) {
        assert size > 0 : "Taille initiale invalide.";
        Vertex.initial_node_size = size;
    }

    /**
     * @param factor Facteur d'agrandissement selon le degré d'un sommet (0 pour que
     *               la taille soit identique pour tous les sommets, > 0 pour faire
     *               varier la taille proportionnellement au degré)
     */
    @Override
    public void setDegreeScaleFactor(double factor) {
        assert factor >= 0 : "Facteur d'agrandissement invalide.";
        Vertex.degree_scale_factor = factor;
    }

    /**
     * Affiche les sommets dont le degré est supérieur ou égal à degree
     * 
     * @param degree Degré minimum des sommets à afficher
     */
    @Override
    public void setMiniumDegree(int degree) {
        assert degree >= 0 : "Degré minimum invalide.";
        minimumDegree.set(degree);
    }

    /**
     * @param rate Intervalle de temps entre chaque mise à jour du graphe (en
     *             secondes)
     */
    @Override
    public void setRefreshRate(double rate) {
        assert rate > 0 : "Fréquence de mise à jour invalide.";
        updateFrequency.set(rate);
    }

    /**
     * @param hexaColor Couleur de fond du graphe (au format hexadécimal)
     */
    @Override
    public void setBackGroundColor(String hexaColor) {
        assert hexaColor != null && !hexaColor.isEmpty() : "Couleur de fond non définie.";
        background_color.set(hexaColor);
    }

    // Options supplémentaires

    /**
     * Exporte le graphe en image PNG
     * 
     * @param path Chemin de l'image PNG à exporter
     */
    @Override
    public void exportToPng(String path) {
        assert path != null && !path.isEmpty() : "Chemin d'export non défini.";
        /*
         * // Déterminer les dimensions réelles du graphe
         * double contentWidth = root.getBoundsInLocal().getWidth();
         * double contentHeight = root.getBoundsInLocal().getHeight();
         * 
         * // Vérifier que la taille n'est pas nulle
         * if (contentWidth <= 0 || contentHeight <= 0) {
         * System.out.println("Erreur : dimensions invalides pour l'export.");
         * return;
         * }
         * 
         * // Créer une image de la taille réelle du graphe
         * WritableImage image = new WritableImage((int) contentWidth, (int)
         * contentHeight);
         * SnapshotParameters params = new SnapshotParameters();
         * params.setTransform(Transform.scale(1, 1));
         * 
         * // Capture l'image complète du graphe
         * root.snapshot(params, image);
         * 
         * // Enregistrement de l'image en PNG
         * File file = new File(path);
         * try {
         * boolean success = ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png",
         * file);
         * if (!success) {
         * throw new IOException("Échec de l'écriture de l'image.");
         * } else {
         * System.out.println("Graph exporté avec succès : " + file.getAbsolutePath());
         * }
         * } catch (IOException e) {
         * throw new RuntimeException("Erreur lors de l'export du graphe en image", e);
         * }
         */

        // TODO
    }

    /**
     * @param mode Mode de similitude
     * @return l'identifiant du mode de similitude
     */
    private static int getModeSimilitude(GraphData.SimilitudeMode mode) {
        assert mode != null : "Mode de similitude non défini.";
        int modeSimilitude = -1;
        switch (mode) {
            case GraphData.SimilitudeMode.CORRELATION -> modeSimilitude = 0;
            case GraphData.SimilitudeMode.DISTANCE_COSINE -> modeSimilitude = 1;
            case GraphData.SimilitudeMode.DISTANCE_EUCLIDIENNE -> modeSimilitude = 2;
            case GraphData.SimilitudeMode.NORME_L1 -> modeSimilitude = 3;
            case GraphData.SimilitudeMode.NORME_LINF -> modeSimilitude = 4;
            case GraphData.SimilitudeMode.KL_DIVERGENCE -> modeSimilitude = 5;
        }
        if (modeSimilitude == -1)
            throw new RuntimeException("Mode de similitude non reconnu.");
        return modeSimilitude;
    }

    /**
     * @param community Mode de détection de communautés
     * @return l'identifiant du mode de détection de communautés
     */
    private static int getModeCommunity(GraphData.NodeCommunity community) {
        assert community != null : "Mode de détection de communautés non défini.";
        int modeCommunity = -1;
        switch (community) {
            case GraphData.NodeCommunity.LOUVAIN -> modeCommunity = 0;
            case GraphData.NodeCommunity.LOUVAIN_PAR_COMPOSANTE -> modeCommunity = 1;
            case GraphData.NodeCommunity.LEIDEN -> modeCommunity = 2;
            case GraphData.NodeCommunity.LEIDEN_CPM -> modeCommunity = 3;
            case GraphData.NodeCommunity.COULEURS_SPECIALES -> modeCommunity = 4;
        }
        if (modeCommunity == -1)
            throw new RuntimeException("Mode de détection de communautés non reconnu.");
        return modeCommunity;
    }

}
