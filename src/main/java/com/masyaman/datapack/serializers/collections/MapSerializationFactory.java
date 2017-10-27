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
        return TypeDescriptor.HASH_MAP;
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

        Serializer keySerializer = os.createAndRegisterSerializer(keyFactory, keyType);
        Serializer valueSerializer = os.createAndRegisterSerializer(valueFactory, valueType);

        return new MapSerializer(os, keySerializer, valueSerializer, allowReordering);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        Deserializer<Object> keyDeserializer = is.createAndRegisterDeserializer();
        Deserializer<Object> valueDeserializer = is.createAndRegisterDeserializer();
        return new Deserializer<Object>() {
            @Override
            public <T> T deserialize(TypeDescriptor<T> type) throws IOException {
                if (type.getAnnotation(AsJson.class) != null) {
                    return (T) new JsonMapDeserializer(is, keyDeserializer, valueDeserializer).deserialize(type);
                } else {
                    return (T) new MapDeserializer(is, keyDeserializer, valueDeserializer).deserialize(type);
                }
            }
        };
    }

    private <T> SerializationFactory<T> getSerializer(DataWriter os, TypeDescriptor<T> type, boolean isSpecifiedType) throws IOException {
        return os.getSerializationFactoryLookup().getSerializationFactory(type, isSpecifiedType);
    }
}
