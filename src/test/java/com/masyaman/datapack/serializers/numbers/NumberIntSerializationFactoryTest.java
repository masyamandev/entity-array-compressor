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

import static com.masyaman.datapack.reflection.TypeDescriptor.INTEGER;
import static org.assertj.core.api.Assertions.assertThat;

public class NumberIntSerializationFactoryTest {

    public static final SerializationFactory FACTORY = NumberSerializationFactory.INSTANCE;

    @Test
    public void testInt8Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        byte[] serializerBytes = os.toByteArray();
        for (int l = 63; l >= -63; l--) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 63 * 2 + 1);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int l = 63; l >= -63; l--) {
            assertThat(deserializer.deserialize(INTEGER)).isEqualTo(l);
        }
    }

    @Test
    public void testIntPositive16Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        byte[] serializerBytes = os.toByteArray();
        for (int l = 200; l < 8200; l+= 10) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 800 * 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int l = 200; l < 8200; l+= 10) {
            assertThat(deserializer.deserialize(INTEGER)).isEqualTo(l);
        }
    }

    @Test
    public void testIntNegative16Bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        byte[] serializerBytes = os.toByteArray();
        for (int l = -200; l > -8200; l-= 10) {
            serializer.serialize(l);
        }
        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 800 * 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int l = -200; l > -8200; l-= 10) {
            assertThat(deserializer.deserialize(INTEGER)).isEqualTo(l);
        }
    }

    @Test
    public void testIntRandom() throws Exception {
        Random r = new Random(2345678);
        int[] ll = new int[100000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = r.nextInt();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(INTEGER)).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testInt100k() throws Exception {
        int[] ll = new int[200000];
        for (int i = 0; i < ll.length; i++) {
            ll[i] = i - 100000;
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        for (int i = 0; i < ll.length; i++) {
            serializer.serialize(ll[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < ll.length; i++) {
            assertThat(deserializer.deserialize(INTEGER)).isEqualTo(ll[i]);
        }
    }

    @Test
    public void testIntInfinity() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        byte[] serializerBytes = os.toByteArray();
        serializer.serialize(Integer.MAX_VALUE);
        serializer.serialize(Integer.MIN_VALUE);

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 2 * 5);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(INTEGER)).isEqualTo(Integer.MAX_VALUE);
        assertThat(deserializer.deserialize(INTEGER)).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testIntNull() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Integer> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER);
        byte[] serializerBytes = os.toByteArray();
        serializer.serialize(null);

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 1);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Integer> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(INTEGER)).isNull();
    }
}