package org.nastation.data.generator;

import org.vaadin.artur.exampledata.DataType;

import java.time.LocalDateTime;
import java.util.Random;

public class LongIdDataType extends DataType<Long> {
    private long sequence = 1;

    @Override
    public Long getValue(Random random, int seed, LocalDateTime referenceTime) {
        return sequence++;
    }

}