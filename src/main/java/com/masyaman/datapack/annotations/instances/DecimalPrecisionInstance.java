package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.DecimalPrecision;

import java.math.RoundingMode;

@DecimalPrecision(0)
public class DecimalPrecisionInstance extends AbstractAnnotationInstance implements DecimalPrecision {

    private final int precision;
    private RoundingMode roundingMode;

    public DecimalPrecisionInstance(int precision) {
        this(precision, RoundingMode.HALF_UP);
    }

    public DecimalPrecisionInstance(int precision, RoundingMode roundingMode) {
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
