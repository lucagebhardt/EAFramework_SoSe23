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
//        float[] min = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
//        float[] max = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
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
        var algo = new DEAlgorithm(min, max,
                comparator, 10.f, new RandomMutation(min, max),new Random(),0.4f, 0.1f, 10, new CsvCreator("DE_with_csv", 10000));
        algo.run();

    }
}
