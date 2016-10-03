package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class MultiGzipDataReader extends DataReader {

    private SerializationFactoryLookup serializationFactoryLookup;

    private List<Deserializer> registeredDeserializers = new ArrayList<>();

    private List<InputStream> streams;

    public MultiGzipDataReader(InputStream is) throws IOException {
        this(is, new SerializationFactoryLookup());
    }

    public MultiGzipDataReader(InputStream is, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        this(splitStreams(is));
        this.serializationFactoryLookup = serializationFactoryLookup;
        readGlobalSettings();
    }

    private MultiGzipDataReader(List<InputStream> streams) throws IOException {
        super(streams.remove(0));
        this.streams = streams;
    }

    private static List<InputStream> splitStreams(InputStream is) throws IOException {
        // Unoptimized code
        DataReader.Wrapper dataReader = new Wrapper(is, null);

        int streams = dataReader.readUnsignedLong().intValue();
        int[] lengths = new int[streams];
        for (int i = 0; i < streams; i++) {
            lengths[i] = dataReader.readUnsignedLong().intValue();
        }

        List<InputStream> dataStreams = new LinkedList<>();
        for (int i = 0; i < streams; i++) {
            byte[] streamData = new byte[lengths[i]];
            is.read(streamData); // TODO: check
            dataStreams.add(new GZIPInputStream(new ByteArrayInputStream(streamData)));
        }

        return dataStreams;
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
        DataReader dr = new DataReader.Wrapper(streams.remove(0), this);
        return serializationFactory.createDeserializer(dr, type);
    }
}