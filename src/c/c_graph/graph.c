#include "graph.h"

Edge edges[MAX_EDGES]; // Pour les arêtes normales
char *node_names[MAX_NODES]; // Array to store node names as strings      
int S[MAX_NODES]={0};
jobjectArray vertices;
Point velocities[MAX_NODES];
int node_degrees[MAX_NODES];

_Atomic int num_edges = 0;
_Atomic int num_antiedges = 0;
double coeff_antiarete = 100; // Facteur de répulsion des antiarêtes
Edge antiedges[MAX_EDGES];  // Pour les anti-arêtes

int num_nodes = 0;
double Lx = 300, Ly = 300;

int iteration=0;

double friction = 0.1;
double attraction_coeff = 100;
double thresholdA = 1;
double seuilrep = 0;
double thresholdS = 1;

void toroidal_vector(Point *dir, Point p1, Point p2) {
    dir->x = p2.x - p1.x;
    dir->y = p2.y - p1.y;
    if (fabs(dir->x) > Lx / 2) dir->x -= copysign(Lx, dir->x);
    if (fabs(dir->y) > Ly / 2) dir->y -= copysign(Ly, dir->y);
}

// Calculer la distance toroïdale entre deux points
double toroidal_distance(Point p1, Point p2) {
    Point dir;
    toroidal_vector(&dir, p1, p2);
    return sqrt(dir.x * dir.x + dir.y * dir.y);
}

// Calculer les degrés de chaque noeud
void calculate_node_degrees(void) {
    for (int i = 0; i < num_nodes; i++) {
        node_degrees[i] = 0;
    }
    for (int i = 0; i < num_edges; i++) {
        node_degrees[edges[i].node1]++;
        node_degrees[edges[i].node2]++;
    }
}

// Générer un point aléatoire près du centre
void random_point_in_center(JNIEnv* env, int index) {
    double center_width = Lx * 0.3;
    double center_height = Ly * 0.3;
    double x = (rand() / (double)RAND_MAX) * center_width - center_width / 2;
    double y = (rand() / (double)RAND_MAX) * center_height - center_height / 2;

    setVertex(env, index, x, y);
}

void translate_positions(JNIEnv* env, double dx, double dy) {
    double half_Lx = Lx / 2.0;
    double half_Ly = Ly / 2.0;
    for (int i = 0; i < num_nodes; ++i) {
        double x = getVertex_x(env, i) + dx;
        double y = getVertex_y(env, i) + dy;
        // Appliquer les conditions aux limites toroïdales

        while ( x < -half_Lx) { x += Lx; }
        while ( x > half_Lx)  { x -= Lx; }
        while ( y < -half_Ly) { y += Ly; }
        while ( y > half_Ly)  { y -= Ly; }

        setNewVertex(env, i, x, y);
    }
}

// probablement privé utilisée dans update_positions
// Étape 1 : Forces d'attraction basées sur les arêtes
void repulsion_edges(JNIEnv* env, Point* forces)
{

    for (int edge_index = 0; edge_index < num_edges; edge_index++) {
        int node1 = edges[edge_index].node1;
        int node2 = edges[edge_index].node2;
    
        Point dir;
        toroidal_vector(&dir, getVertex(env, node1), getVertex(env, node2));
    
        double dist_squared = dir.x * dir.x + dir.y * dir.y;
        double att_force = attraction_coeff; //*dist_squared;
        
        if (dist_squared > thresholdA) {            
            forces[node1].x += dir.x * att_force;
            forces[node1].y += dir.y * att_force;
            forces[node2].x -= dir.x * att_force;
            forces[node2].y -= dir.y * att_force;
        }
    }

}

// probablement privé utilisé dans update_positions
// Étape 2 bis : Mettre à jour les positions en fonction des forces
void repulsion_anti_edges(JNIEnv* env, Point* forces)
{

    for (int edge_index = 0; edge_index < num_antiedges; edge_index++) {
        int node1 = antiedges[edge_index].node1;
        int node2 = antiedges[edge_index].node2;
    
        Point dir;
        toroidal_vector(&dir, getVertex(env, node1), getVertex(env, node2));
    
        double dist = sqrt(dir.x * dir.x + dir.y * dir.y);
        if (dist > seuilrep) {
            double rep_force = coeff_antiarete/(dist*dist);
            forces[node1].x -= (dir.x / dist) * rep_force;
            forces[node1].y -= (dir.y / dist) * rep_force;
            forces[node2].x += (dir.x / dist) * rep_force;
            forces[node2].y += (dir.y / dist) * rep_force;
        } else {
            double rep_force = coeff_antiarete/ seuilrep;
                                        
            forces[node1].x -= dir.x * rep_force;
            forces[node1].y -= dir.y * rep_force;
            forces[node2].x += dir.x * rep_force;
            forces[node2].y += dir.y * rep_force;
        }
    }

}

