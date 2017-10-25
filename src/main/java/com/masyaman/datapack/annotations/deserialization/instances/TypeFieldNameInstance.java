package com.masyaman.datapack.annotations.deserialization.instances;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.deserialization.TypeFieldName;

@TypeFieldName
public class TypeFieldNameInstance extends AbstractAnnotationInstance implements TypeFieldName {
    private String typeField;

    public TypeFieldNameInstance(String typeField) {
        this.typeField = typeField;
    }

    @Override
    public String typeField() {
        return typeField;
    }

    public TypeFieldNameInstance setTypeField(String typeField) {
        this.typeField = typeField;
        return this;
    }
}
