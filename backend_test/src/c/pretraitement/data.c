#include "data.h"
#include "../global.h"

int nbValeurs;
double **data = NULL;
int num_rows = 0, num_columns = 0;
char delimiter[1] = "\0";
int S[MAX_NODES]={0};
char **node_names = NULL;

short str_is_number(char* line)
{
    if ( *line == '-' ) {
        ++line;
    } else if ( *line == '\0' ) {
        return 0;
    }

    short pt = 0;
    while ( (*line >= 48 && *line <= 57) || (*line == 46 && ! pt) || *line == 101 ) {
        if (*line == 46)
            pt = 1;
        else if (*line == 101)
            return str_is_number(++line);
        ++line;
    }

    return *line == '\0';
}

// chargement Data
void load_csv_data(const char *filename) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        printf("Could not open file %s\n", filename);
        exit(1);
    }

    char *line = NULL;
    size_t len = 0;

    // Première passe : Compter les lignes et les colonnes
    while (getline(&line, &len, file) != -1) {
        // Supprimer les retours à la ligne potentiels
        line[strcspn(line, "\r\n")] = '\0';
        num_rows++;

        // Compter les colonnes uniquement à la première ligne
        if (num_columns == 0) {
            int count = 0;  // Commence à 1 car il y a toujours une colonne avant la première virgule
            for (int i = 0; line[i] != '\0'; i++) {
                if (delimiter[0] == '\0' || line[i] == ',' || line[i] == ';' || line[i] == '|' || line[i] == ' ' || line[i] == '\t') {
                    delimiter[0] = line[i];
                    count++;
                } else if (line[i] == delimiter[0]) {
                    count++;
                }
            }

            num_columns = count;
        }
    }

    // Allouer de la mémoire pour les données
    data = (double **)malloc((num_rows-1) * sizeof(double *));
    for (int i = 0; i < num_rows-1; i++) {
        data[i] = (double *)malloc(num_columns * sizeof(double));
    }

    // Deuxième passe : Lire les données
    rewind(file);
    int row = 0;
    while (getline(&line, &len, file) != -1) {
        line[strcspn(line, "\r\n")] = '\0';  // Supprimer les retours à la ligne potentiels

        int col = 0;
        char *start = line;
        char *end = NULL;

        // Parcourir chaque valeur séparée par des virgules
        while ((end = strchr(start, delimiter[0])) != NULL || *start != '\0') {
            if (end) {
                *end = '\0';  // Terminer la chaîne courante à la virgule
            }
            int is_number = str_is_number(start);
            // Convertir la valeur en double
            if ( row > 0 && ! is_number ){
                printf("Warning %s: Missing value on row %d, col %d\n", start, row, col);
            }
            if ( row > 0 && is_number ) {
                data[row-1][col] = atof(start);
            } else if ( row > 0 ) {
                data[row-1][col] = 0.;
            }
            col++;

            // Si end est NULL, on est à la dernière valeur
            if (!end) break;

            start = end + 1;  // Passer à la prochaine valeur
        }

        // Si le nombre de colonnes lues est différent de num_columns, avertir
        if (col != num_columns) {
            //printf("Warning: Row %d has %d columns (expected %d).\n", row, col, num_columns);
        }

        row++;
    }
    
    fclose(file);
    free(line);  // Libérer la mémoire allouée par getline

    printf("Loaded CSV with %d rows and %d columns.\n", num_rows, num_columns);
    
        // Afficher les trois premières lignes
    printf("Les 3 premières lignes des données :\n");
    for (int i = 0; i < 5 && i < num_rows; i++) {
        for (int j = 0; j < num_columns; j++) {
            printf("%f ", data[i][j]);
        }
        printf("\n");
    }

    // On ne garde pas la première ligne dans les données
    num_rows = num_rows - 1;
}


