package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.annotations.serialization.instances.PrecisionInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.settings.SettingsHandler;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;

import static com.masyaman.datapack.settings.SettingsKeys.DEFAULT_PRECISION;
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
                new TypeDescriptor(Double.class, new PrecisionInstance(6)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.123457, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.987654, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.123457, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.987654, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1235);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1235);
    }

    @Test
    public void testPrecisionDefault6() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os,
                        new SettingsHandler().set(DEFAULT_PRECISION, 6)), new TypeDescriptor(Double.class));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.123457, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.987654, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.123457, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.987654, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1235);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1235);
    }

    @Test
    public void testPrecision2() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(2)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.99, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.99, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1235);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1235);
    }

    @Test
    public void testPrecisionDefault2() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os,
                new SettingsHandler().set(DEFAULT_PRECISION, 2)), new TypeDescriptor(Double.class));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.99, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.99, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1235);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1235);
    }

    @Test
    public void testPrecision2Floor() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(2, RoundingMode.FLOOR)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.98, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.13, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.99, OFFSET);
    }

    @Test
    public void testPrecision2Ceiling() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(2, RoundingMode.CEILING)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.13, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.99, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.12, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.98, OFFSET);
    }

    @Test
    public void testPrecision0() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(0)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1234.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1235.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1234.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1235.0, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1235);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1234);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1235);
    }

    @Test
    public void testPrecisionM2() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(-2)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1200.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1200.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1200.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1200.0, OFFSET);

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1200);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(1200);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1200);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-1200);
    }

    @Test
    public void testPrecisionM2Up() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(-2, RoundingMode.UP)));
        serializer.serialize(1234.1234567890);
        serializer.serialize(1234.987654321);
        serializer.serialize(-1234.1234567890);
        serializer.serialize(-1234.987654321);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble));
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1300.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(1300.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1300.0, OFFSET);
        assertThat(doubleDeserializer.deserialize(DOUBLE_TYPE)).isCloseTo(-1300.0, OFFSET);
    }

    @Test
    public void testLongPrecisionM2() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Long.class, new PrecisionInstance(-2)));
        serializer.serialize(123456789L);
        serializer.serialize(987654321L);
        serializer.serialize(-123456789L);
        serializer.serialize(-987654321L);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(123456800L);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(987654300L);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-123456800L);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-987654300L);
    }

    @Test
    public void testLongPrecisionM2Floor() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Long> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Long.class, new PrecisionInstance(-2, RoundingMode.FLOOR)));
        serializer.serialize(123456789L);
        serializer.serialize(987654321L);
        serializer.serialize(-123456789L);
        serializer.serialize(-987654321L);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isLong = new ByteArrayInputStream(bytes);
        Deserializer<Long> longDeserializer = FACTORY.createDeserializer(new SerialDataReader(isLong));
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(123456700L);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(987654300L);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-123456800L);
        assertThat(longDeserializer.deserialize(LONG_TYPE)).isEqualTo(-987654400L);
    }
}