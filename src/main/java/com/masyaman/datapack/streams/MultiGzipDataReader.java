package com.masyaman.datapack.streams;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.primitives.UnsignedLongReader;
import com.masyaman.datapack.settings.SettingsHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;

/**
 * Reader for {@link MultiGzipDataWriter}.
 * It's experimental version and may be used only for testing compression.
 */
public class MultiGzipDataReader extends DataReader.Abstract {

    private List<InputStream> streams;

    public MultiGzipDataReader(InputStream is) throws IOException {
        this(is, SettingsHandler.DEFAULTS);
    }

    public MultiGzipDataReader(InputStream is, SettingsHandler settings) throws IOException {
        this(splitStreams(is), settings);
        readGlobalSettings();
    }

    private MultiGzipDataReader(List<InputStream> streams, SettingsHandler settings) throws IOException {
        super(streams.remove(0), settings);
        this.streams = streams;
    }

    private static List<InputStream> splitStreams(InputStream is) throws IOException {
        // Unoptimized code
        UnsignedLongReader lengthReader = new UnsignedLongReader(is);

        int streams = lengthReader.deserialize(LONG).intValue();
        int[] lengths = new int[streams];
        for (int i = 0; i < streams; i++) {
            lengths[i] = lengthReader.deserialize(LONG).intValue();
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
        if (version == null || version.longValue() != MultiGzipDataWriter.CURRENT_VERSION) {
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
        DataReader dr = new DataReader.Wrapper(streams.remove(0), this);
        return serializationFactory.createDeserializer(dr);
    }
}
