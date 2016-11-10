package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberLongLinearSerializationFactoryTest {

    public static final SerializationFactory FACTORY = NumberLinearSerializationFactory.INSTANCE;
    public static final TypeDescriptor LONG_TYPE = new TypeDescriptor(Long.class);

    @Test
    public void testLong8Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        for (long l = 63; l >= -63; l--) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 63; l >= -63; l--) {
            assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(l);
        }
    }

    @Test
    public void testLongPositive16Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        byte[] serializerBytes = os.toByteArray();
        for (long l = 200; l < 8200; l+= 10) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes.length).isLessThan(serializerBytes.length + 800 * 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 200; l < 8200; l+= 10) {
            assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(l);
        }
    }

    @Test
    public void testLongNegative16Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        byte[] serializerBytes = os.toByteArray();
        for (long l = -200; l > -8200; l-= 10) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes.length).isLessThan(serializerBytes.length + 800 * 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = -200; l > -8200; l-= 10) {
            assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(l);
        }
    }

    @Test
    public void testLongRandom() throws Exception {
        Random r = new Random(2345678);
        long[] ll = new long[100000];
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
    public void testLong100k() throws Exception {
        long[] ll = new long[200000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = i - 100000L;
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
    public void testLongInfinity() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        serializer.serialize(Long.MAX_VALUE);
        serializer.serialize(Long.MIN_VALUE);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(Long.MAX_VALUE);
        assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testLongNull() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        byte[] serializerBytes = os.toByteArray();
        serializer.serialize(null);

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 1);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(LONG_TYPE)).isNull();
    }
}