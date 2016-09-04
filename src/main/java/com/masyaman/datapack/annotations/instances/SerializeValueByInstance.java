package com.masyaman.datapack.annotations.instances;

import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.annotations.SerializeValueBy;
import com.masyaman.datapack.serializers.SerializationFactory;

@SerializeValueBy(SerializationFactory.class)
public class SerializeValueByInstance extends AbstractAnnotationInstance implements SerializeValueBy {

    private final Class<? extends SerializationFactory> value;
    private final Class serializeAs;
    private final Class annotationsFrom;

    public SerializeValueByInstance(Class<? extends SerializationFactory> value) {
        this(value, InheritFromParent.class, InheritFromParent.class);
    }

    public SerializeValueByInstance(Class<? extends SerializationFactory> value, Class serializeAs, Class annotationsFrom) {
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
