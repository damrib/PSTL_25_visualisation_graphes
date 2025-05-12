# Détection du système d'exploitation
UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
    OS_TYPE = Linux
    OUT_DIR = out/linux
    JNI_FLAGS = -I/usr/lib/jvm/java-21-openjdk-amd64/include -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -fPIC
    FLAGS = -Wall -g -fPIC -lm -lc -shared
    LIBNATIVE_OUT = $(OUT_DIR)/libnative.so
    RM = rm -rf
    CLASSPATH_SEP = :
else ifeq ($(UNAME_S),Darwin)
    OS_TYPE = macOS
    OUT_DIR = out/macos
    JNI_FLAGS = -I$(shell /usr/libexec/java_home)/include -I$(shell /usr/libexec/java_home)/include/darwin -fPIC
    FLAGS = -Wall -g -dynamiclib
    LIBNATIVE_OUT = $(OUT_DIR)/libnative.dylib
    RM = rm -rf
    CLASSPATH_SEP = :
else
    OS_TYPE = Windows
    OUT_DIR = out/windows
    JNI_FLAGS = -I"C:/Program Files/Java/jdk-21/include" -I"C:/Program Files/Java/jdk-21/include/win32" -fPIC
    FLAGS = -Wall -g -fPIC -lm -shared
    LIBNATIVE_OUT = $(OUT_DIR)/libnative.dll
    RM = cmd /C "del /F /Q
    CLASSPATH_SEP = ;

endif

# Répertoires
SRC_DIR = src/main/java/com/mongraphe/graphui

# Configuration JavaFX
JAVAFX_DIR = lib/javafx-sdk-23.0.2/lib
JAVAFX_MODULES = javafx.controls,javafx.fxml,javafx.graphics,javafx.swing

# Définition des fichiers sources et objets
GRAPH_C_DIR = c/c_graph
DEBUG_DIR = c/debug
CONCURRENT_DIR = c/concurrent
PRETRAITEMENT_DIR = c/pretraitement

GRAPH_C_SRC = $(wildcard $(GRAPH_C_DIR)/*.c)
GRAPH_C_OBJ = $(patsubst $(GRAPH_C_DIR)/%.c, $(OUT_DIR)/%.o, $(GRAPH_C_SRC))

DEBUG_SRC = $(wildcard $(DEBUG_DIR)/*.c)
DEBUG_OBJ = $(patsubst $(DEBUG_DIR)/%.c, $(OUT_DIR)/%.o, $(DEBUG_SRC))

CONCURRENT_SRC = $(wildcard $(CONCURRENT_DIR)/*.c)
CONCURRENT_OBJ = $(patsubst $(CONCURRENT_DIR)/%.c, $(OUT_DIR)/%.o, $(CONCURRENT_SRC))

PRETRAITEMENT_SRC = $(wildcard $(PRETRAITEMENT_DIR)/*.c)
PRETRAITEMENT_OBJ = $(patsubst $(PRETRAITEMENT_DIR)/%.c, $(OUT_DIR)/%.o, $(PRETRAITEMENT_SRC))

FORCE_ATLAS_SRC = c/forceatlasV4_CSV.c
FORCE_ATLAS_OBJ = $(OUT_DIR)/forceatlas.o

JOGL_DIR = lib/jogl
JOGL_JARS = $(JOGL_DIR)/jogl-all.jar:$(JOGL_DIR)/gluegen-rt.jar
JOGL_NATIVE_LIBS = $(JOGL_DIR)/jogl-all-natives-linux-amd64.jar:$(JOGL_DIR)/gluegen-rt-natives-linux-amd64.jar
CLASSPATH = $(JOGL_JARS):$(JOGL_NATIVE_LIBS)

OBJ_FILES = $(GRAPH_C_OBJ) $(DEBUG_OBJ) $(CONCURRENT_OBJ) $(PRETRAITEMENT_OBJ) $(FORCE_ATLAS_OBJ)

# Vérifier si le dossier de sortie existe
$(shell mkdir -p $(OUT_DIR))

# Compilation
all: java $(LIBNATIVE_OUT) 

# Compilation de la bibliothèque native
$(LIBNATIVE_OUT): $(OBJ_FILES)
	$(CC) $^ -o $@ $(FLAGS)

# Compilation des fichiers .c
$(OUT_DIR)/%.o: $(GRAPH_C_DIR)/%.c
	$(CC) -c $< -o $@ $(FLAGS) $(JNI_FLAGS)

$(OUT_DIR)/%.o: $(DEBUG_DIR)/%.c
	$(CC) -c $< -o $@ $(FLAGS) $(JNI_FLAGS)

$(OUT_DIR)/%.o: $(CONCURRENT_DIR)/%.c
	$(CC) -c $< -o $@ $(FLAGS) $(JNI_FLAGS)

$(OUT_DIR)/%.o: $(PRETRAITEMENT_DIR)/%.c
	$(CC) -c $< -o $@ $(FLAGS) $(JNI_FLAGS)

$(OUT_DIR)/forceatlas.o: $(FORCE_ATLAS_SRC)
	$(CC) -c $< -o $@ $(FLAGS) $(JNI_FLAGS)

# Compilation Java
java:
	javac --module-path $(JAVAFX_DIR) --add-modules $(JAVAFX_MODULES) \
	      -cp $(CLASSPATH) \
	      -h $(OUT_DIR) -d $(OUT_DIR) $(SRC_DIR)/*.java

# Nettoyage
clean:

	$(RM) $(OUT_DIR)/*.o $(OUT_DIR)/*.h $(OUT_DIR)/com/mongraphe/graphui/*.class $(LIBNATIVE_OUT)

