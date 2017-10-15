package com.masyaman.datapack.annotations;

import com.masyaman.datapack.settings.ClassManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Add alias to class or field.
 * Warn: when alias is added to a class, class should be explicitly specified in {@link ClassManager} during deserialization.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    String value();
}
