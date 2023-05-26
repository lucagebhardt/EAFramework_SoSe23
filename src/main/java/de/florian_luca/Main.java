package de.florian_luca;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import java.io.Serializable;
import java.util.Random;
import java.util.function.Supplier;


public class Main {
    public static void main(String[] args) {


        Train train = new Train(100, 100);
        final Op<Integer> accelerate = Op.of("accelerate", 1, v -> train.accelerate(v[0]));
        final Op<Integer> brake = Op.of("brake", 1, v -> train.brake(v[0]));
        final Op<Integer> noop = Op.of("noop", 0, v -> train.noop());
        final EphemeralConst<Integer> integerEphemeralConst = EphemeralConst.of(() -> new Random().nextInt(0, 100));
        final ISeq<Op<Integer>> OPERATIONS = ISeq.of(accelerate, brake, noop);
        final ISeq<Op<Integer>> TERMINALS = ISeq.of(integerEphemeralConst);

        final Regression<Double> REGRESSION = Regression.of(
                Regression.codecOf(OPERATIONS, TERMINALS, 1),
                Error.of(),// Eigene Fehlerfunktion, da auf die Abstände zu den Werten im Train ankommt
                //new Sample()..of(Integer.valueOf(0),Integer.valueOf(0),Integer.valueOf(1));
        );
    }
}
