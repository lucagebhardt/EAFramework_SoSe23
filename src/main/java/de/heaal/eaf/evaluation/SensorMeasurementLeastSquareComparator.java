package de.heaal.eaf.evaluation;

import de.heaal.eaf.base.Individual;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SensorMeasurementLeastSquareComparator<T extends Individual> implements Comparator<T> {
    private final List<List<String>> csvEntries;

    private int getNthRowFromCsvFile(int entry, int position) throws IOException {
        return Integer.parseInt(csvEntries.get(entry).get(position).split(";")[0]);
    }

    public SensorMeasurementLeastSquareComparator(String filename) throws FileNotFoundException {
        csvEntries = new ArrayList<>();

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
        return 1;
    }

    @Override
    public Comparator<T> reversed() {
        return Comparator.super.reversed();
    }
}
