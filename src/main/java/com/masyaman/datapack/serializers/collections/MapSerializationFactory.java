package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.SerializeKeyBy;
import com.masyaman.datapack.annotations.SerializeValueBy;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapSerializationFactory extends SerializationFactory {

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
    public Serializer createSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        TypeDescriptor keyType = new TypeDescriptor(type.getParametrizedType(0), type.getAnnotations());
        TypeDescriptor valueType = new TypeDescriptor(type.getParametrizedType(1), type.getAnnotations());

        // TODO remove (SerializeKeyBy)
        SerializeKeyBy keyDeclared = (SerializeKeyBy) type.getAnnotation(SerializeKeyBy.class);
        SerializeValueBy valueDeclared = (SerializeValueBy) type.getAnnotation(SerializeValueBy.class);

        SerializationFactory keyFactory = keyDeclared != null ? getInstance(keyDeclared.value()) : getSerializer(os, keyType);
        SerializationFactory valueFactory = valueDeclared != null ? getInstance(valueDeclared.value()) : getSerializer(os, valueType);

        return new MapSerializer(os, keyFactory, keyType, valueFactory, valueType);
    }


    @Override
    public Deserializer createDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        return new MapDeserializer(is, type, new TypeDescriptor(type.getParametrizedType(0)), new TypeDescriptor(type.getParametrizedType(1))); // TODO
    }

    private <T> SerializationFactory<T> getSerializer(DataWriter os, TypeDescriptor<T> type) {
        SerializationFactory serializationFactory = os.getSerializationFactoryLookup().getSerializationFactory(type);
        if (serializationFactory == null) {
            serializationFactory = UnknownTypeSerializationFactory.INSTANCE;
        }
        return serializationFactory;
    }
}
