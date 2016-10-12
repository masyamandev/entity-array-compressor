package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerialDataWriter extends DataWriter {

    public static final long CURRENT_VERSION = 0;

    private SerializationFactoryLookup serializationFactoryLookup;

    private Map<TypeDescriptor, Integer> typeToId = new HashMap<>();
    private List<Serializer> registeredSerializers = new ArrayList<>();

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
    
    public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
        if (o == null) {
            writeUnsignedLong(null);
            return;
        }
        Serializer<T> serializer = getOrCreateSerializer(type);
        serializer.serialize(o);
    }

    private <T> Serializer<T> getOrCreateSerializer(TypeDescriptor<T> type) throws IOException {
        Integer id = typeToId.getOrDefault(type, 0);
        writeUnsignedLong(Long.valueOf(id));
        if (id <= 0) {
            SerializationFactory factory = serializationFactoryLookup.getSerializationFactory(type);
            if (factory == null) {
                factory = ObjectSerializationFactory.INSTANCE;
            }
            Serializer serializer = writeSerializer(factory, type);
            typeToId.put(type, typeToId.size() + 1);
            registeredSerializers.add(serializer);
            return serializer;
        } else {
            return registeredSerializers.get(id - 1);
        }
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return serializationFactoryLookup;
    }

    public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        writeUnsignedLong(null);
        return writeSerializer(factory, type);
    }

    private <E> Serializer<E> writeSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        writeCachedString(factory.getName());
        Serializer serializer = factory.createSerializer(this, type);
        return serializer;
    }
}
