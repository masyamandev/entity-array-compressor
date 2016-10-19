package com.masyaman.datapack.annotations.serialization.instances;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.serialization.Precision;

import java.math.RoundingMode;

@Precision(0)
public class PrecisionInstance extends AbstractAnnotationInstance implements Precision {

    private final int precision;
    private RoundingMode roundingMode;

    public PrecisionInstance(int precision) {
        this(precision, RoundingMode.HALF_UP);
    }

    public PrecisionInstance(int precision, RoundingMode roundingMode) {
        this.precision = precision;
        this.roundingMode = roundingMode;
    }

    @Override
    public int value() {
        return precision;
    }

    @Override
    public RoundingMode roundingMode() {
        return roundingMode;
    }
}
