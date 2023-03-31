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

import de.heaal.eaf.base.GenericIndividualFactory;
import de.heaal.eaf.base.Algorithm;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.IndividualFactory;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;

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
    private final String csvFileName;

    public HillClimbingAlgorithm(float[] min, float[] max,
                                 Comparator<Individual> comparator, Mutation mutator,
                                 ComparatorIndividual terminationCriterion) {
        super(comparator, mutator);
        this.indFac = new GenericIndividualFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        mutationOptions = new MutationOptions();
        mutationOptions.put(MutationOptions.KEYS.MUTATION_PROBABILITY, 0.5f);
        singleIndividualIndex = 0;
        csvFileName = "updatedAlgorithmValues.csv";
        File file = new File(csvFileName);
        if(file.exists()){
            file.delete();
        }
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
            writeUpdatedValuesToCsv();
        }
    }

    private void writeUpdatedValuesToCsv() {
        try {
            FileWriter fw = new FileWriter(csvFileName,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(population.get(singleIndividualIndex).getGenome().array()[0] + "," + population.get(singleIndividualIndex).getGenome().array()[1] + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        initialize(indFac, 1);
        while (!isTerminationCondition()) {
            nextGeneration();
        }
    }

}
