package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.caching.SimpleCachedDeserializer;
import com.masyaman.datapack.serializers.numbers.LongDeserializer;
import com.masyaman.datapack.serializers.numbers.UnsignedLongDeserializer;
import com.masyaman.datapack.serializers.strings.StringDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class DataReader implements ObjectReader {

    protected InputStream is;
    private DataReader parentReader;

    private Deserializer<Long> signedLongDeserializer;
    private Deserializer<Long> unsignedLongDeserializer;
    private Deserializer<String> stringDeserializer;
    private Deserializer<String> stringCachedDeserializer;

    public DataReader(InputStream is, DataReader parentReader) throws IOException {
        this.is = is;
        this.parentReader = parentReader;

        signedLongDeserializer = new LongDeserializer(this);//SignedLongSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(Long.class));
        unsignedLongDeserializer = new UnsignedLongDeserializer(this);//SignedLongSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(Long.class));
        stringDeserializer = new StringDeserializer(this);//StringSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(String.class));
        stringCachedDeserializer = new SimpleCachedDeserializer(this, stringDeserializer);//StringCachedSerializationFactory.INSTANCE.createDeserializer(this, new TypeDescriptor(String.class));
    }

    @Override
    public void close() throws IOException {
        is.close();
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

    public <T> T readObject(TypeDescriptor<T> type) throws IOException {
        return parentReader.readObject(type);
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return parentReader.getSerializationFactoryLookup();
    }

    public <E> Deserializer<E> createAndRegisterDeserializer(TypeDescriptor<E> type) throws IOException {
        return parentReader.createAndRegisterDeserializer(type);
    }
}
