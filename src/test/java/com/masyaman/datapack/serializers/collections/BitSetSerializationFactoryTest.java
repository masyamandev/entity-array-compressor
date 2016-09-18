package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;

public class BitSetSerializationFactoryTest {

    public static final SerializationFactory FACTORY = BitSetSerializationFactory.INSTANCE;
    public static final TypeDescriptor BITSET_TYPE = new TypeDescriptor(BitSet.class);

    @Test
    public void test1Byte() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<BitSet> serializer = FACTORY.createSerializer(new DataWriter(os), BITSET_TYPE);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize(null);
        serializer.serialize(bitset());
        serializer.serialize(bitset(2));
        serializer.serialize(bitset(2, 7));

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(dataWriterBytes.length + 1 + 1 + 2 + 2);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<BitSet> deserializer = FACTORY.createDeserializer(new DataReader(is), BITSET_TYPE);
        assertThat(deserializer.deserialize()).isNull();
        assertThat(deserializer.deserialize()).isEqualTo(bitset());
        assertThat(deserializer.deserialize()).isEqualTo(bitset(2));
        assertThat(deserializer.deserialize()).isEqualTo(bitset(2, 7));
    }

    @Test
    public void test2Byte() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<BitSet> serializer = FACTORY.createSerializer(new DataWriter(os), BITSET_TYPE);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize(bitset(8));
        serializer.serialize(bitset(8, 15));
        serializer.serialize(bitset(1, 8, 15));

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(dataWriterBytes.length + 3 + 3 + 3);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<BitSet> deserializer = FACTORY.createDeserializer(new DataReader(is), BITSET_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(bitset(8));
        assertThat(deserializer.deserialize()).isEqualTo(bitset(8, 15));
        assertThat(deserializer.deserialize()).isEqualTo(bitset(1, 8, 15));
    }

    @Test
    public void test2Longs() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<BitSet> serializer = FACTORY.createSerializer(new DataWriter(os), BITSET_TYPE);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize(bitset(63));
        serializer.serialize(bitset(64));
        serializer.serialize(bitset(1, 65));

        byte[] bytes = os.toByteArray();
        assertThat(bytes).hasSize(dataWriterBytes.length + 9 + 10 + 10);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<BitSet> deserializer = FACTORY.createDeserializer(new DataReader(is), BITSET_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo(bitset(63));
        assertThat(deserializer.deserialize()).isEqualTo(bitset(64));
        assertThat(deserializer.deserialize()).isEqualTo(bitset(1, 65));
    }

    public static BitSet bitset(int... values) {
        BitSet bitSet = new BitSet();
        for (int value : values) {
            bitSet.set(value);
        }

        return bitSet;
    }
}