import os
from collections import defaultdict
from typing import Dict

import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from pandas import DataFrame
from scipy.stats import zscore


def read_values_from_csv(subdir: str) -> Dict[str, DataFrame]:
    directory_path = f'python/TestRuns/{subdir}/'  # replace with the path to your directory
    csv_files = (f for f in os.listdir(directory_path) if f.endswith('.csv'))
    files = {files: pd.read_csv(directory_path + files) for files in csv_files}
    return files


def main():
    plot_test_runs_that_succeeded()
    plot_trend_of_generations_of_genetic_algorithm_based_on_changing_elitism_value()


def plot_test_runs_that_succeeded():
    genetic_values = read_values_from_csv("genetic")  # Type: Dict[str, DataFrame]
    hill_values = read_values_from_csv("hill")  # Type: Dict[str, DataFrame]
    # concat genetic_values and hill_values
    genetic_values.update(hill_values)
    genetic_plot_values = {key: len(df) for key, df in zip(genetic_values.keys(), genetic_values.values()) if
                           len(df) < 9999}
    # genetic_plot_values the values and keys of the dict in two lists
    column_description = [desc.replace("-", "-\n") for desc in list(genetic_plot_values.keys())]
    fig = plt.figure(figsize=(10, 6))
    plt.subplots_adjust(bottom=0.2)
    plt.bar(column_description, list(genetic_plot_values.values()), width=0.9, alpha=0.7)
    plt.title('Anzahl der Generationen mit verschiedenen Einstellungen und Algorithmen')
    plt.xlabel('Durchlauf')
    plt.ylabel('Anzahl der Generationen')
    plt.annotate("Algorithm-\nFitness-Function-\nMutation-Prob-\nElitism", xy=(0, 0), xytext=(-0.5, 4200))
    plt.show()



def plot_trend_of_generations_of_genetic_algorithm_based_on_changing_elitism_value():
    genetic_values = read_values_from_csv("genetic/many_elitism")
    grouped_elitism_values = defaultdict(list)
    for key, df in zip(genetic_values.keys(), genetic_values.values()):
        if len(df) < 9999:
            casted_key = int(key.split("-")[2])
            list_of_testruns = grouped_elitism_values[casted_key]
            list_of_testruns.append(len(df))
            grouped_elitism_values[casted_key] = list_of_testruns
    genetic_plot_values= dict(sorted(grouped_elitism_values.items()))
    data_items = genetic_plot_values.items()
    plotable_values = defaultdict(int)
    for key, values in data_items:
        z_scores = zscore(values)
        if len(values) >= 3:
            cleaned_values = [value for value, current_zscore in zip(values,z_scores) if current_zscore < 0.60]
            plotable_values[key] = np.mean(cleaned_values)
    plt.plot(list(plotable_values.keys()), list(plotable_values.values()), alpha=0.7)
    plt.title('Anzahl der Generationen mit verschiedenen Einstellungen und Algorithmen')
    plt.xlabel('Anzahl der übernommenen Eltern')
    plt.ylabel('Anzahl der Generationen')
    plt.show()



if __name__ == "__main__":
    main()
