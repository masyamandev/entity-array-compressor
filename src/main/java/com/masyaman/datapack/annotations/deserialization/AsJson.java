package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AsJson {
    boolean numbersAsStrings() default false;
    // TODO add field for saving class name / object type
    // TODO add date deserialization format
}