// Fonction pour lire les valeurs de la première colonne d'un fichier CSV S[MAX_NODES]
void lireColonneCSV(int *S,int *nbValeurs) {
    char chemin[256];
    printf("Veuillez entrer le chemin du fichier CSV: ");
    // Use scanf with a width specifier to avoid buffer overflow
    scanf("%255s", chemin);  // Limiting the input to 255 characters
    FILE *fichier = fopen(chemin, "r");
    if (fichier == NULL) {
        perror("Erreur lors de l'ouverture du fichier");
        return;
    }

    char ligne[MAX_LINE_LENGTH];
    *nbValeurs = 0; // Initialisation du nombre de valeurs

    // Lecture de chaque ligne du fichier
    while (fgets(ligne, sizeof(ligne), fichier) != NULL) {
        // Séparer la première colonne en utilisant le point-virgule comme délimiteur
        char *token = strtok(ligne, delimiter);
        if ( token != NULL && str_is_number(token) ) {
            // Vérifier si le tableau S n'est pas plein
            if (*nbValeurs < MAX_NODES) {
                // Convertir la chaîne en entier et stocker dans le tableau S
                if (str_is_number(token)) {
                    S[*nbValeurs] = atoi(token);
                } else {
                    S[*nbValeurs] = 0;
                }
                //printf("S[%d]=%d ",*nbValeurs,S[*nbValeurs]);
                (*nbValeurs)++;
            } else {
                printf("Nombre maximum de valeurs atteint.\n");
                break;
            }
        } else {
            
            printf("Missing values in csv file");
        }
    }
    // Afficher les trois premières lignes
    printf("Les 5 premières lignes des données :\n");
    for (int i = 0; i < 5 && i < num_rows; i++) {
            printf("%d \n", S[i]);
    }
    fclose(fichier);
}

/**
 * We use this to store the name and the index of the node
 * in the vertices array
 */
struct HashPair {
    int index;
    char* label;
}


void split_str(char * line, size_t size, char* delimiters, int nb_delimiter) {
    
    for (int i = 0; i < size; ++i) {

        for (int j = 0; j < nb_delimiter; ++j) {
            if ( *line == delimiters[j] ) {
                *line = '\0';
            }
        }
        ++line;
    }

}

char* next_str(char** line, size_t* s) {

    while ( **line == '\0' ) { 
        ++*line; 
        --*s;
    }

    char * start = *line;
    int cpt = 0;
    while ( **line != '\0' && *s > 0) {
        ++cpt;
        ++*line;
        --*s;
    }

    char* res = NULL;
    if ( cpt != 0 ) {

        res = (char*) malloc(sizeof(char) * (cpt + 1));
        for (int i = 0; i < cpt; ++i) {
            res[i] = start[i];
        }
        res[cpt] = '\0';
    }

    return res;
}

void find_parameters
    (char** line, size_t* size, double* weight, char** node_name) 
{

    while (*size > 0) {
        char* next = next_str(line, size);

        if ( weight != NULL && *size > 0 && strcmp(next, "weight") == 0 ) {
            char* next = next_str(line, size);
            if ( next != NULL ) {
                sscanf(next, "%lf", weight);
                free(next);
            }
        } else if ( node_name != NULL && *size > 0 && strcmp(next, "label") == 0 ) {
            char* next = next_str(line, size);
            if ( next != NULL ) {
                *node_name = next;
            }
        }
    }
    
}

int hash_string(char* label) {

    int cpt = 0;
    while ( *label != '\0' ) {
        cpt += (int) *label;
        ++label;
    }

    return cpt;
}

