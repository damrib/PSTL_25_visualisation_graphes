import matplotlib.pyplot as plt
import csv 

def mean_content(filename, nb_file, nb_line):
    array = [0 for i in range(0, nb_line)]
    for i in range(1, nb_file + 1):
        csvfile = open(filename + str(i) + ".csv")
        content = csv.DictReader(csvfile, delimiter=',')

        for j, row in enumerate(content):
            if ( j > 0 ):
                array[j - 1] += int(row[" time_since_last_start"])

        csvfile.close()
    result = [array[i] / nb_file for i in range(len(array))]
    return result

array_original = mean_content("Kmeans_original1", 5, 100)
array_parallel = mean_content("Kmeans_parallel1", 5, 100)
array_saut10   = mean_content("Kmeans_saut10", 5, 100)
array_saut5    = mean_content("Kmeans_saut5", 5, 100)
array_plus     = mean_content("Kmeans_plus", 5, 100)
array_plus05   = mean_content("Kmeans_plus05", 5, 100)
array_parallel05 = mean_content("Kmeans_parallel05", 5, 100) 

fig = plt.figure(figsize=(6, 6))
ax = fig.add_subplot(1, 1, 1)

temps_100 = [i for i in range(0, len(array_original))]

ax.plot(temps_100, array_original, label="version originale")
ax.plot(temps_100, array_parallel, label="version optimisée")

ax.set_xlabel("numero d'appel")
ax.set_ylabel("temps en millisecondes")
ax.set_title("evolution du temps de calcul des clusters")
ax.set_yscale("log")
ax.legend()

fig.savefig("kmeans_different_version")

fig  = plt.figure(figsize=(6, 6))
ax  = fig.add_subplot(1, 1, 1)

ax.plot(temps_100, array_saut10, label="calcule 1/10 iteration", color="blue")
ax.plot(temps_100, array_saut5, label="calcule 1/5 iteration", color="black")
ax.plot(temps_100, array_parallel, label="calcule chaque iteration", color="red")

ax.set_xlabel("numero d'appel")
ax.set_ylabel("temps en millisecondes")
ax.set_title("evolution du temps de calcul des clusters avec délai")
ax.set_yscale("log")
ax.legend()

fig.savefig("kmeans_different_delays")

fig  = plt.figure(figsize=(12, 6))
ax1  = fig.add_subplot(1, 2, 1)
ax2  = fig.add_subplot(1, 2, 2) 

ax2.plot(temps_100, array_plus, label="kmeans++ initialization centre", color="blue")
ax2.plot(temps_100, array_plus05, label="kmeans++ initialization", color="red")
ax1.plot(temps_100, array_parallel, label="kmeans initialization centre", color="blue")
ax1.plot(temps_100, array_parallel05, label="kmeans initialization", color="red")

ax1.set_xlabel("numero d'appel")
ax1.set_ylabel("temps en millisecondes")
ax1.set_title("evolution du temps de calcul des clusters")
ax1.set_yscale("log")
ax1.legend()

ax2.set_xlabel("numero d'appel")
ax2.set_ylabel("temps en millisecondes")
ax2.set_title("evolution du temps de calcul des clusters")
ax2.set_yscale("log")
ax2.legend()

fig.savefig("kmeans_different_initialization")