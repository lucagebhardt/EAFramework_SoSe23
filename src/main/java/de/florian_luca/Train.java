package de.florian_luca;

import io.jenetics.EnumGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;

import java.util.function.Function;

public class Train implements Problem<ISeq<Integer>, EnumGene<Integer>, Double> {
    private final int vmax;
    private int velocity;
    private int distance;
    private int energy;
    private int time;

    Train(int vmax, int distance) {
        this.vmax = vmax;
        this.distance = distance;
        this.time = 0;
        this.energy = 0;
    }

    public int accelerate(int accelerationFactor) {
        int newVelocity = velocity + accelerationFactor;
        if (newVelocity <= vmax) {
            velocity = newVelocity;
        }
        return velocity;
    }

    public int brake(int brakeFactor) {
        int newVelocity = velocity - brakeFactor;
        if (newVelocity >= 0) {
            velocity = newVelocity;
        }
        return newVelocity;
    }

    public int noop() {
        distance -= velocity;
        energy += Math.pow(velocity,2);
        time++;
        return 0;
    }


    @Override
    public Function<ISeq<Integer>, Double> fitness() {
        return genomes -> {
            double scalingTime = 1.0;
            double scalingDistance = 1.0;
            double scalingEnergy = 1.0;
            return scalingDistance*distance + scalingEnergy*energy + scalingTime*time;
        };
    }

    @Override
    public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
        return null;
    }
}
