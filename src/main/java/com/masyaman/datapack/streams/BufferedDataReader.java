package com.masyaman.datapack.streams;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.primitives.UnsignedLongReader;
import com.masyaman.datapack.settings.SettingsHandler;
import com.masyaman.datapack.utils.MultipleByteInputStreamHandler;

import java.io.IOException;
import java.io.InputStream;

import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;

/**
 * Reader for {@link BufferedDataWriter}.
 */
public class BufferedDataReader extends DataReader.Abstract {

    private MultipleByteInputStreamHandler streamHandler;
    private UnsignedLongReader settingsReader;

    public BufferedDataReader(InputStream is) throws IOException {
        this(is, SettingsHandler.DEFAULTS);
    }

    public BufferedDataReader(InputStream is, SettingsHandler settings) throws IOException {
        this(is, new MultipleByteInputStreamHandler(), settings);
        this.settingsReader = new UnsignedLongReader(is);
        readGlobalSettings();
    }

    private BufferedDataReader(InputStream is, MultipleByteInputStreamHandler streams, SettingsHandler settings) throws IOException {
        super(streams.newStream(), settings);
        this.streamHandler = streams;
        streams.setEmptyBufferCallback(new MultipleByteInputStreamHandler.EmptyBufferCallback() {
            @Override
            public boolean readBuffer() {
                try {
                    Long id = settingsReader.deserialize(LONG);
                    if (id < 0) {
                        return false;
                    }
                    Long len = settingsReader.deserialize(LONG);
                    streams.readBuffer(id.intValue(), len.intValue(), is);
                    return true;
                } catch (IOException e) {
                    // TODO rethrow RuntimeException?
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    private void readGlobalSettings() throws IOException {
        Long version = settingsReader.deserialize(LONG);
        if (version == null || version.longValue() != BufferedDataWriter.CURRENT_VERSION) {
            throw new IOException("Version " + version + " is not supported!");
        }
        Long settingsNumber = settingsReader.deserialize(LONG);
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
        DataReader dr = new Wrapper(streamHandler.newStream(), this);
        return serializationFactory.createDeserializer(dr);
    }
}
