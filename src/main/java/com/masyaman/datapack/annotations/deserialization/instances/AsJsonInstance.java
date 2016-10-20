package com.masyaman.datapack.annotations.deserialization.instances;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.deserialization.AsJson;

@AsJson
public class AsJsonInstance extends AbstractAnnotationInstance implements AsJson {
    private boolean numbersAsStrings;
    private String typeField;

    public AsJsonInstance() {
        this(false, NO_FIELD_TYPE);
    }

    public AsJsonInstance(boolean numbersAsStrings, String typeField) {
        this.numbersAsStrings = numbersAsStrings;
        this.typeField = typeField;
    }

    public AsJsonInstance(AsJson toClone) {
        this(toClone.numbersAsStrings(), toClone.typeField());
    }

    @Override
    public boolean numbersAsStrings() {
        return numbersAsStrings;
    }

    @Override
    public String typeField() {
        return typeField;
    }

    public AsJsonInstance setNumbersAsStrings(boolean numbersAsStrings) {
        this.numbersAsStrings = numbersAsStrings;
        return this;
    }

    public AsJsonInstance setTypeField(String typeField) {
        this.typeField = typeField;
        return this;
    }
}
