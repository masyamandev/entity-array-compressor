package com.masyaman.datapack.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.RoundingMode;

@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalPrecision {
    int value();
    RoundingMode roundingMode() default RoundingMode.HALF_UP;
}
