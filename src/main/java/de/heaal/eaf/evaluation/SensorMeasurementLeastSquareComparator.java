package de.heaal.eaf.evaluation;

import de.heaal.eaf.base.Individual;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class SensorMeasurementLeastSquareComparator<T extends Individual> implements Comparator<T> {
    private final List<List<String>> csvEntries;
    private final BiFunction<Individual, Float, Float> fitnessFunction;

    private final static int CSV_COLUMN_OF_SENSOR_VALUES = 0;

    private int getNthRowFromCsvFile(int entry, int position) throws IOException {
        return Integer.parseInt(csvEntries.get(entry).get(position).split(";")[0]);
    }

    public SensorMeasurementLeastSquareComparator(String filename, BiFunction<Individual, Float, Float> fitnessFunction) throws FileNotFoundException {
        csvEntries = new ArrayList<>();
        this.fitnessFunction = fitnessFunction;
        try (BufferedReader csvReader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                List<String> values = List.of(line.split(","));
                csvEntries.add(values);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return if o1 is "better" than o2 return 1. Otherwise, return -1. If they are equal the method returns 0
     */
    @Override
    public int compare(T o1, T o2) {
        float individualOneSumOfFailure = 0;
        float individualTwoSumOfFailure = 0;
        for (int i = 1; i < 500; i++) {
            try{
                float currentCsvEntry = Float.parseFloat(csvEntries.get(i).get(CSV_COLUMN_OF_SENSOR_VALUES));
                individualOneSumOfFailure += Math.pow(currentCsvEntry - fitnessFunction.apply(o1, (float) i), 2);
                individualTwoSumOfFailure += Math.pow(currentCsvEntry - fitnessFunction.apply(o2, (float) i), 2);
            } catch (NumberFormatException e) {
                System.out.println("An error occured");
            }
        }
        o1.setCache(individualOneSumOfFailure);
        o2.setCache(individualTwoSumOfFailure);
        return Float.compare(individualOneSumOfFailure, individualTwoSumOfFailure);
    }

    @Override
    public Comparator<T> reversed() {
        return Comparator.super.reversed();
    }

    public void evaluateFitness(Individual individual) {
        float individualSumOfFailure = 0;
        for (int i = 1; i < csvEntries.size(); i++) {
            try{

                float currentCsvEntry = Float.parseFloat(csvEntries.get(i).get(CSV_COLUMN_OF_SENSOR_VALUES));
                individualSumOfFailure += Math.pow(currentCsvEntry - fitnessFunction.apply(individual, (float) i), 2);
            } catch (NumberFormatException e) {
                System.out.println("An error occured");
            }
        }
        individual.setCache(individualSumOfFailure);
    }
}
