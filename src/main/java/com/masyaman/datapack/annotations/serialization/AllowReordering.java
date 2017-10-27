package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.Alias;

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


    @AllowReordering(false)
    class Instance extends AbstractAnnotationInstance implements AllowReordering {

        private boolean value;

        public Instance() {
            this(false);
        }

        public Instance(boolean value) {
            this.value = value;
        }

        @Override
        public boolean value() {
            return value;
        }

        public Instance setValue(boolean value) {
            this.value = value;
            return this;
        }
    }
}
