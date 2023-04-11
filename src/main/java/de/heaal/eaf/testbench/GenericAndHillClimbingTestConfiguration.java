package de.heaal.eaf.testbench;

import de.heaal.eaf.base.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.Math.*;
import static java.lang.Math.exp;

public class GenericAndHillClimbingTestConfiguration {

    private static FitnessFunction evalAckley = new FitnessFunction("ACKLEY", (ind) -> {
        var x0 = ind.getGenome().array()[0];
        var x1 = ind.getGenome().array()[1];
        return (float) (-20 * exp(-0.2 * sqrt(0.5 * (pow(x0, 2) + pow(x1, 2)))) -
                exp(0.5 * (cos(2 * PI * x0) + cos(2 * PI * x1))) + exp(1) + 20);
    });
    // Sphere Function n=2
    private static FitnessFunction evalSphereFunc2D = new FitnessFunction("SPHERE",
            (ind) -> {
                var x0 = ind.getGenome().array()[0];
                var x1 = ind.getGenome().array()[1];
                return x0 * x0 + x1 * x1;
            });

    public static List<RunConfiguration> getConfigurationList() {
        List<RunConfiguration> configurationList = new ArrayList<>();
        List<FitnessFunction> fitnessFunctions = List.of(evalAckley, evalSphereFunc2D);
        for (FitnessFunction fitnessFunction : fitnessFunctions) {
            for (int elitism : new int[]{0, 1, 10}) {
                for (float mutationsProbability : new float[]{0.1f, 0.3f}) {
                    configurationList.add(new RunConfiguration(fitnessFunction.functionName, fitnessFunction.fitnessFunction, elitism, mutationsProbability));
                }
            }

        }
        return configurationList;
    }

    public static List<RunConfiguration> getConfigurationListWithManyElitism() {
        List<RunConfiguration> configurationList = new ArrayList<>();
        List<Integer> numbers = IntStream.range(0, 100)
                .filter(i -> i % 2 == 0)
                .boxed()
                .toList();
        for (int elitism : numbers) {
            configurationList.add(new RunConfiguration("SPHERE", evalAckley.fitnessFunction, elitism, 0.2f));
        }
        return configurationList;
    }

    public static class RunConfiguration {

        public String fitnessFunctionName;
        public Function<Individual, Float> fitnessFunction;
        int elitism;
        float mutationsProbability;

        public RunConfiguration(String fitnessFunctionName, Function<Individual, Float> fitnessFunction, int elitism, float mutationsProbability) {
            this.fitnessFunctionName = fitnessFunctionName;
            this.fitnessFunction = fitnessFunction;
            this.elitism = elitism;
            this.mutationsProbability = mutationsProbability;
        }
    }

    private record FitnessFunction(String functionName, Function<Individual, Float> fitnessFunction) {
    }
}
