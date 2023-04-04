package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.Algorithm;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.mutation.Mutation;

import java.util.Comparator;

public class GeneticAlgorithm extends Algorithm<Individual> {
    public GeneticAlgorithm(Comparator<Individual> comparator, Mutation mutator) {
        super(comparator, mutator);

    }

    @Override
    protected boolean isTerminationCondition() {
        return false;
    }

    @Override
    protected void run() {

    }
}
