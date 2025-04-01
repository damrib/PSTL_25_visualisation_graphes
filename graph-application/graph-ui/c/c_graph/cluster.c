#include "cluster.h"
#include "../global.h"

int communities[MAX_NODES]; // Stocke les communautés détectées par Louvain

int clusters[MAX_NODES];
float cluster_colors[MAX_NODES][3];
double centers[MAX_NODES][2];
Cluster *cluster_nodes = NULL;  // Tableau de clusters
int n_clusters;

double epsilon = 0.1;
int espacement = 1;

// modifiable par utilisateur
double repulsion_coeff = 1;
int saut = 10;
int mode = 0;

double squared_distance(double* center, Point p) {
    double dx = p.x - center[0];
    double dy = p.y - center[1]; 
    return dx * dx + dy * dy;
}

struct map_arguments {
    int num_clusters;
    double Lx, Ly;
    double ** new_centers;
    int * counts, * labels;
    int vertex_index;

    pthread_mutex_t *lock;
    Barrier barrier;
};

void kmeans_map(void * arg) {

    struct map_arguments * arguments = (struct map_arguments*) arg;
    double min_dist = DBL_MAX;
    int best_cluster = 0;

    for ( int i = 0; i < arguments->num_clusters; ++i ) {
        double dist = squared_distance(centers[i], vertices[arguments->vertex_index]);

        if (dist < min_dist) {
            min_dist = dist;
            best_cluster = i;
        }

    }

    double adjusted_x = vertices[arguments->vertex_index].x;
    double adjusted_y = vertices[arguments->vertex_index].y;

    while (adjusted_x - centers[best_cluster][0] > arguments->Lx / 2) adjusted_x -= arguments->Lx;
    while (centers[best_cluster][0] - adjusted_x > arguments->Lx / 2) adjusted_x += arguments->Lx;
    while (adjusted_y - centers[best_cluster][1] > arguments->Ly / 2) adjusted_y -= arguments->Ly;
    while (centers[best_cluster][1] - adjusted_y > arguments->Ly / 2) adjusted_y += arguments->Ly;

    pthread_mutex_lock(arguments->lock);
    arguments->new_centers[best_cluster][0] += adjusted_x;
    arguments->new_centers[best_cluster][1] += adjusted_y;
    arguments->counts[best_cluster]++;
    pthread_mutex_unlock(arguments->lock);

    arguments->labels[arguments->vertex_index] = best_cluster;

    decrement_barrier(arguments->barrier, 1);
}

// Assigner des noeuds aux clusters et mettre à jour les centres en utilisant l'algorithme k-means
void kmeans_iteration(int num_points, int num_clusters, int *labels, double centers[][2], double Lx, double Ly, double* max_diff) {

    // Modification pour utiliser moins de memoire et eviter un seg fault
    int counts[MAX_NODES] = {0};
    double ** new_centers = (double**) malloc(sizeof(double*) * num_clusters);  // Stocker les nouveaux centres calculés

    for (int i = 0; i < num_clusters; ++i) {
        new_centers[i] = (double*) calloc(2, sizeof(double));
    }

    struct barrier bar;
    new_barrier(&bar, num_points);

    pthread_mutex_t lck;
    pthread_mutex_init(&lck, NULL);

    // Assigner chaque point au cluster le plus proche et mettre à jour les centres
    for (int i = 0; i < num_points; i++) {
        struct map_arguments * arguments = (struct map_arguments*) malloc(sizeof(struct map_arguments));
        arguments->Lx = Lx;
        arguments->Ly = Ly;
        arguments->vertex_index = i;
        arguments->counts = counts;
        arguments->new_centers = new_centers;
        arguments->labels = labels;
        arguments->num_clusters = num_clusters;
        arguments->barrier = &bar;
        arguments->lock = &lck;

        struct Job task;
        task.j = kmeans_map;
        task.args = arguments;
        submit(&pool, task);

    }

    wait_barrier(&bar);   
    pthread_mutex_destroy(&lck);

    // Mise à jour des centres de clusters en fonction des nouvelles assignations
    for (int i = 0; i < num_clusters; i++) {
        if (counts[i] > 0) {
            Point old_center;
            old_center.x = centers[i][0];
            old_center.y = centers[i][1];

            centers[i][0] = new_centers[i][0] / counts[i];
            centers[i][1] = new_centers[i][1] / counts[i];

            double dist = squared_distance(centers[i], old_center);
            *max_diff = *max_diff < dist? dist : *max_diff;

            // Ramener les centres dans l'espace torique
            while ( centers[i][0] < -Lx / 2) centers[i][0] += Lx;
            while ( centers[i][0] > Lx / 2 ) centers[i][0] -= Lx;
            while ( centers[i][1] < -Ly / 2) centers[i][1] += Ly;
            while ( centers[i][1] > Ly / 2 ) centers[i][1] -= Ly;
        }

        free(new_centers[i]);
    }

    free(new_centers);
}

