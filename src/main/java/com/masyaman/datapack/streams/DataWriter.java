package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedSerializer;
import com.masyaman.datapack.serializers.numbers.LongSerializer;
import com.masyaman.datapack.serializers.numbers.UnsignedLongSerializer;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public abstract class DataWriter implements ObjectWriter {

    protected OutputStream os;
    private DataWriter parentWriter;

    private Serializer<Long> signedLongSerializer;
    private Serializer<Long> unsignedLongSerializer;
    private Serializer<String> stringSerializer;
    private Serializer<String> stringCachedSerializer;

    public DataWriter(OutputStream os, DataWriter parentWriter) throws IOException {
        this.os = os;
        this.parentWriter = parentWriter;

        signedLongSerializer = new LongSerializer(this);//SignedLongSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(Long.class));
        unsignedLongSerializer = new UnsignedLongSerializer(this);//SignedLongSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(Long.class));
        stringSerializer = new StringSerializer(this);//StringSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(String.class));
        stringCachedSerializer = new SimpleCachedSerializer(this, stringSerializer);//StringCachedSerializationFactory.INSTANCE.createSerializer(this, new TypeDescriptor(String.class));
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

    public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
        parentWriter.writeObject(o, type);
    }

    public SerializationFactoryLookup getSerializationFactoryLookup() {
        return parentWriter.getSerializationFactoryLookup();
    }

    public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        return parentWriter.createAndRegisterSerializer(factory, type);
    }
}
