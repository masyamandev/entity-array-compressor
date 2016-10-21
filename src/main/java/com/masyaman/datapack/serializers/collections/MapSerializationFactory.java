package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.serialization.SerializeKeyBy;
import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.masyaman.datapack.annotations.AnnotationsHelper.*;

public class MapSerializationFactory<E> extends SerializationFactory<E> {

    public static final MapSerializationFactory INSTANCE = new MapSerializationFactory();

    private MapSerializationFactory() {
        super("_M");
    }

    @Override
    public TypeDescriptor getDefaultType() {
        return new TypeDescriptor(HashMap.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Map.class.isAssignableFrom(type.getType());
    }

    @Override
    public <T extends E> Serializer<T> createSerializer(DataWriter os, TypeDescriptor<T> type) throws IOException {
        SerializeKeyBy keyDeclared = type.getAnnotation(SerializeKeyBy.class);
        SerializeValueBy valueDeclared = type.getAnnotation(SerializeValueBy.class);

        TypeDescriptor keyType = new TypeDescriptor(serializeAs(keyDeclared, type.getParametrizedType(0)),
                annotationsFrom(keyDeclared, type.getAnnotations()));
        TypeDescriptor valueType = new TypeDescriptor(serializeAs(valueDeclared, type.getParametrizedType(1)),
                annotationsFrom(valueDeclared, type.getAnnotations()));

        boolean isSpecifiedKeyType = serializeAs(keyDeclared, null) != null || keyType.isFinal();
        boolean isSpecifiedValueType = serializeAs(valueDeclared, null) != null || valueType.isFinal();

        SerializationFactory keyFactory = keyDeclared != null ? getInstance(keyDeclared.value()) : getSerializer(os, keyType, isSpecifiedKeyType);
        SerializationFactory valueFactory = valueDeclared != null ? getInstance(valueDeclared.value()) : getSerializer(os, valueType, isSpecifiedValueType);

        boolean isOrderedMap = LinkedHashMap.class.isAssignableFrom(type.getType());
        boolean allowReordering = allowReordering(type, !isOrderedMap);

        return new MapSerializer(os, keyFactory, keyType, valueFactory, valueType, allowReordering);
    }

    @Override
    public <T> Deserializer<T> createDeserializer(DataReader is, TypeDescriptor<T> type) throws IOException {
        if (type.getAnnotation(AsJson.class) != null) {
            return (Deserializer<T>) new JsonMapDeserializer(is, type);
        } else {
            return new MapDeserializer(is, type, new TypeDescriptor(type.getParametrizedType(0)), new TypeDescriptor(type.getParametrizedType(1)));
        }
    }

    private <T> SerializationFactory<T> getSerializer(DataWriter os, TypeDescriptor<T> type, boolean isSpecifiedType) throws IOException {
        return os.getSerializationFactoryLookup().getSerializationFactory(type, isSpecifiedType);
    }
}
