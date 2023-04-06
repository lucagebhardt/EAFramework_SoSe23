package de.heaal.eaf.crossover;

import de.heaal.eaf.base.GenericIndividual;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.VecN;

import java.util.Arrays;
import java.util.Random;

public class MeanCombination implements Combination {


    @Override
    public void setRandom(Random rng) {
    }

    @Override
    public Individual combine(Individual[] parents) {
        if (parents==null || Arrays.asList(parents).contains(null)) {
            throw new IllegalArgumentException("argument array can't be null and can't contain null references");
        }
        float[] sumOfGenomeValues = new float[parents[0].getGenome().len()];
        for (Individual currentParent : parents) {

            float[] genomeAsArray = currentParent.getGenome().array();
            for (int i = 0; i < genomeAsArray.length; i++) {
                sumOfGenomeValues[i] += genomeAsArray[i];
            }
        }
        for (int i = 0; i < sumOfGenomeValues.length; i++) {
            sumOfGenomeValues[i] = sumOfGenomeValues[i]/parents.length;
        }
        return new GenericIndividual(new VecN(sumOfGenomeValues));
    }
}
