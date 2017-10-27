package com.masyaman.datapack.annotations.deserialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Deserialize objects as Json Strings. Field name could be specified to add original Class type using {@link TypeFieldName}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AsJson {

    boolean numbersAsStrings() default false;


    @AsJson
    class Instance extends AbstractAnnotationInstance implements AsJson {
        private boolean numbersAsStrings;

        public Instance() {
            this(false);
        }

        public Instance(boolean numbersAsStrings) {
            this.numbersAsStrings = numbersAsStrings;
        }

        @Override
        public boolean numbersAsStrings() {
            return numbersAsStrings;
        }

        public Instance setNumbersAsStrings(boolean numbersAsStrings) {
            this.numbersAsStrings = numbersAsStrings;
            return this;
        }
    }
}
