package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;
import static org.assertj.core.api.Assertions.assertThat;

public class UnsignedLongSerializationFactoryTest {

    public static final SerializationFactory FACTORY = UnsignedLongSerializationFactory.INSTANCE;

    @Test
    public void test() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        byte[] serializerBytes = os.toByteArray();
            serializer.serialize(128L);
        byte[] bytes = os.toByteArray();
//        assertThat(bytes).hasSize(serializerBytes.length + 127);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
            assertThat(deserializer.deserialize(LONG)).isEqualTo(128);
    }

    @Test
    public void testLong8Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        byte[] serializerBytes = os.toByteArray();
        for (long l = 126; l >= 0; l--) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 127);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 126; l >= 0; l--) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(l);
        }
    }

    @Test
    public void testLongPositive16Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        byte[] serializerBytes = os.toByteArray();
        for (long l = 200; l < 16200; l+= 10) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 1600 * 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 200; l < 16200; l+= 10) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(l);
        }
    }

    @Test
    public void testLongNegative() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        for (long l = 0; l > -20000; l-= 10) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        // It just have to serialize & deserialize it

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 0; l > -20000; l-= 10) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(l);
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
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testLong200k() throws Exception {
        long[] ll = new long[200000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = i;
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testLongSpecialCases() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        byte[] serializerBytes = os.toByteArray();

        // 1 byte
        serializer.serialize(null);
        serializer.serialize(0L);
        serializer.serialize(1L);
        serializer.serialize(126L);
        // 2 bytes
        serializer.serialize(127L); // special case for null
        serializer.serialize(128L);
        serializer.serialize(129L);
        serializer.serialize(16383L);
        // 3 bytes
        serializer.serialize(16384L);
        // 9 bytes
        serializer.serialize(Long.MAX_VALUE);
        serializer.serialize(Long.MIN_VALUE);
        serializer.serialize(-1L);

        byte[] bytes = os.toByteArray();
//        assertThat(bytes).hasSize(serializerBytes.length + 1 * 4 + 2 * 4 + 3 * 1 + 9 * 3);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));

        assertThat(deserializer.deserialize(LONG)).isNull();
        assertThat(deserializer.deserialize(LONG)).isEqualTo(0L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(1L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(126L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(127L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(128L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(129L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(16383L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(16384L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(Long.MAX_VALUE);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(Long.MIN_VALUE);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(-1L);
    }

    @Test
    public void testLongInfinity() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        byte[] serializerBytes = os.toByteArray();
        serializer.serialize(Long.MAX_VALUE);
        serializer.serialize(Long.MIN_VALUE);

        byte[] bytes = os.toByteArray();
//        assertThat(bytes).hasSize(serializerBytes.length + 2 * 9);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(LONG)).isEqualTo(Long.MAX_VALUE);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testLongNull() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os), LONG);
        byte[] serializerBytes = os.toByteArray();
        serializer.serialize(null);

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 1);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(LONG)).isNull();
    }
}