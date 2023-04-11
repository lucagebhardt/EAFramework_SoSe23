/*
 * Evolutionary Algorithms Framework
 *
 * Copyright (c) 2023 Christian Lins <christian.lins@haw-hamburg.de>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.*;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;

import java.io.IOException;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Implementation of the Hill Climbing algorithm.
 *
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class HillClimbingAlgorithm extends Algorithm<Individual> {

    private final IndividualFactory<Individual> indFac;
    private final ComparatorIndividual terminationCriterion;

    private final MutationOptions mutationOptions;
    private final int singleIndividualIndex;
    private final GenerationsCsvWriter csvWriter;
    private final float mutationProbability;
    private final Function<Individual, Float> fitnessFunction;

    public HillClimbingAlgorithm(float[] min, float[] max,
                                 Comparator<Individual> comparator, Mutation mutator,
                                 ComparatorIndividual terminationCriterion,
                                 float mutationProbability, GenerationsCsvWriter csvWriter, Function<Individual, Float> fitnessFunction) {
        super(comparator, mutator);
        this.indFac = new GenericIndividualFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        mutationOptions = new MutationOptions();
        this.mutationProbability= mutationProbability;
        mutationOptions.put(MutationOptions.KEYS.MUTATION_PROBABILITY, mutationProbability);
        singleIndividualIndex = 0;
        this.csvWriter = csvWriter;
        this.fitnessFunction = fitnessFunction;
    }


    private boolean mutatedIndividualIsBetter(int compareResult) {
        return compareResult > 0;
    }

    @Override
    public void nextGeneration() {

        super.nextGeneration();
        Individual chosenIndividual = population.get(singleIndividualIndex);
        Individual copyOfChosenIndividual = chosenIndividual.copy();
        mutator.mutate(copyOfChosenIndividual, mutationOptions);
        int compareResult = comparator.compare(copyOfChosenIndividual, chosenIndividual);
        if (mutatedIndividualIsBetter(compareResult)) {
            population.set(singleIndividualIndex, copyOfChosenIndividual);
        }
    }


    @Override
    public boolean isTerminationCondition() {
        // Because we only have a population of 1 individual we know that
        // this individual is our current best.
        return comparator.compare(population.get(0), terminationCriterion) > 0;
    }

    @Override
    public void run() {
        int generationCounter = 0;
        initialize(indFac, 1);
        while (!isTerminationCondition()&& generationCounter<10000) {
            try {
                csvWriter.writeGeneration(generationCounter,fitnessFunction.apply(population.get(0)),this.mutationProbability);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            nextGeneration();
            generationCounter++;
        }
        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
