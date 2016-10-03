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
