package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.primitives.UnsignedLongWriter;
import com.masyaman.datapack.settings.SettingsHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Serialize objects into a multiple gzipped data streams.
 * As each column is gzipped separately, this serializer has best compression. However it requires memory for whole
 * compressed output before it can be written.
 * It's experimental version and may be used only for testing compression.
 */
public class MultiGzipDataWriter extends DataWriter.Abstract {

    public static final long CURRENT_VERSION = 0;

    private OutputStream outputStream;
    private List<DataWriter.Wrapper> dataWriters = new ArrayList<>();

    public MultiGzipDataWriter(OutputStream os) throws IOException {
        this(os, SettingsHandler.DEFAULTS);
    }

    public MultiGzipDataWriter(OutputStream os, SettingsHandler settings) throws IOException {
        super(new ByteArrayOutputWrapper(), settings);
        writeGlobalSettings();
        outputStream = os;
    }

    private void writeGlobalSettings() throws IOException {
        writeUnsignedLong(CURRENT_VERSION);
        writeUnsignedLong(0L); // there will be number of settings here
    }

    @Override
    protected  <E> Serializer<E> writeSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        writeString(factory.getName());
        DataWriter.Wrapper dataWriter = new DataWriter.Wrapper(new ByteArrayOutputWrapper(), this);
        dataWriters.add(dataWriter);
        Serializer serializer = factory.createSerializer(dataWriter, type);
        return serializer;
    }

    @Override
    public void close() throws IOException {

        byte[][] arrays = new byte[dataWriters.size() + 1][];

        os.close();
        arrays[0] = ((ByteArrayOutputWrapper) os).toByteArray();

        int i = 1;
        for (DataWriter.Wrapper dataWriter : dataWriters) {
            dataWriter.close();
            arrays[i++] = ((ByteArrayOutputWrapper) dataWriter.os).toByteArray();
        }

        UnsignedLongWriter lengthWriter = new UnsignedLongWriter(outputStream);
        lengthWriter.serialize((long) arrays.length);
        for (byte[] array : arrays) {
            lengthWriter.serialize((long) array.length);
        }

        for (byte[] array : arrays) {
            outputStream.write(array);
        }

        outputStream.close();

        super.close();
    }

    //public static class ByteArrayOutputWrapper extends ByteArrayOutputStream {}
    public static class ByteArrayOutputWrapper extends GZIPOutputStream {

        private ByteArrayOutputStream byteArrayOutputStream;


        private ByteArrayOutputWrapper() throws IOException {
            this(new ByteArrayOutputStream());
        }

        private ByteArrayOutputWrapper(ByteArrayOutputStream out) throws IOException {
            super(out);
            byteArrayOutputStream = out;
        }

        public byte[] toByteArray() throws IOException {
            close();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
