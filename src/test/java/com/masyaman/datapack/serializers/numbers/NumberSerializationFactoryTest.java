package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberSerializationFactoryTest {

    public static final SerializationFactory FACTORY = NumberSerializationFactory.INSTANCE;
    public static final TypeDescriptor DOUBLE_TYPE = new TypeDescriptor(Double.class);

    public static final Offset OFFSET = Offset.offset(0.000000000001);

    public static final double SCALE = 0.01;

    @Test
    public void testDoubleSmall() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        for (long l = 63; l >= -63; l--) {
            serializer.serialize(SCALE * l);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 63; l >= -63; l--) {
            assertThat(deserializer.deserialize(DOUBLE_TYPE)).isCloseTo(SCALE * l, OFFSET);
        }
    }

    @Test
    public void testDoublePositive() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        for (long l = 200; l < 8200; l+= 10) {
            serializer.serialize(SCALE * l);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = 200; l < 8200; l+= 10) {
            assertThat(deserializer.deserialize(DOUBLE_TYPE)).isCloseTo(SCALE * l, OFFSET);
        }
    }

    @Test
    public void testDoubleNegative() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        byte[] serializerBytes = os.toByteArray();
        for (long l = -200; l > -8200; l-= 10) {
            serializer.serialize(SCALE * l);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (long l = -200; l > -8200; l-= 10) {
            assertThat(deserializer.deserialize(DOUBLE_TYPE)).isCloseTo(SCALE * l, OFFSET);
        }
    }

    @Test
    public void testDoubleRandom() throws Exception {
        Random r = new Random(2345678);
        double[] dd = new double[100000];
        for (int i = 0; i < dd.length; i++) {
            dd[i] = SCALE * r.nextInt(1000000000);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        for (int i = 0; i < dd.length; i++) {
            serializer.serialize(dd[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        Offset offset = Offset.offset(0.0000001);
        for (int i = 0; i < dd.length; i++) {
            assertThat(deserializer.deserialize(DOUBLE_TYPE)).isCloseTo(dd[i], offset);
        }
    }

    @Test
    public void testDouble100k() throws Exception {
        double[] dd = new double[200000];
        for (int i = 0; i < dd.length; i++) {
            dd[i] = i - 100000L;
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        for (int i = 0; i < dd.length; i++) {
            serializer.serialize(dd[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < dd.length; i++) {
            assertThat(deserializer.deserialize(DOUBLE_TYPE)).isCloseTo(dd[i], OFFSET);
        }
    }

    @Test
    public void testDoubleNull() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        byte[] serializerBytes = os.toByteArray();
        serializer.serialize(null);

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(serializerBytes.length + 1);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(DOUBLE_TYPE)).isNull();
    }
}