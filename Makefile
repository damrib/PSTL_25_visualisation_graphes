CC = gcc
#FLAG est envoyé par le bash script "build.sh" comme cela depend de l'OS
JNI_FLAGS = -I$(shell /usr/libexec/java_home)/include -I$(shell /usr/libexec/java_home)/include/darwin -fPIC
SRC_DIR = src
OUT_DIR = out
JAVAFX_MODULES = javafx.controls,javafx.fxml

GRAPH_JAVA = $(SRC_DIR)/*/*.java
GRAPH_JAVA_MAIN = $(SRC_DIR)/graph/Graph.java
GRAPH_JAVA_OUT = -cp $(OUT_DIR) graph.Graph

GRAPH_C_DIR  = $(SRC_DIR)/c/c_graph
GRAPH_C_DEPS = $(GRAPH_C_DIR)/%.h
GRAPH_C_SRC  = $(GRAPH_C_DIR)/%.c
GRAPH_C_OBJ  =  graph.o cluster.o communities.o
GRAPH_C	   = $(patsubst %, $(GRAPH_C_DIR)/%,$(GRAPH_C_OBJ))

DEBUG_DIR  = $(SRC_DIR)/c/debug
DEBUG_DEPS = $(DEBUG_DIR)/%.h
DEBUG_SRC = $(DEBUG_DIR)/%.c
DEBUG_OBJ = debug_time.o
DEBUG = $(patsubst %, $(DEBUG_DIR)/%,$(DEBUG_OBJ))

CONCURRENT_DIR  = $(SRC_DIR)/c/concurrent
CONCURRENT_DEPS = $(CONCURRENT_DIR)/%.h
CONCURRENT_SRC  = $(CONCURRENT_DIR)/%.c
CONCURRENT_OBJ  = Pool.o Barrier.o Tools.o
CONCURRENT = $(patsubst %,$(CONCURRENT_DIR)/%,$(CONCURRENT_OBJ))

PRETRAITEMENT_DIR = $(SRC_DIR)/c/pretraitement
PRETRAITEMENT_DEPS = $(PRETRAITEMENT_DIR)/%.h
PRETRAITEMENT_SRC = $(PRETRAITEMENT_DIR)/%.c
PRETRAITEMENT_OBJ = data.o similarity.o
PRETRAITEMENT = $(patsubst %,$(PRETRAITEMENT_DIR)/%,$(PRETRAITEMENT_OBJ))

FORCE_ATLAS = $(SRC_DIR)/c/forceatlasV4_CSV.c
FORCE_ATLAS_OUT = $(OUT_DIR)/forceatlas.o
LIBNATIVE_OUT = $(OUT_DIR)/libnative.so

OUT_NAME = forceatlas

DIR_SAMPLES = samples
DIR_SAMPLE = $(DIR_SAMPLES)/iris.csv

$(DEBUG_DIR)/%.o: $(DEBUG_SRC) $(DEBUG_DEPS)
	$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

$(CONCURRENT_DIR)/%.o: $(CONCURRENT_SRC) $(CONCURRENT_DEPS)
	$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

$(GRAPH_C_DIR)/%.o: $(GRAPH_C_SRC) $(GRAPH_C_DEPS)
			$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

$(PRETRAITEMENT_DIR)/%.o: $(PRETRAITEMENT_SRC) $(PRETRAITEMENT_DEPS)
			$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

all: $(CONCURRENT) $(GRAPH_C) $(PRETRAITEMENT)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -d $(OUT_DIR) $(GRAPH_JAVA)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -h $(OUT_DIR) -d $(OUT_DIR) $(GRAPH_JAVA)

	$(CC) $(JNI_FLAGS) -c $(FORCE_ATLAS) -o $(FORCE_ATLAS_OUT)
	$(CC) $(FORCE_ATLAS_OUT) $(CONCURRENT) $(GRAPH_C) $(PRETRAITEMENT) -o $(LIBNATIVE_OUT) $(FLAGS)

	java -Djava.library.path=. --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) $(GRAPH_JAVA_OUT) $(DIR_SAMPLE)

debug: $(DEBUG) $(CONCURRENT) $(GRAPH_C) $(PRETRAITEMENT)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -d $(OUT_DIR) $(GRAPH_JAVA)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -h $(OUT_DIR) -d $(OUT_DIR) $(GRAPH_JAVA)

	$(CC) $(JNI_FLAGS) -c $(FORCE_ATLAS) -D_DEBUG_ -o $(FORCE_ATLAS_OUT)
	$(CC) $(FORCE_ATLAS_OUT) $(CONCURRENT) $(GRAPH_C) $(DEBUG) $(PRETRAITEMENT) -o $(LIBNATIVE_OUT) $(FLAGS)

	java -Djava.library.path=. --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) $(GRAPH_JAVA_OUT) $(DIR_SAMPLE)

clean:
	- rm -rf $(OUT_DIR)/*.o
	- rm -rf $(OUT_DIR)/*.h
	- rm -rf $(OUT_DIR)/*/*.class
	- rm -rf $(SRC_DIR)/*/*.o
	- rm -rf $(SRC_DIR)/*/*/*.o
	- rm -rf $(GRAPH_C_DIR)/*.o
	- rm -rf $(shell find out -mindepth 1 -type d)
