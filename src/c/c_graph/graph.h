#ifndef FORCEATLAS_GRAPH_H
#define FORCEATLAS_GRAPH_H

#include <math.h>
#include <stddef.h>
#include <stdlib.h>
#include <jni.h>

// Structures de données pour représenter les arêtes, points, et clusters
typedef struct {
    int node1;
    int node2;
    double weight;
} Edge;

typedef struct {
    double x;
    double y;
} Point;

void initialize_vertices(JNIEnv* env);
void setNewVertex(JNIEnv* env, int index, double x, double y);
void setVertex(JNIEnv* env, int index, double x, double y);
jdouble getVertex_x(JNIEnv* env, int index);
jdouble getVertex_y(JNIEnv* env, int index);
Point getVertex(JNIEnv* env, int index);

// Calculer un vecteur avec un enroulement toroïdal
void toroidal_vector(Point *dir, Point p1, Point p2);
void calculate_node_degrees(void);
void random_point_in_center(JNIEnv* env, int index);
void translate_positions(JNIEnv* env, double dx, double dy);
double toroidal_distance(Point p1, Point p2);

// Fonction update_position
void repulsion_edges(JNIEnv* env, Point* forces);
void repulsion_anti_edges(JNIEnv* env, Point* forces);
double update_position_forces(JNIEnv* env, Point* forces, double PasMaxX, double PasMaxY, double Max_movement);
void normalize(Point *p);

void calculate_similitude_and_edges(int mode_similitude, double threshold, double antiseuil);

#endif