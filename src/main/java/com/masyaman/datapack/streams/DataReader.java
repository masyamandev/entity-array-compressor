package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.caching.SimpleCachedDeserializer;
import com.masyaman.datapack.serializers.numbers.LongDeserializer;
import com.masyaman.datapack.serializers.numbers.UnsignedLongDeserializer;
import com.masyaman.datapack.serializers.strings.StringDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    private InputStream is;

    private SerializationFactoryLookup serializationFactoryLookup;

    private Deserializer<Long> signedLongDeserializer;
    private Deserializer<Long> unsignedLongDeserializer;
    private Deserializer<String> stringDeserializer;
    private Deserializer<String> stringCachedDeserializer;

    private List<Deserializer> registeredDeserializers = new ArrayList<>();

    public DataReader(InputStream is) throws IOException {
        this(is, new SerializationFactoryLookup());
    }

    public DataReader(InputStream is, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        this.is = is;
        this.serializationFactoryLookup = serializationFactoryLookup;
        
        signedLongDeserializer = new LongDeserializer(this);//SignedLongSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(Long.class));
        unsignedLongDeserializer = new UnsignedLongDeserializer(this);//SignedLongSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(Long.class));
        stringDeserializer = new StringDeserializer(this);//StringSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(String.class));
        stringCachedDeserializer = new SimpleCachedDeserializer(this, stringDeserializer);//StringCachedSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(String.class));

        readGlobalSettings();
    }

    private void readGlobalSettings() throws IOException {
        Long version = unsignedLongDeserializer.deserialize();
        if (version == null || version.longValue() != DataWriter.CURRENT_VERSION) {
            throw new IOException("Version " + version + " is not supported!");
        }
        Long settingsNumber = unsignedLongDeserializer.deserialize();
        if (settingsNumber == null || settingsNumber.longValue() != 0) {
            throw new IOException("Settings are not supported!");
        }
    }

    public byte readByte() throws IOException {
        return (byte) is.read();
    }

    public int readUnsignedByte() throws IOException {
        return is.read();
    }
    
    public Long readSignedLong() throws IOException {
        return signedLongDeserializer.deserialize();
    }

    public Long readUnsignedLong() throws IOException {
        return unsignedLongDeserializer.deserialize();
    }

    public String readString() throws IOException {
        return stringDeserializer.deserialize();
    }

    public String readCachedString() throws IOException {
        return stringCachedDeserializer.deserialize();
    }

    public Object readObject() throws IOException {
        return readObject(null);
    }

    public <T> T readObject(TypeDescriptor<T> type) throws IOException {
        Long id = readUnsignedLong();
        if (id == null) {
            return null;
        }
        if (registeredDeserializers.size() <= id) {
//            String name = readCachedString();
//            SerializationFactory serializationFactory = serializationFactoryLookup.getByName(name);
//            if (type == null) {
//                type = serializationFactory.getDefaultType();
//            }
//            Deserializer deserializer = serializationFactory.createDeserializer(this, type);
            registeredDeserializers.add(createAndRegisterDeserializer(type));
        }
        return (T) registeredDeserializers.get(id.intValue()).deserialize();
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return serializationFactoryLookup;
    }

    public <E> Deserializer<E> createAndRegisterDeserializer(TypeDescriptor<E> type) throws IOException {
        String name = readCachedString();
        SerializationFactory serializationFactory = serializationFactoryLookup.getByName(name);
        if (serializationFactory == null) {
            throw new IOException("Unable to find serialization factory '" + name + "'");
        }
//        if (type == null) {
//            type = serializationFactory.getDefaultType();
//        }
        return serializationFactory.createDeserializer(this, type);
    }
}
