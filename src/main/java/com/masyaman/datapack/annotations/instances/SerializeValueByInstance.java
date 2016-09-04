package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.SerializeValueBy;
import com.masyaman.datapack.serializers.SerializationFactory;

@SerializeValueBy(SerializationFactory.class)
public class SerializeValueByInstance extends AbstractAnnotationInstance implements SerializeValueBy {

    private final Class<? extends SerializationFactory> value;

    public SerializeValueByInstance(Class<? extends SerializationFactory> value) {
        this.value = value;
    }

    @Override
    public Class<? extends SerializationFactory> value() {
        return value;
    }

}
