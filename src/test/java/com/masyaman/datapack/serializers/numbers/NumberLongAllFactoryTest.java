package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class NumberLongAllFactoryTest {

    public final SerializationFactory factory;
    public static final TypeDescriptor LONG_TYPE = new TypeDescriptor(Long.class);
    public static final TypeDescriptor DOUBLE_TYPE = new TypeDescriptor(Double.class);

    public NumberLongAllFactoryTest(SerializationFactory factory, String name) {
        this.factory = factory;
    }

    @Parameterized.Parameters(name = "{1}")
    public static Collection parameters() {
        List<AbstractNumberSerializationFactory> factories = Arrays.asList(
                NumberDiffSerializationFactory.INSTANCE,
                NumberIncrementalSerializationFactory.INSTANCE,
                NumberLinearSerializationFactory.INSTANCE,
                NumberMedianSerializationFactory.INSTANCE,
                NumberSerializationFactory.INSTANCE,
                UnsignedLongSerializationFactory.INSTANCE);

        List<Object[]> params = new ArrayList<>();
        for (AbstractNumberSerializationFactory factory : factories) {
            params.add(new Object[] {
                    factory, factory.getClass().getSimpleName()
            });
        }

        return params;
    }

    @Test
    public void testLongRandomSmallPositives() throws Exception {
        Random r = new Random(2345678);
        long[] data = new long[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (r.nextLong() & Long.MAX_VALUE) % 1000;
        }

        assertSerializeDeserialize(data);
    }

    @Test
    public void testLongRandomSmallNegative() throws Exception {
        Random r = new Random(2345678);
        long[] data = new long[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = -(r.nextLong() & Long.MAX_VALUE) % 1000;
        }

        assertSerializeDeserialize(data);
    }

    @Test
    public void testLongRandom() throws Exception {
        Random r = new Random(2345678);
        long[] data = new long[100000];
        for (int i = 0; i < data.length; i++) {
            data[i] = r.nextLong();
        }

        assertSerializeDeserialize(data);
    }

    @Test
    public void testLongInfinity() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = factory.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        serializer.serialize(Long.MAX_VALUE);
        serializer.serialize(Long.MIN_VALUE);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = factory.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(Long.MAX_VALUE);
        assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testLongNull() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = factory.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        serializer.serialize(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = factory.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(LONG_TYPE)).isNull();
    }

    @Test
    public void testDoubleRandom() throws Exception {
        Random r = new Random(2345678);
        double[] data = new double[100000];
        for (int i = 0; i < data.length; i++) {
            data[i] = r.nextInt() / 1000000d;
        }

        assertSerializeDeserialize(data, Offset.offset(0.00000001));
    }

    private void assertSerializeDeserialize(long[] data) throws java.io.IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = factory.createSerializer(new SerialDataWriter(os), LONG_TYPE);
        for (int i = 0; i < data.length; i++) {
            serializer.serialize(data[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Long> deserializer = factory.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < data.length; i++) {
            assertThat(deserializer.deserialize(LONG_TYPE)).isEqualTo(data[i]);
        }
    }

    private void assertSerializeDeserialize(double[] data, Offset<Double> offset) throws java.io.IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = factory.createSerializer(new SerialDataWriter(os), DOUBLE_TYPE);
        for (int i = 0; i < data.length; i++) {
            serializer.serialize(data[i]);
        }
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Double> deserializer = factory.createDeserializer(new SerialDataReader(is));
        for (int i = 0; i < data.length; i++) {
            assertThat(deserializer.deserialize(DOUBLE_TYPE)).isCloseTo(data[i], offset);
        }
    }
}