package com.masyaman.datapack.annotations.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.RoundingMode;

@Retention(RetentionPolicy.RUNTIME)
public @interface Precision {
    int value();
    RoundingMode roundingMode() default RoundingMode.HALF_UP;
}
