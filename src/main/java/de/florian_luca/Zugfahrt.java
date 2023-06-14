package de.florian_luca;

import io.jenetics.Genotype;
import io.jenetics.MultiPointCrossover;
import io.jenetics.Mutator;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.*;
import io.jenetics.util.ISeq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author chris
 */
public class Zugfahrt {

    private static final int POPULATION = 50;
    private static final int GENERATIONS = 200;

    private static final double INITIAL_DISTANCE = 1000;

    private static double distanceOfBestTrain = Integer.MAX_VALUE;
    public static double error(Genotype<ProgramGene<Double>> ind) {
        Zug zug = new Zug();
        
        // Setze die Zug instance bei allen Operationen und Terminalen
        for (var op : ind.gene().operations()) {
            if (op instanceof Zug.Operation operation) {
                operation.setZug(zug);
            }
        }
        
        for (var op : ind.gene().terminals()) {
            if (op instanceof Zug.Operation operation) {
                operation.setZug(zug);
            }
        }
        var gene = ind.gene();
        for (int i = 0; i < 100; i++) { // Maximal 100 Schritte
            // Wir rufen das Kontrollprogramm des Zugs auf.
            // Je nach Entfernung (und ggfls. aktueller Geschwindigkeit) soll
            // das Programm die neue Geschwindigkeit des Zuges setzen.
            // Der Kontroll-Loop wird durch die For-Schleife hier repräsentiert,
            // weil dies in GP schwierig als Operation darzustellen ist.
            gene.eval();

            // Wir lassen Entfernung und Energie aktualisieren
            zug.tick();
            double distanceLeftTillDestination = zug.getEntfernung() - INITIAL_DISTANCE;
            if (distanceLeftTillDestination >= 0) {
                break;
            }
        }

        distanceOfBestTrain = zug.getEntfernung()-INITIAL_DISTANCE;
        if(distanceOfBestTrain<0) {
            // Individuen, die nicht ankommen, werden bestraft
            return Integer.MAX_VALUE;
        }
        //
        int highestPriority = 3;
        int mediumPriority = 2;
        int lowestPriority = 1;
        return  highestPriority*distanceOfBestTrain + mediumPriority*zug.getEnergie() + mediumPriority*zug.getTickCounter() + lowestPriority*ind.geneCount();
    }
    
    public static void main(String[] args) {
        final ISeq<Op<Double>> operations = ISeq.of(
                new Zug.SetSpeed(), 
                new Zug.IfElse(),
                MathOp.ADD, MathOp.SUB, MathOp.MUL); // Hier haben wir noch das MUL hinzugefügt
        
        final ISeq<Op<Double>> terminals = ISeq.of(
                new Zug.GetSpeed(), 
                new Zug.GetDistance(), 
                Const.of(0.0), Const.of(1.0), Const.of(2.0), Const.of(3.0),
                Const.of(-1.0), Const.of(-2.0), Const.of(-3.0)
        );
        
        final ProgramChromosome<Double> program
                = ProgramChromosome.of(5, operations, terminals);

        final Engine<ProgramGene<Double>, Double> engine = Engine
                .builder(Zugfahrt::error, program)
                .minimizing()
                .alterers(
                        new SingleNodeCrossover<>(),
                        new MultiPointCrossover<>(),
                        new Mutator<>())
                .populationSize(POPULATION)
                .build();

        final var results = engine
                .stream()
                .limit(Limits.byFixedGeneration(GENERATIONS)).collect(Collectors.toList());

        EvolutionResult result = results.get(GENERATIONS-1); // last generation
        List<Genotype<ProgramGene<Double>>> pop = new ArrayList<>(result.genotypes().asList());
        pop.sort((a, b) -> {
            double e1 = error(a);
            double e2 = error(b);
            if (e1 > e2) {
                return 1;
            } else if (e2 > e1) {
                return -1;
            } else {
                return 0;
            }
        });

        Genotype<ProgramGene<Double>> best = result.bestPhenotype().genotype();

        final TreeNode<Op<Double>> tree = pop.get(0).gene().toTreeNode();
        System.out.println("Generations: " + result.totalGenerations());
        System.out.println("Function:    " + tree);
        System.out.println("Error:       " + error(pop.get(0)));
        List<List<String>> treeString = new ArrayList<>(tree.depth()+1);
        for (int i = 0; i < tree.depth()+1; i++){
            treeString.add(i, new ArrayList<>());
        }
        System.out.println("Entfernung zum Ziel:  " + distanceOfBestTrain);
        printBestTrain(INITIAL_DISTANCE-distanceOfBestTrain, INITIAL_DISTANCE);

    }

    public static void createTreeString(TreeNode<Op<Double>> tree, int idx, List<List<String>> treeString){
        treeString.get(idx).add(tree.value().toString());
        Iterator<TreeNode<Op<Double>>> iter = tree.childIterator();
        idx++;
        while(iter.hasNext()){
            createTreeString(iter.next(), idx, treeString);
        }
    }

    public static void printBestTrain(double distanceAlreadyDriven, double initialDistanceToTarget){
        if (distanceAlreadyDriven < 0 || distanceAlreadyDriven > initialDistanceToTarget) {
            System.out.println("Train moved backwards or overshot the target!");
            return;
        }
        int percentageAlreadyDriven = (int) (distanceAlreadyDriven/initialDistanceToTarget*100);
        StringBuilder visualization = new StringBuilder("S");
        if(percentageAlreadyDriven > 0){
            for(int i=0; i<percentageAlreadyDriven; i++){
                visualization.append("_");
            }
        }
        visualization.append("0");
        if(percentageAlreadyDriven < 100 && percentageAlreadyDriven > 0){
            for(int i=percentageAlreadyDriven + 1; i<100; i++){
                visualization.append("_");
            }
        }
        visualization.append("T");
        System.out.println(visualization);
    }
}
