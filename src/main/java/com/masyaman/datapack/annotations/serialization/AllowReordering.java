package com.masyaman.datapack.annotations.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specify if Collection values could be reordered for better compression or not.
 * By default all Arrays, Lists, LinkedHashSets, LinkedHashMaps are not allowed to be reordered whereas order of values
 * in other Sets or Maps could be changed during serialization.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowReordering {
    boolean value();
}
