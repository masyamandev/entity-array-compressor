package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.primitives.UnsignedLongWriter;
import com.masyaman.datapack.settings.SettingsHandler;
import com.masyaman.datapack.utils.MultipleByteOutputStreamHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.masyaman.datapack.settings.SettingsKeys.BYTE_BUFFER_SIZE;

/**
 * Serialize objects into a several data streams. Data is buffered and flushed when size limit exceeds.
 * This method produces output which usually compresses better than in {@link SerialDataWriter}.
 */
public class BufferedDataWriter extends DataWriter.Abstract {

    public static final long CURRENT_VERSION = 0;

    private OutputStream outputStream;
    private List<Wrapper> dataWriters = new ArrayList<>();
    private MultipleByteOutputStreamHandler streamHandler;
    private UnsignedLongWriter settingsWriter;
    private int bufferSize;

    public BufferedDataWriter(OutputStream os) throws IOException {
        this(os, SettingsHandler.DEFAULTS);
    }

    public BufferedDataWriter(OutputStream os, SettingsHandler settings) throws IOException {
        this(os, new MultipleByteOutputStreamHandler(), settings);
    }

    public BufferedDataWriter(OutputStream os, MultipleByteOutputStreamHandler streams, SettingsHandler settings) throws IOException {
        super(streams.newStream(), settings);
        this.outputStream = os;
        this.streamHandler = streams;
        this.settingsWriter = new UnsignedLongWriter(os);
        writeGlobalSettings();
        bufferSize = settings.get(BYTE_BUFFER_SIZE);
    }

    private void writeGlobalSettings() throws IOException {
        settingsWriter.serialize(CURRENT_VERSION);
        settingsWriter.serialize(0L); // there will be number of settings here
    }

    @Override
    protected  <E> Serializer<E> writeSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        writeString(factory.getName());
        Wrapper dataWriter = new Wrapper(streamHandler.newStream(), this);
        dataWriters.add(dataWriter);
        Serializer serializer = factory.createSerializer(dataWriter, type);
        return serializer;
    }

    @Override
    public void close() throws IOException {
        flush(true);
        outputStream.close();
        super.close();
    }

    @Override
    public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
        super.writeObject(o, type);
        if (streamHandler.getCount() > bufferSize) {
            flush(false);
        }
    }

    private void flush(boolean close) throws IOException {
        for (int i = 0; i < streamHandler.getStreams().size(); i++) {
            MultipleByteOutputStreamHandler.ByteArrayOutputWrapper stream = streamHandler.getStreams().get(i);
            stream.flush();
            if (close) {
                stream.close();
            }
            if (stream.getCount() > 0) {
                settingsWriter.serialize((long) i);
                settingsWriter.serialize((long) stream.getCount());
                stream.writeTo(outputStream);
            }
        }
    }
}
