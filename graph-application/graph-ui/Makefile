# Compilateur et options
CC = gcc
FLAGS = -Wall -g -lGL -lGLU -lglut -fPIC -lm -lc -shared
JNI_FLAGS = -I/usr/lib/jvm/java-21-openjdk-amd64/include -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -fPIC

# Répertoires
SRC_DIR = src/main/java/com/mongraphe/graphui
OUT_DIR = out

# Configuration JavaFX
JAVAFX_DIR = lib/javafx-sdk-23.0.2/lib
JAVAFX_MODULES = javafx.controls,javafx.fxml,javafx.graphics,javafx.swing

# Fichiers Java
GRAPH_JAVA = $(SRC_DIR)/*.java
GRAPH_JAVA_MAIN = $(SRC_DIR)/Graph.java
GRAPH_JAVA_OUT = -cp $(OUT_DIR) com.mongraphe.graphui.Graph

#Fichier C pour le graphe
GRAPH_C_DIR  = c/c_graph
GRAPH_C_DEPS = $(GRAPH_C_DIR)/%.h
GRAPH_C_SRC  = $(GRAPH_C_DIR)/%.c
GRAPH_C_OBJ  =  graph.o cluster.o communities.o
GRAPH_C	   = $(patsubst %, $(GRAPH_C_DIR)/%,$(GRAPH_C_OBJ))

DEBUG_DIR  = c/debug
DEBUG_DEPS = $(DEBUG_DIR)/%.h
DEBUG_SRC = $(DEBUG_DIR)/%.c
DEBUG_OBJ = debug_time.o
DEBUG = $(patsubst %, $(DEBUG_DIR)/%,$(DEBUG_OBJ))

#Fichier C d'outils pour la concurrence
CONCURRENT_DIR  = c/concurrent
CONCURRENT_DEPS = $(CONCURRENT_DIR)/%.h
CONCURRENT_SRC  = $(CONCURRENT_DIR)/%.c
CONCURRENT_OBJ  = Pool.o Barrier.o Tools.o
CONCURRENT = $(patsubst %,$(CONCURRENT_DIR)/%,$(CONCURRENT_OBJ))

#Fichier C pour le pretraitement des données
PRETRAITEMENT_DIR = c/pretraitement
PRETRAITEMENT_DEPS = $(PRETRAITEMENT_DIR)/%.h
PRETRAITEMENT_SRC = $(PRETRAITEMENT_DIR)/%.c
PRETRAITEMENT_OBJ = data.o similarity.o
PRETRAITEMENT = $(patsubst %,$(PRETRAITEMENT_DIR)/%,$(PRETRAITEMENT_OBJ))

# Fichiers ForceAtlas
FORCE_ATLAS = c/forceatlasV4_CSV.c
FORCE_ATLAS_OUT = $(OUT_DIR)/forceatlas.o
LIBNATIVE_OUT = $(OUT_DIR)/libnative.so

# Nom de sortie
OUT_NAME = forceatlas

# Règles de compilation
all: $(CONCURRENT) $(GRAPH_C) $(PRETRAITEMENT)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -d $(OUT_DIR) $(GRAPH_JAVA)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -h $(OUT_DIR) -d $(OUT_DIR) $(GRAPH_JAVA)

	$(CC) $(JNI_FLAGS) -c $(FORCE_ATLAS) -o $(FORCE_ATLAS_OUT)
	$(CC) $(FORCE_ATLAS_OUT) $(CONCURRENT) $(GRAPH_C) $(PRETRAITEMENT) -o $(LIBNATIVE_OUT) $(FLAGS)

# Règle pour compiler les fichiers de débogage
$(DEBUG_DIR)/%.o: $(DEBUG_SRC) $(DEBUG_DEPS)
	$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

# Règle pour compiler les fichiers concurrents
$(CONCURRENT_DIR)/%.o: $(CONCURRENT_SRC) $(CONCURRENT_DEPS)
	$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

# Règle pour compiler les fichiers C du graphe
$(GRAPH_C_DIR)/%.o: $(GRAPH_C_SRC) $(GRAPH_C_DEPS)
			$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

# Règle pour compiler les fichiers C du pretraitement
$(PRETRAITEMENT_DIR)/%.o: $(PRETRAITEMENT_SRC) $(PRETRAITEMENT_DEPS)
			$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

# Nettoyage
clean:
	-rm -rf $(OUT_DIR)/*.o
	-rm -rf $(OUT_DIR)/*.h
	-rm -rf $(OUT_DIR)/com/mongraphe/graphui/*.class
	-rm -rf $(CONCURRENT_DIR)/*.o
	-rm -rf $(GRAPH_C_DIR)/*.o
	-rm -f $(LIBNATIVE_OUT)
