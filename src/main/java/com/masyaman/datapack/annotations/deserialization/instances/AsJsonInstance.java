package com.masyaman.datapack.annotations.deserialization.instances;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.deserialization.TypeFieldName;

@AsJson
public class AsJsonInstance extends AbstractAnnotationInstance implements AsJson {
    private boolean numbersAsStrings;

    public AsJsonInstance() {
        this(false);
    }

    public AsJsonInstance(boolean numbersAsStrings) {
        this.numbersAsStrings = numbersAsStrings;
    }

    @Override
    public boolean numbersAsStrings() {
        return numbersAsStrings;
    }

    public AsJsonInstance setNumbersAsStrings(boolean numbersAsStrings) {
        this.numbersAsStrings = numbersAsStrings;
        return this;
    }

}
