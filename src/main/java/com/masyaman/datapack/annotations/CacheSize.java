package com.masyaman.datapack.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CacheSize {
    int value() default CacheSizes.CACHE_1_BYTE;
}
