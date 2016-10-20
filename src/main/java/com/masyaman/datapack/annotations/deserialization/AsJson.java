package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AsJson {
    boolean numbersAsStrings() default false;
    String typeField() default "";
    // TODO add date deserialization format
}