// probablement privé utilisé dans update_positions
// Étape 3 : Mettre à jour les positions des sommets du graphe en fonction des forces
double update_position_forces(JNIEnv* env, Point* forces, double PasMaxX, double PasMaxY, double Max_movement)
{
    double half_Lx = Lx / 2.0;
    double half_Ly = Ly / 2.0;

    double new_max_movement = 0.0;
    for (int i = 0; i < num_nodes; i++) {
        velocities[i].x = (velocities[i].x + forces[i].x) * friction;
        velocities[i].y = (velocities[i].y + forces[i].y) * friction;
        velocities[i].x = fmin(fmax(velocities[i].x, -PasMaxX), PasMaxX); // Capper la force en x à 1
        velocities[i].y = fmin(fmax(velocities[i].y, -PasMaxY), PasMaxY); // Capper la force en y à 1

        double x = getVertex_x(env, i) + velocities[i].x;
        double y = getVertex_y(env, i) + velocities[i].y;
        // Appliquer les conditions aux limites toroïdales

        while ( x < -half_Lx) { x += Lx; }
        while ( x > half_Lx)  { x -= Lx; }
        while ( y < -half_Ly) { y += Ly; }
        while ( y > half_Ly)  { y -= Ly; }

        setVertex(env, i, x, y);

        new_max_movement = fmax(Max_movement, velocities[i].x * velocities[i].x + velocities[i].y * velocities[i].y);
    }

    return new_max_movement;
}

void initialize_vertices(JNIEnv* env){
    jclass obj_class = (*env)->FindClass(env, "graph/Vertex");
    jmethodID point_constructor = (*env)->GetMethodID(env, obj_class, "<init>", "(DD)V");
    jobject initial_elem = (*env)->NewObject(env, obj_class, point_constructor, 0., 0.);

    jobjectArray localArray = (*env)->NewObjectArray(env, num_nodes, obj_class, initial_elem);

    vertices = (jobjectArray) (*env)->NewGlobalRef(env, localArray);

    (*env)->DeleteLocalRef(env, localArray);
}

// Uses Vertex constructor instead of the update method
void setNewVertex(JNIEnv* env, int index, double x, double y){
    jclass obj_class = (*env)->FindClass(env, "graph/Vertex");
    jmethodID point_constructor = (*env)->GetMethodID(env, obj_class, "<init>", "(DD)V");

    jobject point = (*env)->NewObject(env, obj_class, point_constructor, x, y);
    
    (*env)->SetObjectArrayElement(env, vertices, index, point);
    
    (*env)->DeleteLocalRef(env, point);
    
    printf("%lf %lf \t %lf %lf\n", getVertex_x(env, index), getVertex_y(env, index), x, y);

}

void setVertex(JNIEnv* env, int index, double x, double y){
    jclass obj_class = (*env)->FindClass(env, "graph/Vertex");
    jmethodID update_method = (*env)->GetMethodID(env, obj_class, "update", "(DD)V");
    jobject vertex = (*env)->GetObjectArrayElement(env, vertices, index);
    
    (*env)->CallVoidMethod(env, vertex, update_method, x, y);
    
    (*env)->DeleteLocalRef(env, vertex);

    printf("%lf %lf \t %lf %lf\n", getVertex_x(env, index), getVertex_y(env, index), x, y);
}

jdouble getVertex_x(JNIEnv* env, int index){
    jclass obj_class = (*env)->FindClass(env, "graph/Vertex");
    jmethodID getter = (*env)->GetMethodID(env, obj_class, "getX", "()D");
    jobject vertex = (*env)->GetObjectArrayElement(env, vertices, index);

    double res = (*env)->CallDoubleMethod(env, vertex, getter);

    (*env)->DeleteLocalRef(env, vertex);

    return res;
}

jdouble getVertex_y(JNIEnv* env, int index){
    jclass obj_class = (*env)->FindClass(env, "graph/Vertex");
    jmethodID getter = (*env)->GetMethodID(env, obj_class, "getY", "()D");
    jobject vertex = (*env)->GetObjectArrayElement(env, vertices, index);

    double res = (*env)->CallDoubleMethod(env, vertex, getter);

    (*env)->DeleteLocalRef(env, vertex);

    return res;
}

Point getVertex(JNIEnv* env, int index){
    Point res;
    res.x = getVertex_x(env, index);
    res.y = getVertex_y(env, index);
    //printf("GetVertex: %lf, %lf\n", res.x, res.y);
    return res;  
}