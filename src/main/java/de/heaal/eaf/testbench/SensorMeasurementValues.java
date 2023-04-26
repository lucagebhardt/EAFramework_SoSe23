package de.heaal.eaf.testbench;

import de.heaal.eaf.algorithm.DEAlgorithm;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
import de.heaal.eaf.evaluation.SensorMeasurementLeastSquareComparator;
import de.heaal.eaf.mutation.RandomMutation;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;


public class SensorMeasurementValues {
    public static void main(String[] args) throws FileNotFoundException {
        float[] min = {-1000, -1000, -1000, -1000};
        float[] max = {1000, 1000, 1000, 1000};
        BiFunction<Individual,Float,Float> fitnessFunction = (individual, t) -> {
            float A = individual.getGenome().array()[0];
            float f = individual.getGenome().array()[1];
            float phi = individual.getGenome().array()[2];
            float D = individual.getGenome().array()[3];
            return (float) (A*Math.sin(2*Math.PI*f*t+phi)+D);
        };
        var comparator = new SensorMeasurementLeastSquareComparator("resources/sensordata.csv", fitnessFunction);
        float F = 0.45f;
        float c = 0.5f;
        int N = 100;
        int maxGenerations = 1000;
        var algo = new DEAlgorithm(min, max,
                comparator, 1.f, new RandomMutation(min, max),new Random(), F, c, N, new CsvCreator("sensordata_DE_F=0_45-c=0_5-N=100_data=500_maxGen=10000_with_csv", maxGenerations));
        algo.run();

    }
}
