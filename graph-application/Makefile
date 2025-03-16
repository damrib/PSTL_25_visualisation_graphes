CC = gcc
FLAGS = -Wall -g -fPIC
LDFLAGS = -lGL -lGLU -lglut -lm -lc -shared
JNI_FLAGS = -I/usr/lib/jvm/java-21-openjdk-amd64/include -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -fPIC

SRC_DIR = graph-layout/src/main/java/com/mongraphe/graphlayout
OUT_DIR = out

JAVAFX_DIR = lib/javafx-sdk-23.0.2/lib
JAVAFX_MODULES = javafx.controls,javafx.fxml

GRAPH_JAVA = $(SRC_DIR)/*.java
GRAPH_JAVA_MAIN = $(SRC_DIR)/Graph.java
GRAPH_JAVA_OUT = -cp $(OUT_DIR) com.mongraphe.graphelayout.Graph

CONCURRENT_DIR = concurrent
CONCURRENT_SRC = $(wildcard $(CONCURRENT_DIR)/*.c)
CONCURRENT_OBJ = $(patsubst $(CONCURRENT_DIR)/%.c, $(CONCURRENT_DIR)/%.o, $(CONCURRENT_SRC))

FORCE_ATLAS = c/forceatlasV4_CSV.c
FORCE_ATLAS_OUT = $(OUT_DIR)/forceatlas.o
LIBNATIVE_OUT = $(OUT_DIR)/libnative.so

all: $(LIBNATIVE_OUT)
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) -h $(OUT_DIR) -d $(OUT_DIR) $(GRAPH_JAVA)

$(CONCURRENT_DIR)/%.o: $(CONCURRENT_DIR)/%.c
	$(CC) $(FLAGS) $(JNI_FLAGS) -c $< -o $@

$(FORCE_ATLAS_OUT): $(FORCE_ATLAS)
	$(CC) $(FLAGS) $(JNI_FLAGS) -c $< -o $@

$(LIBNATIVE_OUT): $(FORCE_ATLAS_OUT) $(CONCURRENT_OBJ)
	$(CC) $(FLAGS) $^ -o $@ $(LDFLAGS)

clean:
	-rm -rf $(OUT_DIR)/*.o
	-rm -rf $(OUT_DIR)/*.h
	-rm -rf $(OUT_DIR)/*.class
	-rm -rf $(OUT_DIR)/com/mongraphe/graphlayout/*.class
	-rm -rf $(CONCURRENT_DIR)/*.o
	-rm -f $(LIBNATIVE_OUT)