int map_put(HashPair** map, int* capacity, char* key) {
    
    if ( *capacity <= num_nodes ) {
        int new_capacity = 2 * *capacity;
        HashPair* new_map = (HashPair*) malloc(sizeof(HashPair) * new_capacity);
        for (int i = 0; i < *capacity; ++i) {
            int new_index = hash_string((*map)[i].label) % new_capacity;
            new_map[new_index].label = (*map)[i].label;
            new_map[new_index].index = (*map)[i].index;
        }
        free(*map);
        *map = new_map;
        *capacity = new_capacity;
    }

    int ind = hash_string(key) % *capacity;

    while( (*map)[ind].index != -1 ) {
        if ( strcmp(key, map[ind].label) == 0 ) {
            free((*map)[ind].label);
        } else {
            ind = (ind + 1) % *capacity;
        }
    }

    (*map)[ind].index = num_nodes++;
    (*map)[ind].label = key;

    return (*map)[ind].index;
}

void parse_node_stmt(char * ptr, size_t size, HashPair* map, char* delimiters) {
    char delimiters[5] = {' ', '[', ']', '=', ';'}; 
    // we replace spaces by null
    split_str(ptr, size, delimiters, 1);

    char * label = next_str(&ptr, &size);
    if ( label == NULL || label[0] == '[' ) {
        free(label);
        return;
    }

    char * next = next_str(&ptr, &size);
    while ( next != NULL && next[0] != '[' ) {
        free(next);
        next = next_str(&line, &size);
    }

    int index = map_put(map, label);

    if ( next != NULL ) {
        split_str(ptr, size, delimiters, 5);
        if ( strcmp(next + 1, "label") == 0 ) {
            char* node_name = next_str(&line, &size);
            if ( node_name != NULL ) {
                node_names[index] = node_name;
            }
        } else {
            find_parameters(&ptr, &size, NULL, node_names + index);
        }
        free(next);
    }

}

void parse_edge_stmt(char* ptr, size_t size, HashPair* map, char* delimiters) {
    char delimiters[5] = {' ', '[', ']', '=', ';'};
    split_str(ptr, size, delimiters, 5);
            
    // reading the first node label
    char* next = next_str(&ptr, &size);
    if ( next != NULL ) {
        node1 = map_put(map, next);
    }
            
    // we can ignore the next as it should be either "->" or "--"
    next = next_str(&ptr, &size);
    free(next); 

    next = next_str(&ptr, &size);
    if ( next != NULL ) {
        node2 = map_put(map, next);
    }

    find_parameters(&ptr, &size, &weight, NULL);

    if ( num_edges < MAX_EDGES ) {
        edges[num_edges].node1 = node1;
        edges[num_edges].node2 = node2;
        edges[num_edges].weight = weight;

        ++num_edges;
    } else {
        fprintf(stderr, "Warning: Number of edges exceeds MAX_EDGES\n");
        return ;
    }
}

void parse_dot_line(char * ptr, size_t size, HashPair* map) {

    int node1 = -1, node2;
    double weight = 1.0;

    if ( strstr(ptr, "->") || strstr(ptr, "--") ) {
        parse_edge_stmt(ptr, size, map);
    } else if ( strstr(ptr, "{") ) {
        
    } else if ( ! strstr(ptr, "}") ) {
        parse_node_stmt(ptr, size, map);
    }

}

void parse_dot_file(const char *filename) {
    FILE *file = fopen(filename, "r");
    
    if (file == NULL) {
        perror("Error opening file");
        exit(1);
    }

    // num_rows will be greater than num_nodes
    num_rows = 0;
    for ( char c = fgetc(file); c != EOF; c = fgetc ) {
        if ( c == '\n' ) {
            ++num_rows;
        }
    }

    HashPair* map = (HashPair*) malloc(sizeof(HashPair) * num_rows) 

    node_names = (char**) malloc(sizeof(char*) * num_rows);
    for (int i = 0; i < num_rows; ++i){
        node_names[i] = NULL;
        map[i].index = -1;
    }

    char* line = NULL;
    size_t size = 0;

    rewind(file);

    getline(&line, &size, file);
    // skip first line for now
    free(line);
    line = NULL;

    while ( getline(&line, &size, file) != -1 ) {
        parse_dot_line(line, size, map);
        free(line);
        line = NULL;
    }

    free(map);
    fflush(NULL);

    fclose(file);
}

