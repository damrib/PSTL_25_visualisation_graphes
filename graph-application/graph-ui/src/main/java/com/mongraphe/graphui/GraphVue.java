package com.mongraphe.graphui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import com.mongraphe.graphui.GraphData.NodeCommunity;
import com.mongraphe.graphui.GraphData.SimilitudeMode;


import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
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
	private AnchorPane graphContainer;
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

	///////////////////////

	// Nouveaux éléments FXML pour le panneau Aspect du graphe

	@FXML private ToggleGroup elementToggleGroup;
	@FXML private ToggleButton nodesToggleButton;
	@FXML private ToggleButton edgesToggleButton;
	@FXML private ColorPicker elementColorPicker;
	@FXML private ComboBox<String> elementRankingCombo;
	@FXML private Consumer<String> onRankingSelectedCallback;
	
	private Graph graph;

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
		graph =new Graph();
		System.out.println("Bouton démarrer cliqué !");
		graphInit();
		graphContainer.getChildren().clear(); // Nettoyer le conteneur
		graphContainer.getChildren().add(root);
	}

	/**
	 * Méthode principale du graphe
	 */
	public void graphInit() {

		root = new Pane();
		root.setId("dynamicPane");
		root.setLayoutX(0);
		root.setLayoutY(0);

		Graph.isRunMode.addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				graph.animator.resume();
				System.out.println("Reprise de l'animation");
			} else {
				graph.animator.pause();
				System.out.println("Pause de l'animation");
			}
		});

		testInit();

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
			graph.vertices.get(i).setCommunity(communities.get(community_id)); // Attribution de la communauté à chaque sommet
		}

		graph.edges = new ArrayList<>();
		EdgeC[] edgesC = graph.getEdges();
		for (EdgeC edgeC : edgesC) {
			Edge e = new Edge(graph.vertices.get(edgeC.getStart()), graph.vertices.get(edgeC.getEnd()), edgeC.getWeight());
			graph.edges.add(e);
		}

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

		// Créer un NewtCanvasJFX pour intégrer OpenGL dans JavaFX
		NewtCanvasJFX newtCanvas = new NewtCanvasJFX(graph.glWindow);
		newtCanvas.setWidth(WIDTH);
		newtCanvas.setHeight(HEIGHT);

		// Ajouter les listeners pour la souris
		graph.addMouseListeners();

		// Créer la scène JavaFX avec le SwingNode
		root.getChildren().add(newtCanvas);

		// Démarrer l'animation
		graph.animator = new FPSAnimator(graph.glWindow, 60);
		graph.animator.start();
		

	}

	private void testInit() {

		// Initialisation du graphe avec le fichier à charger, la méthode de similitude
		// et la méthode de détection de communautés
		graph.initGraph(fichier.getAbsolutePath(), measureCode, methodCode);

		graph.setScreenSize(WIDTH, HEIGHT);

		graph.setScreenSize(WIDTH, HEIGHT); // Taille de l'écran du graphe
		graph.setBackgroundColor(0.0f, 0.0f, 0.0f);; // Couleur de fond du graphe (au format hexadécimal)
		graph.setUpscale(5); // Facteur d'agrandissement pour le graphe
		graph.setInitialNodeSize(3); // Taille initiale d'un sommet
		graph.setDegreeScaleFactor(0.3); // Facteur d'agrandissement selon le degré d'un sommet
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

	@FXML
	private void applyMinDegree() {
		int newDegreeMin = (int) degreeMinSlider.getValue();
		minimumDegree.set(newDegreeMin);
		degreeMinValue.setText(String.valueOf(newDegreeMin));
		System.out.println("Nouveau degré minimum : " + newDegreeMin);
	}

	// Réinitialiser les paramètres du graphe
	@FXML
	private void resetGraphSettings() {
		degreeMinSlider.setValue(3);
		degreeMinValue.setText("3");
		minimumDegree.set(3);

		sizeFilterComboBox.getSelectionModel().select("Tous");
		showIsolatedNodesCheckbox.setSelected(true);

	}


	public static int WIDTH = 1500; // Largeur de la fenêtre
	public static int HEIGHT = 800; // Hauteur de la fenêtre

	// Propriété pour le degré minimum des sommets
	public static final IntegerProperty minimumDegree = new SimpleIntegerProperty(0);

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
