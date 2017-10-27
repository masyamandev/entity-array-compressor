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


    @Alias("")
    class Instance extends AbstractAnnotationInstance implements Alias {

        private String value;

        public Instance() {
            this("");
        }

        public Instance(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        public Instance setValue(String value) {
            this.value = value;
            return this;
        }
    }
}
