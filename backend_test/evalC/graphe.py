import numpy as np
import matplotlib.pyplot as plt

fig = plt.figure(figsize=(6, 6))

ax2 = fig.add_subplot(1, 1, 1)

nb_threads = [1, 2, 4, 6, 8, 16]

calculate_mean_similitude = [34228, 26590, 18643, 13243, 12051, 8837]

ax2.plot(nb_threads, calculate_mean_similitude, label="derniere version", color="b")
ax2.axline((0, 37161), (20, 37161), label="version originale", color="r")

ax2.legend()

ax2.set_title("calculate_similitude_and_edges")

ax2.set_ylabel("temps en millisecondes")
ax2.set_xlabel("nombre de thread")

fig.savefig("similitude")