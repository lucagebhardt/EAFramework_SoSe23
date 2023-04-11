package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.*;
import de.heaal.eaf.crossover.MeanCombination;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;
import de.heaal.eaf.selection.SelectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneticAlgorithm extends Algorithm<Individual> {
    private final IndividualFactory<Individual> individualFactory;
    private final Function<Individual, Float> fitnessFunction;
    private final ComparatorIndividual terminationCriterion;
    private final GenerationsCsvWriter csvWriter;
    private final float mutationProbability;
    private final MutationOptions mutationOptions;

    private int elitism;

    public void setElitism(int elitism) {
        this.elitism = elitism;
    }


    public GeneticAlgorithm(Comparator<Individual> comparator, Mutation mutator, float[] min, float[] max, Function<Individual, Float> fitnessFunction, ComparatorIndividual terminationCriterion, float mutationProbability, GenerationsCsvWriter csvWriter) {
        super(comparator, mutator);
        this.individualFactory = new GenericIndividualFactory(min, max);
        this.fitnessFunction = fitnessFunction;
        this.terminationCriterion = terminationCriterion;
        this.elitism = 0;
        this.csvWriter = csvWriter;
        this.mutationProbability = mutationProbability;
        mutationOptions = new MutationOptions();
        mutationOptions.put(MutationOptions.KEYS.MUTATION_PROBABILITY, mutationProbability);

    }

    @Override
    public void nextGeneration() {
        super.nextGeneration();
        Population<Individual> parentsFitness = getParentsSortedByFitness();
        List<Individual> children = new ArrayList<>(parentsFitness.asList().subList(0, elitism));
        while (children.size() < parentsFitness.size()) {
            Individual firstParent = SelectionUtils.selectNormal(population, new Random(), null);
            Individual secondParent = SelectionUtils.selectNormal(population, new Random(), firstParent);
            int numberOfParents = 2;
            for (int i = 0; i < numberOfParents && children.size() != population.size(); i++) {
                Individual newChildren = new MeanCombination().combine(new Individual[]{firstParent, secondParent});
                children.add(newChildren);
            }
        }
        children.subList(elitism,children.size()).forEach(i -> mutator.mutate(i,mutationOptions));
        population.set(children);
    }

    private Population<Individual> getParentsSortedByFitness() {
        List<Individual> newPopulationAsList = population.asList().stream().sorted((ind1, ind2) -> -1 * comparator.compare(ind1, ind2)).collect(Collectors.toList());
        return new Population<>(newPopulationAsList);
    }

    @Override
    protected boolean isTerminationCondition() {
        return comparator.compare(population.get(0), terminationCriterion) > 0;
    }

    @Override
    public void run() {
        //field population is equal to parents
        initialize(individualFactory, 100);
        int generation = 0;
        while (!isTerminationCondition() && generation < 10000) {
            List<Float> populationFitness = population.asList().stream().map(this.fitnessFunction).toList();
            try {
                this.csvWriter.writeGeneration(generation, populationFitness, elitism, mutationProbability);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            nextGeneration();
            generation++;
        }
        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