// probablement privé utilisée dans update_positions
// Étape 2 : Forces de répulsion intra-cluster
void repulsion_intra_clusters(Point* forces, double FMaxX, double FMaxY)
{

    for (int cluster = 0; cluster < n_clusters; cluster++) {
        int size = cluster_nodes[cluster].size;
        for (int i = 0; i < size; i++) {
            int node_i = cluster_nodes[cluster].nodes[i];
    
            Point pi = vertices[i];
            for (int j = i + 1; j < size; j++) {
                int node_j = cluster_nodes[cluster].nodes[j];
                Point dir;
                toroidal_vector(&dir, pi, vertices[i]);
    
                double dist_squared = dir.x * dir.x + dir.y * dir.y;
                if (dist_squared > seuilrep) { // Assume a minimum distance to avoid division by zero
                    //double dist = sqrt(dist_squared);
                    
                    double rep_force;
                    if ( mode == 1 ) { 
                        rep_force = repulsion_coeff/(dist_squared*dist_squared);
                    } else if (mode == 2 && communities[i] != communities[j]) {//printf("extra repulsion %d, %d \n",i,j);
                        rep_force = 100000*repulsion_coeff*(node_degrees[i]+1)*(node_degrees[j]+1) / dist_squared;
                    } else { // mode == 0 est le mode par defaut
                            rep_force = repulsion_coeff*(node_degrees[i]+1)*(node_degrees[j]+1) / dist_squared;
                    } 
                                        
                    forces[node_i].x -= dir.x * rep_force;
                    forces[node_i].y -= dir.y * rep_force;
                    forces[node_j].x += dir.x * rep_force;
                    forces[node_j].y += dir.y * rep_force;
                } else {
                    double rep_force = repulsion_coeff / seuilrep;
                                        
                    forces[node_i].x -= dir.x * rep_force;
                    forces[node_i].y -= dir.y * rep_force;
                    forces[node_j].x += dir.x * rep_force;
                    forces[node_j].y += dir.y * rep_force;
                }
            }
                // capper les forces d'attractions
        forces[node_i].x = fmax(fmin(forces[node_i].x, FMaxX), -FMaxX);
        forces[node_i].y = fmax(fmin(forces[node_i].y, FMaxY), -FMaxY);
    
        }
    }


}

// etape 4 dans update_positions
void update_clusters()
{

    if (iteration % (saut * (1 + 0 * espacement)) == 0) {
        int centers_converged = 0;

        while (centers_converged == 0) {
            // the biggest difference between the former centers and the new ones
            double max_movement = 0;

            kmeans_iteration(num_nodes, n_clusters, clusters, centers, Lx, Ly, &max_movement);

            centers_converged = max_movement <= epsilon;
        }
        clear_clusters();

        for (int i = 0; i < num_nodes; i++) {
            int best_cluster = clusters[i];
            add_node_to_cluster(best_cluster, i);
        }
    }

}

void clear_clusters() {
    for (int i = 0; i < n_clusters; i++) {
        cluster_nodes[i].size = 0;
    }
}

void reinitialize_clusters(int new_num_clusters) {
    if (new_num_clusters != n_clusters) {
        free_clusters();
        init_clusters(new_num_clusters);
    } else {
        clear_clusters();
    }
    initialize_centers();
    assign_cluster_colors();
    n_clusters = new_num_clusters;
}

void free_clusters() {
    if (cluster_nodes != NULL) {
        for (int i = 0; i < n_clusters; i++) {
            if (cluster_nodes[i].nodes != NULL) {
                free(cluster_nodes[i].nodes);
                cluster_nodes[i].nodes = NULL;
            }
        }
        free(cluster_nodes);
        cluster_nodes = NULL;
    }
}

// Générer un point aléatoire dans le plan
void random_point_in_plane(Point *p) {
    p->x = (rand() / (double)RAND_MAX) * Lx - Lx / 2;
    p->y = (rand() / (double)RAND_MAX) * Ly - Ly / 2;
}

void initialize_centers(){
    for (int i = 0; i < n_clusters; i++) {
        random_point_in_plane((Point *)centers[i]);
    }
}

// Assigner des couleurs aux clusters
void assign_cluster_colors() {
    for (int i = 0; i < n_clusters; i++) {
        cluster_colors[i][0] = (float)rand() / RAND_MAX;
        cluster_colors[i][1] = (float)rand() / RAND_MAX;
        cluster_colors[i][2] = (float)rand() / RAND_MAX;
    }
}

void init_clusters(int num_clusters) {
    int estimated_capacity = (num_nodes / num_clusters) + 1;
    cluster_nodes = (Cluster *)malloc(num_clusters * sizeof(Cluster));
    for (int i = 0; i < num_clusters; i++) {
        cluster_nodes[i].nodes = (int *)malloc(estimated_capacity * sizeof(int));
        cluster_nodes[i].size = 0;
        cluster_nodes[i].capacity = estimated_capacity;
    }
}

void add_node_to_cluster(int cluster_id, int node) {
    Cluster *cluster = &cluster_nodes[cluster_id];
    if (cluster->size == cluster->capacity) {
        cluster->capacity *= 2;
        cluster->nodes = (int *)realloc(cluster->nodes, cluster->capacity * sizeof(int));
    }
    cluster->nodes[cluster->size++] = node;
}