package com.masyaman.datapack.serializers.dates;

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
import java.util.Date;

import static com.masyaman.datapack.utils.ByteStream.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;

public class DateSerializerBytesTest {

    public static final TypeDescriptor DATE_TYPE = new TypeDescriptor(Date.class);

    public static final long SOME_TIMESTAMP = 962668800000L; // 4th Jul 2000
    public static final long SECOND = 1000L;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;

    @Test
    public void testDateSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = DateSerializationFactory.INSTANCE;
        Serializer<Date> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, DATE_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                // registering serializer, no Id here
                0, serializationFactory.getName().length(), serializationFactory.getName(), // save serializer (cached)
                // serializer properties
                0 // scale 0, millis
        ));

        serializer.serialize(new Date (SOME_TIMESTAMP));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0xF8, 0xE0, 0x23, 0x88, 0x28, 0x00));
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0xF8, 0xE0, 0x23, 0x88, 0x2B, 0xE8));
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Date> deserializer = dataReader.createAndRegisterDeserializer(DATE_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(deserializer.deserialize()).isNull();
    }

    @Test
    public void testDateDiffSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = DateDiffSerializationFactory.INSTANCE;
        Serializer<Date> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, DATE_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                // registering serializer, no Id here
                0, serializationFactory.getName().length(), serializationFactory.getName(), // save serializer (cached)
                // serializer properties
                0 // scale 0, millis
        ));

        serializer.serialize(new Date (SOME_TIMESTAMP));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0xF8, 0xE0, 0x23, 0x88, 0x28, 0x00));
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 + 0x03, 0xE8)); // 1000L
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND * 2));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 + 0x03, 0xE8)); // 1000L
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Date> deserializer = dataReader.createAndRegisterDeserializer(DATE_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND * 2));
        assertThat(deserializer.deserialize()).isNull();
    }

    @Test
    public void testDateLinearSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = DateLinearSerializationFactory.INSTANCE;
        Serializer<Date> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, DATE_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                // registering serializer, no Id here
                0, serializationFactory.getName().length(), serializationFactory.getName(), // save serializer (cached)
                // serializer properties
                0 // scale 0, millis
        ));

        serializer.serialize(new Date (SOME_TIMESTAMP));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0xF8, 0xE0, 0x23, 0x88, 0x28, 0x00));
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 + 0x03, 0xE8)); // 1000L
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND * 2));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0));
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Date> deserializer = dataReader.createAndRegisterDeserializer(DATE_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND * 2));
        assertThat(deserializer.deserialize()).isNull();
    }

    @Test
    public void testDateMedianSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = DateMedianSerializationFactory.INSTANCE;
        Serializer<Date> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, DATE_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                // registering serializer, no Id here
                0, serializationFactory.getName().length(), serializationFactory.getName(), // save serializer (cached)
                // serializer properties
                0, // scale 0, millis
                3 // diff history size
        ));

        serializer.serialize(new Date (SOME_TIMESTAMP));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0xF8, 0xE0, 0x23, 0x88, 0x28, 0x00));
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 + 0x03, 0xE8)); // 1000L
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND * 2));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80 + 0x03, 0xE8)); // 1000L
        serializer.serialize(new Date (SOME_TIMESTAMP + SECOND * 3));
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0));
        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x40));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Date> deserializer = dataReader.createAndRegisterDeserializer(DATE_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND * 2));
        assertThat(deserializer.deserialize()).isEqualTo(new Date (SOME_TIMESTAMP + SECOND * 3));
        assertThat(deserializer.deserialize()).isNull();
    }

}