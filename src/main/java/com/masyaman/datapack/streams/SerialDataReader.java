package com.masyaman.datapack.streams;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.io.IOException;
import java.io.InputStream;

public class SerialDataReader extends DataReader.Abstract {

    public SerialDataReader(InputStream is) throws IOException {
        this(is, new ClassManager());
    }

    public SerialDataReader(InputStream is, ClassManager classManager) throws IOException {
        this(is, classManager, new SerializationFactoryLookup());
    }

    public SerialDataReader(InputStream is, ClassManager classManager, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        super(is, classManager, serializationFactoryLookup);
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

    @Override
    protected <E> Deserializer<E> readDeserializer() throws IOException {
        String name = readString();
        SerializationFactory serializationFactory = serializationFactoryLookup.getByName(name);
        if (serializationFactory == null) {
            throw new IOException("Unable to find serialization factory '" + name + "'");
        }
        return serializationFactory.createDeserializer(this);
    }
}
