package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.Alias;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Cache size of cached serializers. Smaller values causes less memory overhead but could increase output size.
 * Values <= 0 means unlimited cache size.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheSize {

    int value();


    @CacheSize(0)
    class Instance extends AbstractAnnotationInstance implements CacheSize {

        private int value;

        public Instance() {
            this(0);
        }

        public Instance(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return value;
        }

        public Instance setValue(int value) {
            this.value = value;
            return this;
        }
    }
}
