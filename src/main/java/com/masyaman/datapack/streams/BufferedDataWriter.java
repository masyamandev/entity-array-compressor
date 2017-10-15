package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.primitives.UnsignedLongWriter;
import com.masyaman.datapack.utils.Constants;
import com.masyaman.datapack.utils.MultipleByteOutputStreamHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

    public BufferedDataWriter(OutputStream os) throws IOException {
        this(os, new ClassManager());
    }

    public BufferedDataWriter(OutputStream os, ClassManager classManager) throws IOException {
        this(os, classManager, new SerializationFactoryLookup());
    }

    public BufferedDataWriter(OutputStream os, ClassManager classManager, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        this(os, new MultipleByteOutputStreamHandler(), classManager, serializationFactoryLookup);
    }

    public BufferedDataWriter(OutputStream os, MultipleByteOutputStreamHandler streamHandler, ClassManager classManager, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        super(streamHandler.newStream(), classManager, serializationFactoryLookup);
        this.serializationFactoryLookup = serializationFactoryLookup;
        this.outputStream = os;
        this.streamHandler = streamHandler;
        this.settingsWriter = new UnsignedLongWriter(os);
        writeGlobalSettings();
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
        if (streamHandler.getCount() > Constants.BYTE_BUFFER_SIZE) {
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
