package de.heaal.eaf.testbench;

import de.heaal.eaf.algorithm.GeneticAlgorithm;
import de.heaal.eaf.algorithm.HillClimbingAlgorithm;
import de.heaal.eaf.base.GenerationsCsvWriter;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
import de.heaal.eaf.mutation.RandomMutation;
import de.heaal.eaf.testbench.GenericAndHillClimbingTestConfiguration.RunConfiguration;

import java.io.IOException;
import java.util.List;

public class RunGenericAndHillClimbingTests {
    public static void main(String[] args) throws IOException {
        float[] min = {-5.12f, -5.12f};
        float[] max = {+5.12f, +5.12f};
        List<GenericAndHillClimbingTestConfiguration.RunConfiguration> configurationList = GenericAndHillClimbingTestConfiguration.getConfigurationList();
        for (RunConfiguration runConfiguration : configurationList) {
            var comparator = new MinimizeFunctionComparator(runConfiguration.fitnessFunction);
            GenerationsCsvWriter hillCsvWriter = new GenerationsCsvWriter(String.format("python/TestRuns/hill/%s-%s-%f.csv", "hill", runConfiguration.fitnessFunctionName, runConfiguration.mutationsProbability), false);
            HillClimbingAlgorithm hillClimbingAlgorithm = new HillClimbingAlgorithm(min, max, comparator, new RandomMutation(min, max), new ComparatorIndividual(0.0001f), runConfiguration.mutationsProbability, hillCsvWriter, runConfiguration.fitnessFunction);
            hillClimbingAlgorithm.run();
            GenerationsCsvWriter geneticCsvWriter = new GenerationsCsvWriter(String.format("python/TestRuns/genetic/%s-%s-%f-%d.csv", "genetic", runConfiguration.fitnessFunctionName, runConfiguration.mutationsProbability, runConfiguration.elitism), false, 100);
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(comparator, new RandomMutation(min, max), min, max, runConfiguration.fitnessFunction, new ComparatorIndividual(0.0001f), runConfiguration.mutationsProbability, geneticCsvWriter);
            geneticAlgorithm.setElitism(runConfiguration.elitism);
            geneticAlgorithm.run();
        }
        List<GenericAndHillClimbingTestConfiguration.RunConfiguration> configurationListDifferentEpitilism = GenericAndHillClimbingTestConfiguration.getConfigurationListWithManyElitism();
        for (RunConfiguration runConfiguration : configurationListDifferentEpitilism) {
            for (int testRun = 0; testRun <20; testRun++) {
                var comparator = new MinimizeFunctionComparator(runConfiguration.fitnessFunction);
                GenerationsCsvWriter geneticCsvWriter = new GenerationsCsvWriter(String.format("python/TestRuns/genetic/many_elitism/%s-%s-%d-%d.csv", "genetic", runConfiguration.fitnessFunctionName, runConfiguration.elitism,testRun), false, 100);
                GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(comparator, new RandomMutation(min, max), min, max, runConfiguration.fitnessFunction, new ComparatorIndividual(0.0001f), runConfiguration.mutationsProbability, geneticCsvWriter);
                geneticAlgorithm.setElitism(runConfiguration.elitism);
                geneticAlgorithm.run();
            }
        }
    }
}
