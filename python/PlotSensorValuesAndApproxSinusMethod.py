import numpy as np
import numpy as numpy
import pandas as pd
import matplotlib.pyplot as plt

# Daten aus der CSV-Datei laden
data = pd.read_csv('../resources/generic_sinus.csv').iloc[:200,0].astype(float).values

# sinus_generator_values = [numpy.sin(i/10) for i in range(500)]
# df = pd.DataFrame(sinus_generator_values, columns=["colummn"])
# df.to_csv('../resources/generic_sinus.csv', index=False)

# Diagramm erstellen
A,f,phi,D,_ = pd.read_csv('../DE_with_csv').iloc[-1].astype(float).values
t = np.arange(0,200,1)
sin = A*np.sin(2*np.pi*f*t+phi) + D
plt.plot(range(0,data.size), list(data))
plt.plot(t,sin)
plt.show()

# plt.xlabel('X-Achse')
# plt.ylabel('Y-Achse')
# plt.title('HCA Optimiertere Datenpunkte in Reihenfolge')
# for i, row in data.iterrows():
#     plt.text(row[0], row[1], str(i), fontsize=12, color='black')
#
# max_x_value = data.iloc[:, 0].abs().max() + 0.05
# max_y_value = data.iloc[:, 1].abs().max() + 0.05
# ax.set_xlim([-max_x_value, max_x_value])
# ax.set_ylim([-max_y_value, max_y_value])
# plt.show()
