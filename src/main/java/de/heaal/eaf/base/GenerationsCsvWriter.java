package de.heaal.eaf.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenerationsCsvWriter extends FileWriter {
    public GenerationsCsvWriter(String fileName, boolean append, int populationSize) throws IOException {
        super(fileName, append);
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        String resultString = IntStream.rangeClosed(0, populationSize)
                .mapToObj(i -> String.format("%s%d,", "Individual ", i))
                .collect(Collectors.joining());
        write("Generation, " + resultString + "Elitism, MutationProbability\n");
    }

    public GenerationsCsvWriter(String fileName, boolean append) throws IOException {
        super(fileName, append);
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        write("Generation, Individual, Elitism, MutationProbability\n");
    }

    public void clearFile() throws IOException {
        write("");
    }

    public void writeGeneration(int generation, float fitness, int elitism, float mutationProbability) throws IOException {
        write(String.format("%d,%f,%d,%f\n", generation, fitness, elitism, mutationProbability));
    }

    public void writeGeneration(int generation, List<Float> fitness, int elitism, float mutationProbability) throws IOException {
        StringBuilder fitnessCsvStringBuilder = new StringBuilder();
        fitness.forEach(fit -> fitnessCsvStringBuilder.append(",").append(fit.toString()));
        write(String.format("%d%s,%d,%f\n", generation, fitnessCsvStringBuilder, elitism, mutationProbability));
    }


    public void writeGeneration(int generation, float fitness, float mutationProbability) throws IOException {
        write(String.format("%d,%f,0,%f\n", generation, fitness, mutationProbability));
    }
}
