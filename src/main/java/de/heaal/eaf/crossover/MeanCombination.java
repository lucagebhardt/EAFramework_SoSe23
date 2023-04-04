package de.heaal.eaf.crossover;

import de.heaal.eaf.base.GenericIndividual;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.VecN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MeanCombination implements Combination {


    @Override
    public void setRandom(Random rng) {
    }

    @Override
    public Individual combine(Individual[] parents) {
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
