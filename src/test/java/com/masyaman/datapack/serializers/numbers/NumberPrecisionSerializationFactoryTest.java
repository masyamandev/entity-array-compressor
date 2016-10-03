package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.annotations.instances.DecimalPrecisionInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberPrecisionSerializationFactoryTest {

    public static final SerializationFactory FACTORY = NumberSerializationFactory.INSTANCE;
    public static final TypeDescriptor DOUBLE_TYPE = new TypeDescriptor(Double.class);
    public static final TypeDescriptor LONG_TYPE = new TypeDescriptor(Long.class);

    public static final Offset OFFSET = Offset.offset(0.000000000001);


    @Test
    public void testPrecision6() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new DecimalPrecisionInstance(6)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble), DOUBLE_TYPE);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1234.123457, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1234.987654, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1234.123457, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1234.987654, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong), LONG_TYPE);
        assertThat(longDeserializer.deserialize()).isEqualTo(1234);
        assertThat(longDeserializer.deserialize()).isEqualTo(1235);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1235);
    }

    @Test
    public void testPrecision2() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new DecimalPrecisionInstance(2)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble), DOUBLE_TYPE);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1234.99, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1234.99, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong), LONG_TYPE);
        assertThat(longDeserializer.deserialize()).isEqualTo(1234);
        assertThat(longDeserializer.deserialize()).isEqualTo(1235);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1235);
    }

    @Test
    public void testPrecision0() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new DecimalPrecisionInstance(0)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble), DOUBLE_TYPE);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1234.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1235.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1234.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1235.0, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong), LONG_TYPE);
        assertThat(longDeserializer.deserialize()).isEqualTo(1234);
        assertThat(longDeserializer.deserialize()).isEqualTo(1235);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1235);
    }

    @Test
    public void testPrecisionM2() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new DecimalPrecisionInstance(-2)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble), DOUBLE_TYPE);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1200.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1200.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1200.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-1200.0, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong), LONG_TYPE);
        assertThat(longDeserializer.deserialize()).isEqualTo(1200);
        assertThat(longDeserializer.deserialize()).isEqualTo(1200);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1200);
        assertThat(longDeserializer.deserialize()).isEqualTo(-1200);
    }
}