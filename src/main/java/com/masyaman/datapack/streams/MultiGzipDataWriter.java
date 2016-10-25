package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.primitives.UnsignedLongWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

// Experimental version of column-based gzipped storage
public class MultiGzipDataWriter extends DataWriter.Abstract {

    public static final long CURRENT_VERSION = 0;

    private OutputStream outputStream;
    private List<DataWriter.Wrapper> dataWriters = new ArrayList<>();

    public MultiGzipDataWriter(OutputStream os) throws IOException {
        this(os, new ClassManager());
    }

    public MultiGzipDataWriter(OutputStream os, ClassManager classManager) throws IOException {
        this(os, classManager, new SerializationFactoryLookup());
    }

    public MultiGzipDataWriter(OutputStream os, ClassManager classManager, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        super(new ByteArrayOutputWrapper(), classManager, serializationFactoryLookup);
        this.serializationFactoryLookup = serializationFactoryLookup;
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
