import matplotlib.pyplot as plt
import csv 
    
csvfile_saut1 = open("Kmeans_plus1.csv")
csvfile_saut10 = open("Kmeans_plus10.csv")
csvfile_10 = open("Kmeans_10.csv")
csvfile_1 = open("Kmeans_1.csv")

content_saut1 = csv.DictReader(csvfile_saut1, delimiter=',')
content_saut10 = csv.DictReader(csvfile_saut10, delimiter=',')
content_10 = csv.DictReader(csvfile_10, delimiter=',')
content_1 = csv.DictReader(csvfile_1, delimiter=',')
array_saut1 = []
array_saut10 = []
array_10 = []
array_1 = []

for row in content_saut1:
    array_saut1.append(int(row[" time_since_last_start"]))
        
for row in content_saut10:
    array_saut10.append(int(row[" time_since_last_start"]))

for row in content_10:
    array_10.append(int(row[" time_since_last_start"]))

for row in content_1:
    array_1.append(int(row[" time_since_last_start"]))

temps = [i for i in range(0, len(array_saut1))]
fig = plt.figure(figsize=(12, 6))
ax1 = fig.add_subplot(1, 2, 1)
ax2 = fig.add_subplot(1, 2, 2)

ax1.plot(temps, array_saut1, label="saut=1 plus")
ax2.plot(temps, array_saut10, label="saut=10 plus")
ax2.plot(temps, array_10, label="saut=10")
ax1.plot(temps, array_1, label="saut=1")

ax1.set_xlabel("numero d'appel")
ax1.set_ylabel("temps en millisecondes")
ax1.set_title("evolution du temps de calcul de l'algorithme kmeans")
ax1.set_yscale("log")
ax1.legend()

ax2.set_xlabel("numero d'appel")
ax2.set_ylabel("temps en millisecondes")
ax2.set_title("evolution du temps de calcul de l'algorithme kmeans")
ax2.set_yscale("log")
ax2.legend()

fig.savefig("kmeans_graph")
csvfile_saut1.close()
csvfile_saut10.close()
csvfile_10.close()
csvfile_1.close()

