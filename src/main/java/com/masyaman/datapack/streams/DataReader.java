package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedDeserializer;
import com.masyaman.datapack.serializers.primitives.SignedLongReader;
import com.masyaman.datapack.serializers.primitives.StringReader;
import com.masyaman.datapack.serializers.primitives.UnsignedLongReader;

import java.io.IOException;
import java.io.InputStream;

public abstract class DataReader implements ObjectReader {

    protected InputStream is;

    private Deserializer<Long> signedLongDeserializer;
    private Deserializer<Long> unsignedLongDeserializer;
    private Deserializer<String> stringDeserializer;
    private Deserializer<String> stringCachedDeserializer;

    public DataReader(InputStream is) throws IOException {
        this.is = is;

        signedLongDeserializer = new SignedLongReader(is);
        unsignedLongDeserializer = new UnsignedLongReader(is);
        stringDeserializer = new StringReader(is, unsignedLongDeserializer);
        stringCachedDeserializer = new SimpleCachedDeserializer(this, stringDeserializer);
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public boolean hasObjects() throws IOException {
        return is.available() > 0;
    }

    public byte readByte() throws IOException {
        return (byte) is.read();
    }

    public int readUnsignedByte() throws IOException {
        return is.read();
    }
    
    public Long readSignedLong() throws IOException {
        return signedLongDeserializer.deserialize();
    }

    public Long readUnsignedLong() throws IOException {
        return unsignedLongDeserializer.deserialize();
    }

    public String readString() throws IOException {
        return stringDeserializer.deserialize();
    }

    public String readCachedString() throws IOException {
        return stringCachedDeserializer.deserialize();
    }

    public Object readObject() throws IOException {
        return readObject(null);
    }

    public abstract <T> T readObject(TypeDescriptor<T> type) throws IOException;

    public abstract SerializationFactoryLookup getSerializationFactoryLookup();

    public abstract <E> Deserializer<E> createAndRegisterDeserializer(TypeDescriptor<E> type) throws IOException;


    public static class Wrapper extends DataReader {
        DataReader parent;

        public Wrapper(InputStream is, DataReader parent) throws IOException {
            super(is);
            this.parent = parent;
        }

        @Override
        public <T> T readObject(TypeDescriptor<T> type) throws IOException {
            return parent.readObject(type);
        }

        @Override
        public SerializationFactoryLookup getSerializationFactoryLookup() {
            return parent.getSerializationFactoryLookup();
        }

        @Override
        public <E> Deserializer<E> createAndRegisterDeserializer(TypeDescriptor<E> type) throws IOException {
            return parent.createAndRegisterDeserializer(type);
        }
    }
}
