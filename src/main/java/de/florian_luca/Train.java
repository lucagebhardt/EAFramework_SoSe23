package de.florian_luca;

public class Train {
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
        return 0.0;
    }
}
