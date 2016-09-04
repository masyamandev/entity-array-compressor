package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.SerializeKeyBy;
import com.masyaman.datapack.serializers.SerializationFactory;

@SerializeKeyBy(SerializationFactory.class)
public class SerializeKeyByInstance extends AbstractAnnotationInstance implements SerializeKeyBy {

    private final Class<? extends SerializationFactory> value;

    public SerializeKeyByInstance(Class<? extends SerializationFactory> value) {
        this.value = value;
    }

    @Override
    public Class<? extends SerializationFactory> value() {
        return value;
    }

}
