package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.*;
import de.heaal.eaf.crossover.MeanCombination;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;
import de.heaal.eaf.selection.SelectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneticAlgorithm extends Algorithm<Individual> {
    private final IndividualFactory<Individual> individualFactory;
    private final Function<Individual, Float> fitnessFunction;
    private final ComparatorIndividual terminationCriterion;

    public GeneticAlgorithm(Comparator<Individual> comparator, Mutation mutator, float[] min, float[] max, Function<Individual, Float> fitnessFunction, ComparatorIndividual terminationCriterion) {
        super(comparator, mutator);
        this.individualFactory = new GenericIndividualFactory(min, max);
        this.fitnessFunction = fitnessFunction;
        this.terminationCriterion = terminationCriterion;
    }

    @Override
    public void nextGeneration() {
        super.nextGeneration();

        Population<Individual> parentsFitness = getParentsSortedByFitness();
        List<Individual> children = new ArrayList<>();
        while (children.size() < parentsFitness.size()) {
            for (int i = 0; i < 2; i++) {
                Individual firstParent = SelectionUtils.selectNormal(population, new Random(), null);
                Individual secondParent = SelectionUtils.selectNormal(population, new Random(), firstParent);
                Individual newChildren = new MeanCombination().combine(new Individual[]{firstParent, secondParent});
                children.add(newChildren);
            }
        }
        int randomNumberOfChildrenToMutate = new Random().nextInt(children.size());
        MutationOptions mutationOptions = new MutationOptions();
        for (int i = 0; i < randomNumberOfChildrenToMutate; i++) {
            mutator.mutate(children.get(i), mutationOptions);
        }
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
        initialize(individualFactory, 10);
        while (!isTerminationCondition()) {
            nextGeneration();
        }
        System.out.println(population.get(0).getGenome().toString());
    }
}
