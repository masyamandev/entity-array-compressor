package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.Alias;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Ignore annotated field during serialization.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoredField {


    @IgnoredField
    class Instance extends AbstractAnnotationInstance implements IgnoredField {
    }
}
