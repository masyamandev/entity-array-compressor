package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;

import java.io.IOException;
import java.io.OutputStream;

public class SerialDataWriter extends DataWriter.Abstract {

    public static final long CURRENT_VERSION = 0;

    public SerialDataWriter(OutputStream os) throws IOException {
        this(os, new SerializationFactoryLookup());
    }

    public SerialDataWriter(OutputStream os, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        super(os);
        this.serializationFactoryLookup = serializationFactoryLookup;
        writeGlobalSettings();
    }

    private void writeGlobalSettings() throws IOException {
        writeUnsignedLong(CURRENT_VERSION);
        writeUnsignedLong(0L); // there will be number of settings here
    }

    @Override
    protected <E> Serializer<E> writeSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        writeCachedString(factory.getName());
        Serializer serializer = factory.createSerializer(this, type);
        return serializer;
    }
}
