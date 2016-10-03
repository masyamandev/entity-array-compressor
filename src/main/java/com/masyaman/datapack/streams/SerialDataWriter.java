package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedSerializer;
import com.masyaman.datapack.serializers.numbers.LongSerializer;
import com.masyaman.datapack.serializers.numbers.UnsignedLongSerializer;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializer;

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
        Integer id = typeToId.getOrDefault(type, registeredSerializers.size());
        writeUnsignedLong(Long.valueOf(id));
        if (id >= registeredSerializers.size()) {
            SerializationFactory factory = serializationFactoryLookup.getSerializationFactory(type);
            if (factory == null) {
                factory = ObjectSerializationFactory.INSTANCE;
            }
            Serializer serializer = createAndRegisterSerializer(factory, type);
            typeToId.put(type, id);
            registeredSerializers.add(serializer);
        }
        return registeredSerializers.get(id);
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return serializationFactoryLookup;
    }

    public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        if (factory instanceof GloballyDefined && typeToId.containsKey(type)) {
            return registeredSerializers.get(typeToId.get(type));
        }
        writeCachedString(factory.getName());
        Serializer serializer = factory.createSerializer(this, type);
        return serializer;
    }
}
