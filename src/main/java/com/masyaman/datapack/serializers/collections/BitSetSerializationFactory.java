package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.BitSet;

public class BitSetSerializationFactory extends SerializationFactory {

    public static final BitSetSerializationFactory INSTANCE = new BitSetSerializationFactory();

    private BitSetSerializationFactory() {
        super("_BS");
    }

    @Override
    public TypeDescriptor getDefaultType() {
        return new TypeDescriptor(BitSet.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return BitSet.class.isAssignableFrom(type.getType());
    }

    @Override
    public Serializer createSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        return new BitSetSerializer(os);
    }

    @Override
    public Deserializer createDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        return new BitSetDeserializer(is);
    }
}
