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
import de.heaal.eaf.crossover.MeanCrossover;
import de.heaal.eaf.crossover.SinglePointCrossover;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.IndividualFactory;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;
import de.heaal.eaf.selection.SelectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Implementation of the Genetic algorithm.
 *
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class GeneticAlgorithm extends Algorithm<Individual> {

    private final IndividualFactory<Individual> indFac;
    private final ComparatorIndividual terminationCriterion;

    private final MutationOptions mutationOptions;
    //private final int singleIndividualIndex;
    private final String csvFileName;

    private final int population_size;
    private final Function<Individual,Float> fitness_function;
    private final boolean use_mean_crossover;
    private final int elite_size;

    public GeneticAlgorithm(float[] min, float[] max,
                                 Comparator<Individual> comparator, Mutation mutator,
                                 ComparatorIndividual terminationCriterion, int population_size,
                                Function<Individual,Float> fitness_function, boolean use_mean_crossover, int elite_size) {
        super(comparator, mutator);
        this.indFac = new GenericIndividualFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        this.population_size = population_size;
        this.fitness_function = fitness_function;
        this.use_mean_crossover = use_mean_crossover;
        this.elite_size = elite_size;
        mutationOptions = new MutationOptions();
        mutationOptions.put(MutationOptions.KEYS.MUTATION_PROBABILITY, 0.3f);
        csvFileName = "updatedAlgorithmValues.csv";
        File file = new File(csvFileName);
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public void nextGeneration() {
        super.nextGeneration();
        List<Individual> children = new ArrayList<>();
        List<Individual> crossover_children = new ArrayList<>();
        SinglePointCrossover<Individual> singlePointCrossover = new SinglePointCrossover<>();
        singlePointCrossover.setRandom(new Random());
        MeanCrossover<Individual> meanCrossover = new MeanCrossover<>();
        // Maybe better to use foreach here?
        // Calculate fitness for all parents
        for(int idx = 0; idx < population.size(); idx++){
            Individual current_individual = population.get(idx);
            current_individual.setCache(fitness_function.apply(current_individual));
        }
        // Sort parents by their fitness
        population.sort(comparator);
        while(children.size() < population.size()){
            // crossover
            Individual parent_1 = SelectionUtils.selectNormal(population, new Random(), null);
            Individual parent_2 = SelectionUtils.selectNormal(population, new Random(), parent_1);
            if (use_mean_crossover){
                children.add(meanCrossover.combine(new Individual[]{parent_1, parent_2}));
            } else {
                crossover_children = singlePointCrossover.produce_two_children(parent_1, parent_2);
                children.add(crossover_children.get(0));
                children.add(crossover_children.get(1));
            }
        }
        // Mutate some individuals. Only mutate at most one feature per individual.
        // Also set children as new parents. Keep best parents from last generation equal to the 'elite_size' parameter
        for (int idx = 0; idx < children.size()-elite_size; idx++){
            mutator.mutate(children.get(idx), mutationOptions);
            population.set(idx+elite_size, children.get(idx));
        }
        //writeUpdatedValuesToCsv();
    }

//    private void writeUpdatedValuesToCsv() {
//        try {
//            FileWriter fw = new FileWriter(csvFileName,true);
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.write(population.get(singleIndividualIndex).getGenome().array()[0] + "," + population.get(singleIndividualIndex).getGenome().array()[1] + "\n");
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public boolean isTerminationCondition() {
        // Sort the population by fitness so that the best individual is at position 0
        population.sort(comparator);
        return comparator.compare(population.get(0), terminationCriterion) > 0;
    }

    @Override
    public void run() {
        initialize(indFac, population_size);
        while (!isTerminationCondition()) {
            nextGeneration();
        }
    }

}

