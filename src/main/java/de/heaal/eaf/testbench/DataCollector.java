package de.heaal.eaf.testbench;

public interface DataCollector {
    void saveFitnessOfIndividual(float fitness);
    void prepareForNextGeneration();
}
