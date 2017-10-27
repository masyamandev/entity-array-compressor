package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;

import static com.masyaman.datapack.reflection.TypeDescriptor.BIT_SET;
import static org.assertj.core.api.Assertions.assertThat;

public class BitSetSerializationFactoryTest {

    public static final SerializationFactory FACTORY = BitSetSerializationFactory.INSTANCE;

    @Test
    public void test1Byte() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<BitSet> serializer = FACTORY.createSerializer(new SerialDataWriter(os), BIT_SET);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize(null);
        serializer.serialize(bitset());
        serializer.serialize(bitset(2));
        serializer.serialize(bitset(2, 7));

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(dataWriterBytes.length + 1 + 1 + 2 + 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<BitSet> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(BIT_SET)).isNull();
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset());
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(2));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(2, 7));
    }

    @Test
    public void test2Byte() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<BitSet> serializer = FACTORY.createSerializer(new SerialDataWriter(os), BIT_SET);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize(bitset(8));
        serializer.serialize(bitset(8, 15));
        serializer.serialize(bitset(1, 8, 15));

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(dataWriterBytes.length + 3 + 3 + 3);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<BitSet> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(8));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(8, 15));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(1, 8, 15));
    }

    @Test
    public void test2Longs() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<BitSet> serializer = FACTORY.createSerializer(new SerialDataWriter(os), BIT_SET);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize(bitset(63));
        serializer.serialize(bitset(64));
        serializer.serialize(bitset(1, 65));

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(dataWriterBytes.length + 9 + 10 + 10);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<BitSet> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(63));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(64));
        assertThat(deserializer.deserialize(BIT_SET)).isEqualTo(bitset(1, 65));
    }

    public static BitSet bitset(int... values) {
        BitSet bitSet = new BitSet();
        for (int value : values) {
            bitSet.set(value);
        }

        return bitSet;
    }
}