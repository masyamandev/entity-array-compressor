package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import com.masyaman.datapack.utils.ByteStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.masyaman.datapack.utils.ByteStream.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;

public class NumberSerializerBytesTest {

    public static final TypeDescriptor<Long> LONG_TYPE = new TypeDescriptor<>(Long.class);

    @Test
    public void testNumberSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = NumberSerializationFactory.INSTANCE;
        Serializer<Long> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, LONG_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, 0 means new serializer, null means id-less serializer
                serializationFactory.getName().length(), serializationFactory.getName(), // save serializer
                // serializer properties
                2, "64", // type of signed 64-bits value
                0 // scale 0
        ));

        serializer.serialize(10L);
        serializer.serialize(30L);
        serializer.serialize(60L);
        serializer.serialize(50L);
        serializer.serialize(-50L);
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(10, 30, 60, 50, 0x80 - 50, 0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = dataReader.createAndRegisterDeserializer(LONG_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(10L);
        assertThat(deserializer.deserialize()).isEqualTo(30L);
        assertThat(deserializer.deserialize()).isEqualTo(60L);
        assertThat(deserializer.deserialize()).isEqualTo(50L);
        assertThat(deserializer.deserialize()).isEqualTo(-50L);
        assertThat(deserializer.deserialize()).isNull();
    }

    @Test
    public void testNumberDiffSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = NumberDiffSerializationFactory.INSTANCE;
        Serializer<Long> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, LONG_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0x7F, // serializer id, null means id-less serializer
                serializationFactory.getName().length(), serializationFactory.getName(), // save serializer
                // serializer properties
                2, "64", // type of signed 64-bits value
                0 // scale 0
        ));

        serializer.serialize(10L);
        serializer.serialize(30L);
        serializer.serialize(60L);
        serializer.serialize(100L);
        serializer.serialize(50L);
        serializer.serialize(-10L);
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(10, 20, 30, 40, 0x80 - 50, 0x80 - 60, 0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = dataReader.createAndRegisterDeserializer(LONG_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(10L);
        assertThat(deserializer.deserialize()).isEqualTo(30L);
        assertThat(deserializer.deserialize()).isEqualTo(60L);
        assertThat(deserializer.deserialize()).isEqualTo(100L);
        assertThat(deserializer.deserialize()).isEqualTo(50L);
        assertThat(deserializer.deserialize()).isEqualTo(-10L);
        assertThat(deserializer.deserialize()).isNull();
    }

    @Test
    public void testNumberLinearSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = NumberLinearSerializationFactory.INSTANCE;
        Serializer<Long> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, LONG_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0x7F, // serializer id, null means id-less serializer
                serializationFactory.getName().length(), serializationFactory.getName(), // save serializer
                // serializer properties
                2, "64", // type of signed 64-bits value
                0 // scale 0
        ));

        serializer.serialize(10L);
        serializer.serialize(30L);
        serializer.serialize(50L);
        serializer.serialize(60L);
        serializer.serialize(70L);
        serializer.serialize(50L);
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(10, 20, 0, 0x80 - 10, 0, 0x80 - 30, 0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = dataReader.createAndRegisterDeserializer(LONG_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(10L);
        assertThat(deserializer.deserialize()).isEqualTo(30L);
        assertThat(deserializer.deserialize()).isEqualTo(50L);
        assertThat(deserializer.deserialize()).isEqualTo(60L);
        assertThat(deserializer.deserialize()).isEqualTo(70L);
        assertThat(deserializer.deserialize()).isEqualTo(50L);
        assertThat(deserializer.deserialize()).isNull();
    }

    @Test
    public void testNumberMedianSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = NumberMedianSerializationFactory.INSTANCE;
        Serializer<Long> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, LONG_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0x7F, // serializer id, null means id-less serializer
                serializationFactory.getName().length(), serializationFactory.getName(), // save serializer
                // serializer properties
                2, "64", // type of signed 64-bits value
                0, // scale 0
                3 // number of diffs in median
        ));

        // init 3 diffs
        serializer.serialize(10L);
        serializer.serialize(30L);
        serializer.serialize(50L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(10, 20, 20));
        // diffs are 20, 20, 0
        serializer.serialize(60L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 - 10));

        // diffs are 20, 20, 10
        serializer.serialize(80L);
        serializer.serialize(90L);
        serializer.serialize(100L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 0x80 - 10, 0));

        // diffs are 20, 10, 10
        serializer.serialize(100L);
        serializer.serialize(100L);
        serializer.serialize(100L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 - 10, 0x80 - 10, 0));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = dataReader.createAndRegisterDeserializer(LONG_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(10L);
        assertThat(deserializer.deserialize()).isEqualTo(30L);
        assertThat(deserializer.deserialize()).isEqualTo(50L);
        assertThat(deserializer.deserialize()).isEqualTo(60L);
        assertThat(deserializer.deserialize()).isEqualTo(80L);
        assertThat(deserializer.deserialize()).isEqualTo(90L);
        assertThat(deserializer.deserialize()).isEqualTo(100L);
        assertThat(deserializer.deserialize()).isEqualTo(100L);
        assertThat(deserializer.deserialize()).isEqualTo(100L);
        assertThat(deserializer.deserialize()).isEqualTo(100L);
    }
}
