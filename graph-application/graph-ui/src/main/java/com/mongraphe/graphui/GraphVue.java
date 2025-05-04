package com.mongraphe.graphui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class GraphVue {

	@FXML
	private StackPane mainContentPane;
	@FXML
	private GridPane overviewPane;
	@FXML
	private AnchorPane dataPane;
	@FXML
	private AnchorPane previewPane;
	@FXML
	private StackPane graphContainer;
	@FXML
	private Label nodesDeletedLabel;
	@FXML
	private Label nodesDisplayedLabel;
	@FXML
	private Label edgesDisplayedLabel;
	@FXML
	private Label edgesDeletedLabel;
	@FXML
	private Label totalElementsLabel;
	@FXML
	private ToggleGroup viewToggleGroup;
	@FXML
	private ToggleGroup graphModeToggleGroup;

	@FXML
	private Label recommendedTreshold;
	@FXML
	private Label recommendedAntiTreshold;
	@FXML
	private Label treshold;
	@FXML
	private Label antiTreshold;

	///////////////////////

	// Nouveaux éléments FXML pour le panneau Aspect du graphe

	@FXML
	private ToggleGroup elementToggleGroup;
	@FXML
	private ToggleButton nodesToggleButton;
	@FXML
	private ToggleButton edgesToggleButton;
	@FXML
	private ColorPicker elementColorPicker;
	@FXML
	private ComboBox<String> elementRankingCombo;
	@FXML
	private Consumer<String> onRankingSelectedCallback;

	private int compteurNodesReel;
	private int compteurNodesSup;
	private int edgesDisplayed;
	private int edgesDeleted;

	private Graph graph;

	public void handleQuit(ActionEvent event) {
		graph.stop();
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
				graph.setMode(GraphData.GraphMode.RUN);
				break;
			case "SELECTION":
				graph.setMode(GraphData.GraphMode.SELECTION);
				break;
			case "MOVE":
				graph.setMode(GraphData.GraphMode.MOVE);
				break;
			case "DELETE":
				graph.setMode(GraphData.GraphMode.DELETE);
				break;
		}

		System.out.println("Mode changé : " + mode);
	}

	/**
	 * Gère l'action du bouton "Démarrer".
	 */
	@FXML
	private void handleStartButton() {
		graph = new Graph();
		System.out.println("Bouton démarrer cliqué !");
		graphContainer.getChildren().clear();
		graphInit();
		graphContainer.getChildren().add(root);
		/*
		 * graphContainer.getChildren().clear(); // Nettoyer le conteneur
		 * graphContainer.getChildren().add(root);
		 */
	}

	/**
	 * Méthode principale du graphe
	 */
	public void graphInit() {

		root = new Pane();
		root.setId("dynamicPane");
		/*
		 * root.setLayoutX(0);
		 * root.setLayoutY(0);
		 */

		Graph.isRunMode.addListener((obs, oldValue, newValue) -> {
			if (newValue) {
	            System.out.println("Tentative de reprise de l'animation...");
	            if (!graph.animator.isAnimating()) {
	                graph.animator.resume();
	                System.out.println("Reprise de l'animation réussie");
	            }
			} else {
				System.out.println("Tentative de pause de l'animation...");
	            if (graph.animator.isAnimating()) {
	                graph.animator.pause();
	                System.out.println("Pause de l'animation réussie");
	            }
			}
		});

		testInit();
		System.out.println("vertices : " + graph.getPositions().length);
		System.out.println("edges : " + graph.getEdges().length);
		System.out.println("comminuties : " + graph.getCommunities().length);

		graph.vertices = List.of(graph.getPositions());

		float[][] color = graph.getClusterColors();

		int[] communititesInterm = graph.getCommunities();
		HashMap<Integer, Community> communities = new HashMap<>();

		for (int i = 0; i < graph.vertices.size(); i++) {
			int community_id = communititesInterm[i];
			if (!communities.containsKey(community_id)) {
				// Créer une nouvelle communauté
				communities.put(community_id, new Community(community_id, color[i][0], color[i][1], color[i][2]));
			}
			graph.vertices.get(i).setId(i); // Attribution d'un identifiant unique à chaque sommet
			graph.vertices.get(i).setCommunity(communities.get(community_id)); // Attribution de la communauté à chaque
																				// sommet
		}

		graph.edges = new ArrayList<>();
		EdgeC[] edgesC = graph.getEdges();
		for (EdgeC edgeC : edgesC) {
			Edge e = new Edge(graph.vertices.get(edgeC.getStart()), graph.vertices.get(edgeC.getEnd()),
					edgeC.getWeight());
			graph.edges.add(e);
		}

		this.compteurNodesReel = 0;
		this.compteurNodesSup = 0;
		this.edgesDisplayed = 0;
		this.edgesDeleted = 0;

		// Ajuster les rayons des sommets selon leur degré
		for (Vertex v : graph.vertices)
			v.updateDiameter();

		// Initialisation des buffers pour les sommets et arêtes
		graph.initializeArrays();

		// Initialisation de OpenGL avec JOGL
		GLProfile glProfile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(glProfile);
		capabilities.setDoubleBuffered(true);
		capabilities.setHardwareAccelerated(true);
		// Créer un GLWindow (OpenGL)
		graph.glWindow = GLWindow.create(capabilities);

		graph.glWindow.addGLEventListener(graph);

		NewtCanvasJFX newtCanvas = new NewtCanvasJFX(graph.glWindow);

		// Et au départ, assure une taille de base
		newtCanvas.setWidth(graphContainer.getWidth());
		newtCanvas.setHeight(graphContainer.getHeight());

		// Ajouter les listeners pour la souris
		graph.addMouseListeners();
		
		graph.addKeyListeners();

		// S'assurer que le canvas est bien visible
		newtCanvas.setVisible(true);

		root.getChildren().add(newtCanvas);
		newtCanvas.requestFocus();

		// Démarrer l'animation OpenGL
		graph.animator = new FPSAnimator(graph.glWindow, 60);
		graph.animator.setExclusiveContext(false);
		graph.animator.start();
		System.out.println("Animation démarrée");

		minimumDegree.setText(graph.getMinimumDegree() + "");
		recommendedTreshold.setText(graph.getRecommendedThreshold() + "");
		recommendedAntiTreshold.setText(graph.getRecommendedAntiThreshold() + "");
		treshold.setText(String.valueOf(graph.getThreshold()));
		antiTreshold.setText(String.valueOf(graph.getAntiThreshold()));

	}

	private void testInit() {

		// Initialisation du graphe avec le fichier à charger, la méthode de similitude
		// et la méthode de détection de communautés
		graph.initGraphCsv(fichier.getAbsolutePath(), measureCode, methodCode);

		graph.setScreenSize(graphContainer.getWidth(), graphContainer.getHeight()); // Taille de l'écran du
																					// graphe
		graph.setBackgroundColor(1.0f, 1.0f, 1.0f);
		// Couleur de fond du graphe (au format hexadécimal)
		graph.setUpscale(5); // Facteur d'agrandissement pour le graphe
		graph.setInitialNodeSize(3); // Taille initiale d'un sommet
		graph.setDegreeScaleFactor(0.9); // Facteur d'agrandissement selon le degré d'un sommet
	}

	//////// options contollers
	@FXML
	private TextField initNodeSize;
	@FXML
	private TextField degreeFactor;
	@FXML
	private TextField upScale;
	@FXML
	private TextField stabilizedTreshold;
	@FXML
	private TextField attractionTreshold;
	@FXML
	private TextField updatedFrequence;
	@FXML
	private TextField newFriction;
	@FXML
	private TextField attractionCoefficient;
	@FXML
	private TextField repulsionTreshold;
	@FXML
	private TextField newAmortissement;
	@FXML
	private TextField nbClusters;
	@FXML
	private TextField minimumDegree;
	@FXML
	private ComboBox<GraphData.RepulsionMode> repulsionModeComboBox;
	@FXML
	private ComboBox<GraphData.SimilitudeMode> mesureChamp;
	@FXML
	private ComboBox<GraphData.NodeCommunity> clusteringChamp;

	@FXML
	private void initialize() {
		repulsionModeComboBox.getItems().setAll(GraphData.RepulsionMode.values());
		mesureChamp.getItems().setAll(GraphData.SimilitudeMode.values());
		mesureChamp.setValue(measureCode);
		clusteringChamp.getItems().setAll(GraphData.NodeCommunity.values());
		clusteringChamp.setValue(methodCode);
	}

	@FXML
	private void applyOptions() {
		try {
			if (!degreeFactor.getText().isEmpty()) {
				graph.setDegreeScaleFactor(Double.parseDouble(degreeFactor.getText()));
			}
			if (!initNodeSize.getText().isEmpty()) {
				graph.setInitialNodeSize(Double.parseDouble(initNodeSize.getText()));
			}
			if (!upScale.getText().isEmpty()) {
				graph.setUpscale(Integer.parseInt(upScale.getText()));
			}
			if (!stabilizedTreshold.getText().isEmpty()) {
				graph.setStabilizedThreshold(Double.parseDouble(stabilizedTreshold.getText()));
			}
			if (!attractionTreshold.getText().isEmpty()) {
				graph.setAttractionThreshold(Double.parseDouble(attractionTreshold.getText()));
			}
			if (!updatedFrequence.getText().isEmpty()) {
				graph.setUpdatedFrequence(Integer.parseInt(updatedFrequence.getText()));
			}
			if (!newFriction.getText().isEmpty()) {
				graph.setNewFriction(Double.parseDouble(newFriction.getText()));
			}
			if (!attractionCoefficient.getText().isEmpty()) {
				graph.setAttractionCoefficient(Double.parseDouble(attractionCoefficient.getText()));
			}
			if (!repulsionTreshold.getText().isEmpty()) {
				graph.setRepulsionThreshold(Double.parseDouble(repulsionTreshold.getText()));
			}
			if (!newAmortissement.getText().isEmpty()) {
				graph.setNewAmortissement(Double.parseDouble(newAmortissement.getText()));
			}
			if (!nbClusters.getText().isEmpty()) {
				graph.setNbClusters(Integer.parseInt(nbClusters.getText()));
			}
			if (!minimumDegree.getText().isEmpty()) {
				graph.setMiniumDegree(Integer.parseInt(minimumDegree.getText()));
			}
			GraphData.RepulsionMode selectedMode = repulsionModeComboBox.getValue();
			if (selectedMode != null) {
				graph.setRepulsionMode(selectedMode);
			}

			// Couleur de fond par défaut
			graph.setBackgroundColor(0.0f, 0.0f, 0.0f);

			for (Vertex v : graph.vertices) {
				v.updateDiameter();
			}
			graph.initializeArrays();
			treshold.setText(String.valueOf(graph.getThreshold()));
			antiTreshold.setText(String.valueOf(graph.getAntiThreshold()));
			graph.glWindow.display();

		} catch (NumberFormatException e) {
			System.err.println("Erreur de format dans un des champs : " + e.getMessage());
		}
	}

	@FXML
	private void applyChangement() {

		// Vérifier si le graphe est déjà initialisé avant de le libérer
		graph.stop();

		System.out.println("------------------------------------" + mesureChamp.getValue());

		if (mesureChamp.getValue() != null) {
			this.measureCode = mesureChamp.getValue();
		}

		if (clusteringChamp.getValue() != null) {
			this.methodCode = clusteringChamp.getValue();
		}
		if (graph.glWindow != null) {
			graph.glWindow.destroy();
		}

		graph = new Graph();
		graphContainer.getChildren().clear();

		if (root != null) {
			root.getChildren().clear();
		}

		graphInit();
		graphContainer.getChildren().add(root);

	}

	/*
	 ** Gère l'action du bouton "Appliquer le degré minimum".
	 * Met à jour le degré minimum et rafraîchit le graphe.
	 * Le degré minimum est défini par un curseur.
	 * Le texte affiché est mis à jour en conséquence.
	 */
	@FXML
	private Slider degreeMinSlider;
	@FXML
	private Label degreeMinValue;
	@FXML
	private ComboBox<String> sizeFilterComboBox;
	@FXML
	private CheckBox showIsolatedNodesCheckbox;

	// Réinitialiser les paramètres du graphe
	@FXML
	private void resetGraphSettings() {

	}

	public static int WIDTH = 1500; // Largeur de la fenêtre
	public static int HEIGHT = 800; // Hauteur de la fenêtre

	// Propriété pour la fréquence de mise à jour du graphe
	public static final DoubleProperty updateFrequency = new SimpleDoubleProperty(1.0);

	// Propriété pour la couleur de fond
	public static final StringProperty background_color = new SimpleStringProperty("#000000");

	private File fichier;
	private SimilitudeMode measureCode;
	private NodeCommunity methodCode;
	private Pane root;

	public void initData(File fichier, SimilitudeMode measureCode, double upThreshold, double downThreshold,
			NodeCommunity methodCode) {
		this.fichier = fichier;
		this.measureCode = measureCode;
		this.methodCode = methodCode;
	}

}
