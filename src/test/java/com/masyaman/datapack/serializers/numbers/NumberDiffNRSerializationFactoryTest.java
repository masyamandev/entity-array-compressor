package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.annotations.serialization.instances.PrecisionInstance;
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

import static org.assertj.core.api.Assertions.assertThat;

public class NumberDiffNRSerializationFactoryTest {

    public static final SerializationFactory FACTORY = NumberDiffNRSerializationFactory.INSTANCE;
    public static final TypeDescriptor DOUBLE_TYPE = new TypeDescriptor(Double.class);

    public static final Offset OFFSET = Offset.offset(0.000000000001);

    @Test
    public void testSerializationCloseToPreviousValue() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Double> serializer = FACTORY.createSerializer(new SerialDataWriter(os),
                new TypeDescriptor(Double.class, new PrecisionInstance(0)));

        // 5 values closed to 1.0
        serializer.serialize(1.2);
        serializer.serialize(1.0);
        serializer.serialize(0.7);
        serializer.serialize(1.9);
        serializer.serialize(1.999);

        // switched to 2.0
        serializer.serialize(2.0);
        serializer.serialize(2.8);
        serializer.serialize(2.99);
        serializer.serialize(1.001);
        serializer.serialize(2.999);

        // switched to 5.0, 5.0 is closer to 2 than 6.0
        serializer.serialize(5.999);
        serializer.serialize(4.001);

        // switched to 1.0, 1.0 is closer to 5 than 0.0
        serializer.serialize(0.001);

        serializer.serialize(-3.001);
        serializer.serialize(-5.999);
        serializer.serialize(-7.0);
        serializer.serialize(-6.001);
        serializer.serialize(-6.0);
        serializer.serialize(-2.001);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream isDouble = new ByteArrayInputStream(bytes);
        Deserializer<Double> doubleDeserializer = FACTORY.createDeserializer(new SerialDataReader(isDouble), DOUBLE_TYPE);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(1.0, OFFSET);

        assertThat(doubleDeserializer.deserialize()).isCloseTo(2.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(2.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(2.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(2.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(2.0, OFFSET);

        assertThat(doubleDeserializer.deserialize()).isCloseTo(5.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(5.0, OFFSET);

        assertThat(doubleDeserializer.deserialize()).isCloseTo(1.0, OFFSET);

        assertThat(doubleDeserializer.deserialize()).isCloseTo(-3.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-5.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-7.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-7.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-6.0, OFFSET);
        assertThat(doubleDeserializer.deserialize()).isCloseTo(-3.0, OFFSET);
    }
}