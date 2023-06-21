package org.nastation.data.generator;

import org.vaadin.artur.exampledata.DataType;

import java.time.LocalDateTime;
import java.util.Random;

public class DoubleIdDataType extends DataType<Double> {
    private long sequence = 1;

    @Override
    public Double getValue(Random random, int seed, LocalDateTime referenceTime) {
        return 1D;
    }

}