int isKeyword(char* str) {
    const char* keywords[] = {
        "digraph", "strict", "graph", "subgraph",
        "node", "edge"
    };
    int nb_keywords = 6;
    return belongs(str, keywords, size)
}

int belongs(char* str, const char** keywords, int size) {
    for (int i = 0; i < nb_keywords; ++i) {
        if ( strcmp(str, keywords[i]) == 0 ) {
            return 1;
        }
    }
    return 0;
}

int isCompass(char* str) {
    const char* compass[] = {
        "n", "ne", "nw", "s", "se", "sw", "e", "w", "c", "_"
    }
    size_t nb_compass = 10;
    return belongs(str, compass, nb_compass);
}

int hasEdgeOperator(char c1, char c2) {
    return c1 == '-' && (c2 == '>' || c2 == '-');
}

int isDelimiter(char c) {
    const char delimiters[] = {
        ' ', '[', ']', '{', '}', '=', ';', ',', ':', '"', '\n', '\r'
    }   
    size_t nb_delimiter = 12;
    for (int i = 0; i < nb_delimiter; ++i) {
        if ( delimiters[i] == c ) {
            return 1;
        }
    }
    return 0;
}

int isHtmlString(const char *s, size_t size) {
    return size >= 2 && s[0] == '<' && s[size-1] == '>';
}

int isQuoteString(const char* s, size_t size) {

    if ( size < 2 && s[0] != '"' && s[size-1] != '"' ) {
        return 0;
    }

    for (int i = 0; i < size - 1; ++i) {
        if ( s[i] == '\\' && s[i+1] == '"' ) {
            ++i;
        } else if ( s[i] == '"' ) {
            return 0;
        }
    }
    return 1;
}

int isAlpha(const char* s, size_t size) {
    if ( s[0] != '_' && ! isdigit(s[0]) )
        return 0;

    for (int i = 0; i < size; ++i) {
        if ( ! isalnum(s[i]) && (200 > s[i] || s[i] > 255) && s[i] != '_' )
            return 0;
    }
    return 1;
}

/**
 * checks if s respects the Identifier format of dot files
 * s should be null terminated
 */
int isId(const char *s, size_t size) {
    return 
    isHtmlString(s, size) 
    || str_is_number(s)
    || isQuoteString(s, size)
    || isAlpha(s, size);
}

int isParameter(const char* s) {
    const char* parameters[] = {
        "weight", "label"
    }
    int nb_parameters = 2;
    return belongs(s, parameters, nb_parameters);
}

int next_token(FILE* file, char * buffer, size_t* size, int inQuotes) {

    int c;

    while ( (c = fgetc(file)) != EOF  ) {
        if ( (inQuotes && (c != '"' || *size > 0 && buffer[*size-1] != '\\') ) 
        || ! isDelimiter(c) ) {
            buffer[*size] = c;
            ++*size;
        } else if ( *size > 0 ) {
            break;
        }
    }

    buffer[*size] = '\0';

    return c;
}

struct vect {
    int * content;
    int capacity, size;
};
typedef struct vect* Vect;



void add_elem_list(
    Vect nodes,
    int elem)
{
    if ( nodes->size >= nodes->capacity ) {
        nodes->content = (int*) realloc(nodes->content, nodes->capacity * sizeof(int) * 2);
        *nodes->capacity *= 2;
    } 
    (nodes->content)[nodes->size] = elem;
    ++nodes->size;
}

