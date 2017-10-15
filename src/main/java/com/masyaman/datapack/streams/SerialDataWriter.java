package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.settings.SettingsHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Serialize objects into a single data stream.
 * Once object is written, it's byte array is immediately pushed into a stream without any additional buffering.
 */
public class SerialDataWriter extends DataWriter.Abstract {

    public static final long CURRENT_VERSION = 0;

    public SerialDataWriter(OutputStream os) throws IOException {
        this(os, SettingsHandler.DEFAULTS);
    }

    public SerialDataWriter(OutputStream os, SettingsHandler settings) throws IOException {
        super(os, settings);
        writeGlobalSettings();
    }

    private void writeGlobalSettings() throws IOException {
        writeUnsignedLong(CURRENT_VERSION);
        writeUnsignedLong(0L); // there will be number of settings here
    }

    @Override
    protected <E> Serializer<E> writeSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
        writeString(factory.getName());
        Serializer serializer = factory.createSerializer(this, type);
        return serializer;
    }
}
