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
import java.util.*;

public class DataWriter {

    public static final long CURRENT_VERSION = 0;

    private OutputStream os;

    private SerializationFactoryLookup serializationFactoryLookup;

    private Serializer<Long> signedLongSerializer;
    private Serializer<Long> unsignedLongSerializer;
    private Serializer<String> stringSerializer;
    private Serializer<String> stringCachedSerializer;

    private Map<TypeDescriptor, Integer> typeToId = new HashMap<>();
    private List<Serializer> registeredSerializers = new ArrayList<>();

    public DataWriter(OutputStream os) throws IOException {
        this(os, new SerializationFactoryLookup());
    }

    public DataWriter(OutputStream os, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        this.os = os;
        this.serializationFactoryLookup = serializationFactoryLookup;

        signedLongSerializer = new LongSerializer(this);//SignedLongSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(Long.class));
        unsignedLongSerializer = new UnsignedLongSerializer(this);//SignedLongSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(Long.class));
        stringSerializer = new StringSerializer(this);//StringSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(String.class));
        stringCachedSerializer = new SimpleCachedSerializer(this, stringSerializer);//StringCachedSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(String.class));

        writeGlobalSettings();
    }

    private void writeGlobalSettings() throws IOException {
        unsignedLongSerializer.serialize(CURRENT_VERSION);
        unsignedLongSerializer.serialize(0L); // there will be number of settings here
    }

    public void writeByte(byte b) throws IOException {
        writeByte((int) b);
    }

    public void writeByte(int b) throws IOException {
        os.write(b);
    }

    public void writeByte(long b) throws IOException {
        writeByte((int) b);
    }

    public void writeSignedLong(Long l) throws IOException {
        signedLongSerializer.serialize(l);
    }

    public void writeUnsignedLong(Long l) throws IOException {
        unsignedLongSerializer.serialize(l);
    }

    public void writeString(String s) throws IOException {
        stringSerializer.serialize(s);
    }

    public void writeCachedString(String s) throws IOException {
        stringCachedSerializer.serialize(s);
    }


    public <T> void writeObject(T o) throws IOException {
        writeObject(o, o == null ? null : new TypeDescriptor<T>(o.getClass()));
    }

    public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
        if (o == null) {
            unsignedLongSerializer.serialize(null);
            return;
        }
        Serializer<T> serializer = getOrCreateSerializer(type);
        serializer.serialize(o);
    }

    private <T> Serializer<T> getOrCreateSerializer(TypeDescriptor<T> type) throws IOException {
        Integer id = typeToId.getOrDefault(type, registeredSerializers.size());
        unsignedLongSerializer.serialize(Long.valueOf(id));
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
