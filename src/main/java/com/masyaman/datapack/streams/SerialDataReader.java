package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SerialDataReader extends DataReader {

    private SerializationFactoryLookup serializationFactoryLookup;
    
    private List<Deserializer> registeredDeserializers = new ArrayList<>();

    public SerialDataReader(InputStream is) throws IOException {
        this(is, new SerializationFactoryLookup());
    }

    public SerialDataReader(InputStream is, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        super(is);
        this.serializationFactoryLookup = serializationFactoryLookup;
        readGlobalSettings();
    }

    private void readGlobalSettings() throws IOException {
        Long version = readUnsignedLong();
        if (version == null || version.longValue() != SerialDataWriter.CURRENT_VERSION) {
            throw new IOException("Version " + version + " is not supported!");
        }
        Long settingsNumber = readUnsignedLong();
        if (settingsNumber == null || settingsNumber.longValue() != 0) {
            throw new IOException("Settings are not supported!");
        }
    }

    public <T> T readObject(TypeDescriptor<T> type) throws IOException {
        Long id = readUnsignedLong();
        if (id == null) {
            return null;
        }
        if (id <= 0) {
            Deserializer<T> deserializer = readDeserializer(type);
            registeredDeserializers.add(deserializer);
            return deserializer.deserialize();
        } else {
            return (T) registeredDeserializers.get(id.intValue() - 1).deserialize();
        }
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return serializationFactoryLookup;
    }

    public <E> Deserializer<E> createAndRegisterDeserializer(TypeDescriptor<E> type) throws IOException {
        Long id = readUnsignedLong();
        if (id == null) {
            return readDeserializer(type);
        } else if (id <= 0) {
            Deserializer deserializer = readDeserializer(type);
            registeredDeserializers.add(deserializer);
            return deserializer;
        } else {
            return registeredDeserializers.get(id.intValue() - 1);
        }
    }

    private <E> Deserializer<E> readDeserializer(TypeDescriptor<E> type) throws IOException {
        String name = readCachedString();
        SerializationFactory serializationFactory = serializationFactoryLookup.getByName(name);
        if (serializationFactory == null) {
            throw new IOException("Unable to find serialization factory '" + name + "'");
        }
        return serializationFactory.createDeserializer(this, type);
    }
}
