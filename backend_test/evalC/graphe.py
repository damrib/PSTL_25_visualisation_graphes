import numpy as np
import matplotlib.pyplot as plt

fig = plt.figure(figsize=(12, 12))

ax1 = fig.add_subplot(1, 2, 1)
ax2 = fig.add_subplot(1, 2, 2)

nb_threads = [1, 2, 4, 8, 16, 18]

calculate_similitude_and_edges = [47422, 24877, 15706, 11687, 9695, 8585]
calculate_mean_similitude = [56263, 43504, 26734, 17957, 10054, 8861]

ax1.plot(nb_threads, calculate_mean_similitude, label="derniere version")
ax2.plot(nb_threads, calculate_similitude_and_edges, label="derniere version", color="b")

ax2.axline((0, 37161), (20, 37161), label="version originale", color="r")

ax1.legend()
ax2.legend()

ax1.set_title("calculate_mean_similitude")
ax2.set_title("calculate_similitude_and_edges")

ax1.set_ylabel("temps en millisecondes")
ax1.set_xlabel("nombre de thread")

ax2.set_ylabel("temps en millisecondes")
ax2.set_xlabel("nombre de thread")

fig.savefig("similitude")