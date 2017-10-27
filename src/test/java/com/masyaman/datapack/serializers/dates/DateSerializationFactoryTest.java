package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.annotations.deserialization.DateFormatPattern;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class DateSerializationFactoryTest {
    public static final SerializationFactory FACTORY = DateSerializationFactory.INSTANCE;
    public static final TypeDescriptor DATE_TYPE = new TypeDescriptor(Date.class);
    public static final TypeDescriptor LONG_TYPE = new TypeDescriptor(Long.class);


    @Test
    public void testDateRandom() throws Exception {
        Random r = new Random(2345678);
        Date[] ll = new Date[1000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = new Date(r.nextLong());
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Date> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DATE_TYPE);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Date> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(DATE_TYPE)).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testLongRandom() throws Exception {
        Random r = new Random(2345678);
        long[] ll = new long[1000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = r.nextLong();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testDateToLongRandom() throws Exception {
        Random r = new Random(2345678);
        Date[] ll = new Date[1000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = new Date(r.nextLong());
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Date> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DATE_TYPE);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(ll[i].getTime());
        }
    }

    @Test
    public void testLongToDateRandom() throws Exception {
        Random r = new Random(2345678);
        long[] ll = new long[1000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = r.nextLong();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Date> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(DATE_TYPE).getTime()).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testDateToString() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Date> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DATE_TYPE);
        serializer.serialize(new Date(1400000000123L));
        byte[] bytes = os.toByteArray();

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            TypeDescriptor type = new TypeDescriptor(String.class, new DateFormatPattern.Instance());
            Deserializer<String> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
            assertThat(deserializer.deserialize(type)).isEqualTo("2014-05-13 16:53:20.123");
        }

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            TypeDescriptor type = new TypeDescriptor(String.class, new DateFormatPattern.Instance(DateFormatPattern.MILLIS_FORMAT));
            Deserializer<String> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
            assertThat(deserializer.deserialize(type)).isEqualTo("1400000000123");
        }

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            TypeDescriptor type = new TypeDescriptor(String.class, new DateFormatPattern.Instance(DateFormatPattern.SECONDS_FORMAT));
            Deserializer<String> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
            assertThat(deserializer.deserialize(type)).isEqualTo("1400000000");
        }
    }
}