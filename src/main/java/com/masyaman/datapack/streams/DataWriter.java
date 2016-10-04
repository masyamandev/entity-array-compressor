package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedSerializer;
import com.masyaman.datapack.serializers.primitives.SignedLongWriter;
import com.masyaman.datapack.serializers.primitives.StringWriter;
import com.masyaman.datapack.serializers.primitives.UnsignedLongWriter;
import com.masyaman.datapack.serializers.strings.StringSerializer;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DataWriter implements ObjectWriter {

    protected OutputStream os;

    private Serializer<Long> signedLongSerializer;
    private Serializer<Long> unsignedLongSerializer;
    private Serializer<String> stringSerializer;
    private Serializer<String> stringCachedSerializer;

    public DataWriter(OutputStream os) throws IOException {
        this.os = os;

        signedLongSerializer = new SignedLongWriter(os);
        unsignedLongSerializer = new UnsignedLongWriter(os);
        stringSerializer = new StringWriter(os, unsignedLongSerializer);
        stringCachedSerializer = new SimpleCachedSerializer(this, stringSerializer);
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

    public void writeByte(byte b) throws IOException {
        writeByte((int) b);
    }

    public void writeByte(int b) throws IOException {
        os.write(b);
    }

    public void writeByte(long b) throws IOException {
        writeByte((int) b);
    }

    public void writeSignedLong(Long l) throws IOException {
        signedLongSerializer.serialize(l);
    }

    public void writeUnsignedLong(Long l) throws IOException {
        unsignedLongSerializer.serialize(l);
    }

    public void writeString(String s) throws IOException {
        stringSerializer.serialize(s);
    }

    public void writeCachedString(String s) throws IOException {
        stringCachedSerializer.serialize(s);
    }


    public <T> void writeObject(T o) throws IOException {
        writeObject(o, o == null ? null : new TypeDescriptor<T>(o.getClass()));
    }

    public abstract <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException;

    public abstract SerializationFactoryLookup getSerializationFactoryLookup();

    public abstract <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException;


    public static class Wrapper extends DataWriter {
        DataWriter parent;

        public Wrapper(OutputStream os, DataWriter parent) throws IOException {
            super(os);
            this.parent = parent;
        }

        @Override
        public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
            parent.writeObject(o, type);
        }

        @Override
        public SerializationFactoryLookup getSerializationFactoryLookup() {
            return parent.getSerializationFactoryLookup();
        }

        @Override
        public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
            return parent.createAndRegisterSerializer(factory, type);
        }
    }
}