int add_new_edge(
    Vect nodes,
    Vect tmp) 
{
    int latest_node = nodes->content[nodes->size-1];
    if ( tmp ) {

        for (int i = 0; i < tmp->size; ++i) {
            if ( num_edges >= MAX_EDGES ) return -1

            edges[num_edges].node1 = tmp->content[i];
            edges[num_edges].node2 = latest_node;
            ++num_edges;

            add_elem_list(nodes, tmp->content[i]);
        }
    } else if ( num_edges < MAX_EDGES ){
        edges[num_edges].node1 = (nodes->content)[nodes->size-2];
        edges[num_edges].node2 = (nodes->content)[nodes->size-1];
        num_edges = num_edges + 1;
        return 0;
    } else {
        return -1;
    }
}

struct vect newVect(int capacity) {
    struct vect res;
    res.capacity = capacity;
    res.content = (int*) malloc(sizeof(int) * capacity);
    res.size = 0;
    return res;
} 

void freeVect(Vect v) {

    if ( v != NULL ) {
        if ( v->content != NULL ) {
            free(v->content);
        }
        free(v);
    }

}

Vect parse_stmt_list(
    FILE *file, 
    char* buffer, 
    HashPair** map, 
    size_t* capacity) {

    size_t size = 0;
    int c = next_token(file, buffer, &size, 0);
    int flag = 1;
    int side_edge = 0;
    int inQuote = 0;

    struct vect node_list1 = newVect(4);
    Vect node_list_tmp1 = NULL;
    Vect node_list_tmp2 = NULL;

    int last_edge = num_edges;

    int edge_overflow = 0;

    while ( flag ) {

        if ( size > 0 && ! isKeyword(buffer) && isId(buffer, size) && ! isCompass(buffer) ) {
            int index = map_put(map, capacity, buffer);
            add_elem_list(&node_list1, index);

            if ( side_edge ) {
                last_edge = num_edges;
                edge_overflow |= add_new_edge(&node_list1, node_list_tmp1);
                freeVect(node_list_tmp1);
                node_list_tmp1 = NULL;
            }

            side_edge = 0;
        }

        switch (c)
        {
        case '}':
            flag = 0;
            break;
        case '{':
            node_list_tmp2 = parse_stmt_list(file, buffer, map, capacity);
            if ( side_edge ) {
            
                if ( node_list_tmp1 ) {
                    last_edge = num_edges; 
                    for (int i = 0; i < node_list_tmp1; ++i) {
                        for (int j = 0; j < node_list_tmp2; ++j) {
                            edges[num_edges].node1 = node_list_tmp1->content[i];
                            edges[num_edges].node2 = node_list_tmp2->content[j];
                            ++num_edges;
                        }
                    }

                } else {
                    for (int i = 0; i < node_list_tmp2; ++i) {

                    }
                }

            } else {

            }
            freeVect(node_list_tmp1);
            node_list_tmp1 = NULL;
        case '-':
            size = 0;
            c = next_token(file, buffer, &size, 0);
            if ( c == '>' || c == '-' ) {
                side_edge = 1;
            }
            break;
        case '"':
            inQuote = inQuote ^ 1;
            break;
        case '[':
            
            break;
        default:
            break;
        }

        c = next_token(file, buffer, inQuote);
    }

    Vect res = (Vect) malloc(sizeof(struct vector));
    res->content = node_list1.content;
    res->capacity = node

    return node_list1;
}

#define MAXBUF 1024

void parse_graph(const char filename) {
    FILE *file = fopen(filename, "r");
    
    if (file == NULL) {
        perror("Error opening file");
    }

    char buf[MAXBUF];
    size_t size = 0;

    int c = next_token(file, buf, &size, 0);
    while ( c != EOF || c != '{' ) {
        size = 0;
        c = next_token(file, buf, &size, 0);
    }

    switch(c) {
        case '{':
            parse_stmt_list(file, buf);
            break;
        default:
            break;    
    }
    
    fclose(file);
}

void freeNodeNames() {
    if ( node_names != NULL ) {

        for (int i = 0; i < num_rows; ++i) {
            if ( node_names[i] != NULL ) {
                free(node_names[i]);
            }
        }
        free(node_names);
    }
}