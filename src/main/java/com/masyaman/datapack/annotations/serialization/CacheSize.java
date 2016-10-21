package com.masyaman.datapack.annotations.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Cache size of cached serializers. Smaller values causes less memory overhead but could increase output size.
 * Values <= 0 means unlimited cache size.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheSize {
    int value();
}
