package com.masyaman.datapack.annotations.serialization.instances;

import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.serializers.SerializationFactory;

@SerializeBy(SerializationFactory.class)
public class SerializeByInstance extends AbstractAnnotationInstance implements SerializeBy {

    private final Class<? extends SerializationFactory> value;
    private final Class serializeAs;
    private final Class annotationsFrom;

    public SerializeByInstance(Class<? extends SerializationFactory> value) {
        this(value, InheritFromParent.class, InheritFromParent.class);
    }

    public SerializeByInstance(Class<? extends SerializationFactory> value, Class serializeAs, Class annotationsFrom) {
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
