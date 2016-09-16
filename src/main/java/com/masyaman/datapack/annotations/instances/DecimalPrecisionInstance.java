package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.DecimalPrecision;

@DecimalPrecision(0)
public class DecimalPrecisionInstance extends AbstractAnnotationInstance implements DecimalPrecision {

    private final int precision;

    public DecimalPrecisionInstance(int precision) {
        this.precision = precision;
    }

    @Override
    public int value() {
        return precision;
    }
}
