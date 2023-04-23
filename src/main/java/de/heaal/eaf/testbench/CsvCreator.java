package de.heaal.eaf.testbench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvCreator implements DataCollector{
    private final String csvFileName;
    private File file;

    private int generation_counter;
    private boolean done = false;

    private final int max_generations;
    public CsvCreator(String csvFileName, int max_generations) {
        this.generation_counter = 0;
        this.max_generations = max_generations;
        this.csvFileName = csvFileName;
        this.file = new File(csvFileName);
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public void saveFitnessOfIndividual(float fitness_value) {
        if (generation_counter < max_generations){
            writeToCsv(String.format("%.6f\t", fitness_value));
        } else if (!done){
            System.out.println("Csv file created\n");
            done = true;
        }
    }

    @Override
    public void prepareForNextGeneration(){
        if (generation_counter < max_generations) {
            writeToCsv("\n");
            generation_counter++;
        }
    }
    private void writeToCsv(String string){
        try {
            FileWriter fw = new FileWriter(csvFileName,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(string);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}