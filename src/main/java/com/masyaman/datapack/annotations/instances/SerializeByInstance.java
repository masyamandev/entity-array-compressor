package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.SerializeBy;
import com.masyaman.datapack.serializers.SerializationFactory;

@SerializeBy(SerializationFactory.class)
public class SerializeByInstance extends AbstractAnnotationInstance implements SerializeBy {

    private final Class<? extends SerializationFactory> value;

    public SerializeByInstance(Class<? extends SerializationFactory> value) {
        this.value = value;
    }

    @Override
    public Class<? extends SerializationFactory> value() {
        return value;
    }

}
