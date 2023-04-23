package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.*;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.testbench.DataCollector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class DEAlgorithm extends Algorithm<Individual>{
    private final float F;
    private final float c;
    private final int N;
    private final IndividualFactory<Individual> indFac;
    private final ComparatorIndividual terminationCriterion;
    private final DataCollector dataCollector;

    private final int dimensions;

    public DEAlgorithm(float[] min, float[] max, Comparator<Individual> comparator, ComparatorIndividual terminationCriterion, Mutation mutator, Random rng, final float F, final float c, final int N, DataCollector dataCollector) {
        super(comparator, mutator, rng);
        this.terminationCriterion = terminationCriterion;
        this.indFac = new GenericIndividualFactory(min, max);
        this.F = F;
        this.c = c;
        this.N = N;
        this.dimensions = min.length;
        this.dataCollector = dataCollector;
    }

    @Override
    protected boolean isTerminationCondition() {
        population.sort(comparator);
        for (int i = 0; i < population.size(); i++) {
            dataCollector.saveFitnessOfIndividual(population.get(i).getCache());
        }
        return comparator.compare(population.get(0), terminationCriterion) > 0;
    }

    @Override
    protected void nextGeneration() {
        super.nextGeneration();
        VecN[] u = new VecN[population.size()];
        for (int idx = 0; idx < population.size(); idx++){
            u[idx] = new VecN(this.dimensions);
        }
        for (int i = 0; i < population.size(); i++) {
            int r1 = getRandomIntegerWithNumbersToAvoid(new int[]{i}, 0,N);
            int r2 = getRandomIntegerWithNumbersToAvoid(new int[]{i, r1}, 0, N);
            int r3 = getRandomIntegerWithNumbersToAvoid(new int[]{i, r1, r2}, 0, N);
            Individual vI = population.get(r1).add(population.get(r2).sub(population.get(r3)).mul(F));
            int n = population.get(0).getGenome().len();
            int jR = new Random().nextInt(0,n);
            for (int j = 0; j < n; j++) {
                float rcj = new Random().nextFloat(0,1);
                //TODO Strategy Pattern anwenden und auslagern!
                if ((rcj < c) || (j == jR)) {
                    u[i].array()[j] = vI.getGenome().array()[j];
                } else {
                    u[i].array()[j] = population.get(i).getGenome().array()[j];
                }
            }
        }
        for (int i = 0; i < population.size(); i++) {
            if (comparator.compare(new GenericIndividual(u[i]), population.get(i)) < 0) {
                population.set(i, new GenericIndividual(u[i]));
            }
        }
    }

    private int getRandomIntegerWithNumbersToAvoid(int[] avoid, int lowerBound, int upperBound) {
        Random rand = new Random();
        int randomNum;

        do {
            randomNum = rand.nextInt(lowerBound,upperBound);
        } while (Arrays.binarySearch(avoid,randomNum)>=0);
        return randomNum;
    }

    @Override
    public void run() {
        initialize(indFac, N);
        while (!isTerminationCondition()) {
            dataCollector.prepareForNextGeneration();
            nextGeneration();
        }
        System.out.println(population.get(0).getGenome().toString());
    }
}
