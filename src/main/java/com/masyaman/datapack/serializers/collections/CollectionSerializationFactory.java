package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
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

            SerializationFactory valueFactory = valueDeclared != null ? getInstance(valueDeclared.value()) : getSerializer(os, valueType);

            return (Serializer<T>) new ArraySerializer(os, valueFactory, valueType, allowReordering(type, false));
        } else {
            SerializeValueBy valueDeclared = type.getAnnotation(SerializeValueBy.class);

            TypeDescriptor valueType = new TypeDescriptor(serializeAs(valueDeclared, type.getParametrizedType(0)),
                    annotationsFrom(valueDeclared, type.getAnnotations()));

            SerializationFactory valueFactory = valueDeclared != null ? getInstance(valueDeclared.value()) : getSerializer(os, valueType);

            boolean isOrdered = !Set.class.isAssignableFrom(type.getType()) || LinkedHashSet.class.isAssignableFrom(type.getType());
            boolean allowReordering = allowReordering(type, !isOrdered);

            return new CollectionSerializer(os, valueFactory, valueType, allowReordering);
        }
    }

    @Override
    public <T> Deserializer<T> createDeserializer(DataReader is, TypeDescriptor<T> type) throws IOException {
        if (type.getAnnotation(AsJson.class) != null) {
            return (Deserializer<T>) new JsonCollectionDeserializer(is, type);
        } else if (type.getType().isArray()) {
            return (Deserializer<T>) new ArrayDeserializer(is, new TypeDescriptor(type.getType().getComponentType()));
        } else {
            return new CollectionDeserializer<>(is, type, new TypeDescriptor(type.getParametrizedType(0)));
        }
    }

    private <T> SerializationFactory<T> getSerializer(DataWriter os, TypeDescriptor<T> type) {
        SerializationFactory serializationFactory = os.getSerializationFactoryLookup().getSerializationFactory(type);
        if (serializationFactory == null) {
            serializationFactory = UnknownTypeSerializationFactory.INSTANCE;
        }
        return serializationFactory;
    }
}
