package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.annotations.SerializeKeyBy;
import com.masyaman.datapack.serializers.SerializationFactory;

@SerializeKeyBy(SerializationFactory.class)
public class SerializeKeyByInstance extends AbstractAnnotationInstance implements SerializeKeyBy {

    private final Class<? extends SerializationFactory> value;
    private final Class serializeAs;
    private final Class annotationsFrom;

    public SerializeKeyByInstance(Class<? extends SerializationFactory> value) {
        this(value, InheritFromParent.class, InheritFromParent.class);
    }

    public SerializeKeyByInstance(Class<? extends SerializationFactory> value, Class serializeAs, Class annotationsFrom) {
        this.value = value;
        this.serializeAs = serializeAs;
        this.annotationsFrom = annotationsFrom;
    }

    @Override
    public Class<? extends SerializationFactory> value() {
        return value;
    }

    @Override
    public Class serializeAs() {
        return serializeAs;
    }

    @Override
    public Class annotationsFrom() {
        return annotationsFrom;
    }

}
