package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.numbers.UnsignedLongSerializer;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class MultiGzipDataWriter extends DataWriter {

    public static final long CURRENT_VERSION = 0;

    private SerializationFactoryLookup serializationFactoryLookup;

    private Map<TypeDescriptor, Integer> typeToId = new HashMap<>();
    private List<Serializer> registeredSerializers = new ArrayList<>();

    private OutputStream outputStream;
    private List<DataWriter.Wrapper> dataWriters = new ArrayList<>();

    public MultiGzipDataWriter(OutputStream os) throws IOException {
        this(os, new SerializationFactoryLookup());
    }

    public MultiGzipDataWriter(OutputStream os, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
        super(new ByteArrayOutputWrapper());
        this.serializationFactoryLookup = serializationFactoryLookup;
        writeGlobalSettings();
        outputStream = os;
    }

    private void writeGlobalSettings() throws IOException {
        writeUnsignedLong(CURRENT_VERSION);
        writeUnsignedLong(0L); // there will be number of settings here
    }
    
    public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
        if (o == null) {
            writeUnsignedLong(null);
            return;
        }
        Serializer<T> serializer = getOrCreateSerializer(type);
        serializer.serialize(o);
    }

    private <T> Serializer<T> getOrCreateSerializer(TypeDescriptor<T> type) throws IOException {
        Integer id = typeToId.getOrDefault(type, registeredSerializers.size());
        writeUnsignedLong(Long.valueOf(id));
        if (id >= registeredSerializers.size()) {
            SerializationFactory factory = serializationFactoryLookup.getSerializationFactory(type);
            if (factory == null) {
                factory = ObjectSerializationFactory.INSTANCE;
            }
            Serializer serializer = createAndRegisterSerializer(factory, type);
            typeToId.put(type, id);
            registeredSerializers.add(serializer);
        }
        return registeredSerializers.get(id);
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return serializationFactoryLookup;
    }

    public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        if (factory instanceof GloballyDefined && typeToId.containsKey(type)) {
            return registeredSerializers.get(typeToId.get(type));
        }
        writeCachedString(factory.getName());
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

        DataWriter.Wrapper headerWriter = new DataWriter.Wrapper(outputStream, null);
        UnsignedLongSerializer longSerializer = new UnsignedLongSerializer(headerWriter);
        longSerializer.serialize((long) arrays.length);
        for (byte[] array : arrays) {
            longSerializer.serialize((long) array.length);
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
