CC = gcc-14
FLAGS = -Wall -g -lGL -lGLU -lglut -fPIC -lm -lc

SRC_DIR = .

DEBUG_DIR  = ../src/c/debug
DEBUG_DEPS = $(DEBUG_DIR)/%.h
DEBUG_SRC = $(DEBUG_DIR)/%.c
DEBUG_OBJ = debug_time.o
DEBUG = $(patsubst %, $(DEBUG_DIR)/%,$(DEBUG_OBJ))

FORCE_ATLAS = $(SRC_DIR)/forceatlasV4_CSV2.c
FORCE_ATLAS_OUT = $(SRC_DIR)/forceatlasV4_CSV2.o

OUT_NAME = forceatlas

DIR_SAMPLES = ../samples
DIR_SAMPLE = $(DIR_SAMPLES)/iris.csv

$(DEBUG_DIR)/%.o: $(DEBUG_SRC) $(DEBUG_DEPS)
	$(CC) -c -o $@ $< $(FLAGS) $(JNI_FLAGS)

debug: $(DEBUG)
	$(CC) -c $(FORCE_ATLAS) -D_DEBUG_ $(FLAGS) -o $(FORCE_ATLAS_OUT)
	$(CC) $(FORCE_ATLAS_OUT) $(DEBUG) $(FLAGS) -o forceatlas

clean:
	- rm -rf $(OUT_DIR)/*.o
	- rm -rf $(OUT_DIR)/*.h
	- rm -rf $(OUT_DIR)/*/*.class
	- rm -rf $(SRC_DIR)/*/*.o
	- rm -rf $(SRC_DIR)/*/*/*.o
	- rm -rf $(GRAPH_C_DIR)/*.o
	- rm -rf $(shell find out -mindepth 1 -type d)
