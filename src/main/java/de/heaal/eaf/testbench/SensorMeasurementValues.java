package de.heaal.eaf.testbench;

import de.heaal.eaf.base.Individual;

import java.util.function.BiFunction;
import java.util.function.Function;


public class SensorMeasurementValues {
    public static void main(String[] args) {
        BiFunction<Individual,Float,Float> fitnessFunction = (individual, t) -> {
            float A = individual.getGenome().array()[0];
            float f = individual.getGenome().array()[1];
            float phi = individual.getGenome().array()[2];
            float D = individual.getGenome().array()[3];
            return (float) (A*Math.sin(2*Math.PI*f*t+phi)+D);
        };

    }
}
