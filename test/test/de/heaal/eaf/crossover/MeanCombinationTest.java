package test.de.heaal.eaf.crossover;

import de.heaal.eaf.base.GenericIndividual;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.VecN;
import de.heaal.eaf.crossover.MeanCombination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MeanCombinationTest {


    @Test
    void testCombineShallFailWithNullAsInput() {
        MeanCombination meanCombination = new MeanCombination();
        assertThrows(IllegalArgumentException.class,() ->meanCombination.combine(null));
    }

    @Test
    void testCombineShallFailWithParentObjectAsNull() {
        MeanCombination meanCombination = new MeanCombination();
        assertThrows(IllegalArgumentException.class,() ->meanCombination.combine(new Individual[]{null,null}));
    }

    @ParameterizedTest
    @ArgumentsSource(CombineTestArguments.class)
    void testCombine(float[] parent1Genomes, float[] parent2Genomes, float[] expectedIndividualGenomes) {
        MeanCombination meanCombination = new MeanCombination();
        Individual parent1 = new GenericIndividual(new VecN(parent1Genomes));
        Individual parent2 = new GenericIndividual(new VecN(parent2Genomes));
        Individual expectedIndividual = new GenericIndividual(new VecN(expectedIndividualGenomes));
        Individual computedIndividual = meanCombination.combine(new Individual[]{parent1, parent2});
        assertArrayEquals(expectedIndividual.getGenome().array(), computedIndividual.getGenome().array());
    }

    private static class CombineTestArguments implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new float[]{0f}, new float[]{0f}, new float[]{0f}),
                    Arguments.of(new float[]{1f}, new float[]{0f}, new float[]{0.5f}),
                    Arguments.of(new float[]{0f}, new float[]{1f}, new float[]{0.5f}),
                    Arguments.of(new float[]{1f}, new float[]{1f}, new float[]{1f}),
                    Arguments.of(new float[]{1f, 1f}, new float[]{0f, 0f}, new float[]{0.5f, 0.5f}),
                    Arguments.of(new float[]{0f, 25f, 50f}, new float[]{20, 75f, 150f}, new float[]{10f, 50f, 100f}
                    )
            );
        }
    }
}