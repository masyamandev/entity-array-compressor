package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.*;

import static com.masyaman.datapack.annotations.AnnotationsHelper.allowReordering;
import static com.masyaman.datapack.annotations.AnnotationsHelper.annotationsFrom;
import static com.masyaman.datapack.annotations.AnnotationsHelper.serializeAs;

public class CollectionSerializationFactory<E> extends SerializationFactory<E> {

    public static final CollectionSerializationFactory INSTANCE = new CollectionSerializationFactory();

    private CollectionSerializationFactory() {
        super("_C");
    }

    @Override
    public TypeDescriptor getDefaultType() {
        return new TypeDescriptor(ArrayList.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return type.getType().isArray() || Collection.class.isAssignableFrom(type.getType());
    }

    @Override
    public <T extends E> Serializer<T> createSerializer(DataWriter os, TypeDescriptor<T> type) throws IOException {
        if (type.getType().isArray()) {
            SerializeValueBy valueDeclared = type.getAnnotation(SerializeValueBy.class);

            TypeDescriptor valueType = new TypeDescriptor(serializeAs(valueDeclared, type.getType().getComponentType()),
                    annotationsFrom(valueDeclared, type.getAnnotations()));

            boolean isSpecifiedType = serializeAs(valueDeclared, null) != null || valueType.isFinal();
            SerializationFactory valueFactory = valueDeclared != null ? getInstance(valueDeclared.value()) : getSerializer(os, valueType, isSpecifiedType);

            return (Serializer<T>) new ArraySerializer(os, valueFactory, valueType, allowReordering(type, false));
        } else {
            SerializeValueBy valueDeclared = type.getAnnotation(SerializeValueBy.class);

            TypeDescriptor valueType = new TypeDescriptor(serializeAs(valueDeclared, type.getParametrizedType(0)),
                    annotationsFrom(valueDeclared, type.getAnnotations()));

            boolean isSpecifiedType = serializeAs(valueDeclared, null) != null || valueType.isFinal();
            SerializationFactory valueFactory = valueDeclared != null ? getInstance(valueDeclared.value()) : getSerializer(os, valueType, isSpecifiedType);

            boolean isOrdered = !Set.class.isAssignableFrom(type.getType()) || LinkedHashSet.class.isAssignableFrom(type.getType());
            boolean allowReordering = allowReordering(type, !isOrdered);

            return new CollectionSerializer(os, valueFactory, valueType, allowReordering);
        }
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        Deserializer<Object> valueDeserializer = is.createAndRegisterDeserializer();
        return new Deserializer<Object>() {
            @Override
            public <T> T deserialize(TypeDescriptor<T> type) throws IOException {
                if (type.getAnnotation(AsJson.class) != null) {
                    return (T) new JsonCollectionDeserializer(is, valueDeserializer).deserialize(type);
                } else if (type.getType().isArray()) {
                    return (T) new ArrayDeserializer(is, valueDeserializer).deserialize(type);
                } else {
                    return (T) new CollectionDeserializer<>(is, valueDeserializer).deserialize(type);
                }
            }
        };
    }

    private <T> SerializationFactory<T> getSerializer(DataWriter os, TypeDescriptor<T> type, boolean isSpecifiedType) throws IOException {
        return os.getSerializationFactoryLookup().getSerializationFactory(type, isSpecifiedType);
    }
}
