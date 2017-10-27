package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.numbers.LongDeserializer;
import com.masyaman.datapack.serializers.numbers.LongSerializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import com.masyaman.datapack.utils.ByteStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;
import static com.masyaman.datapack.utils.ByteStream.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;

public class LatestFirstCacheBytesTest {

    @Test
    public void testLimitedCache() throws Exception {

        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        int cacheSize = 4;
        Serializer<Long> serializer = new LatestFirstCachedSerializer<>(dataWriter, new LongSerializer(dataWriter), cacheSize);

        assertThat(stream.getNewBytes()).isEmpty();

        serializer.serialize(10L);
        serializer.serialize(20L);
        serializer.serialize(30L);
        serializer.serialize(40L); // cache: 40, 30, 20, 10
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 10, 0, 20, 0, 30, 0, 40));

        serializer.serialize(40L);
        serializer.serialize(40L);
        serializer.serialize(40L); // cache: 40, 30, 20, 10
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1, 1, 1));

        serializer.serialize(30L); // cache: 30, 40, 20, 10
        serializer.serialize(30L); // cache: 30, 40, 20, 10
        serializer.serialize(40L); // cache: 40, 30, 20, 10
        serializer.serialize(10L); // cache: 10, 40, 30, 20
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, 1, 2, 4));

        serializer.serialize(50L); // cache: 50, 10, 40, 30
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 50));
        serializer.serialize(20L); // cache: 20, 50, 10, 40
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 20));

        serializer.serialize(20L); // cache: 20, 50, 10, 40
        serializer.serialize(10L); // cache: 10, 20, 50, 40
        serializer.serialize(50L); // cache: 50, 10, 20, 40
        serializer.serialize(40L); // cache: 40, 50, 10, 20
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1, 3, 3, 4));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = new LatestFirstCachedDeserializer<>(dataReader, new LongDeserializer(dataReader), cacheSize);

        assertThat(deserializer.deserialize(LONG)).isEqualTo(10L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(20L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(30L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(40L);

        assertThat(deserializer.deserialize(LONG)).isEqualTo(40L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(40L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(40L);

        assertThat(deserializer.deserialize(LONG)).isEqualTo(30L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(30L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(40L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(10L);

        assertThat(deserializer.deserialize(LONG)).isEqualTo(50L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(20L);

        assertThat(deserializer.deserialize(LONG)).isEqualTo(20L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(10L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(50L);
        assertThat(deserializer.deserialize(LONG)).isEqualTo(40L);

    }

    @Test
    public void testRandomValuesLimitedCache() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        int cacheSize = 200;
        Serializer<Long> serializer = new LatestFirstCachedSerializer<>(dataWriter, new LongSerializer(dataWriter), cacheSize);

        Random random = new Random(125667234);

        List<Long> values = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            long value = random.nextBoolean() ? random.nextInt(200) : random.nextInt(2000);
            values.add(value);
            serializer.serialize(value);
        }

        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = new LatestFirstCachedDeserializer<>(dataReader, new LongDeserializer(dataReader), cacheSize);

        for (Long value : values) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(value);
        }
    }

    @Test
    public void testRandomValuesUnlimitedCache() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        int cacheSize = 0;
        Serializer<Long> serializer = new LatestFirstCachedSerializer<>(dataWriter, new LongSerializer(dataWriter), cacheSize);

        Random random = new Random(125667234);

        List<Long> values = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            long value = random.nextBoolean() ? random.nextInt(200) : random.nextInt(2000);
            values.add(value);
            serializer.serialize(value);
        }

        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Long> deserializer = new LatestFirstCachedDeserializer<>(dataReader, new LongDeserializer(dataReader), cacheSize);

        for (Long value : values) {
            assertThat(deserializer.deserialize(LONG)).isEqualTo(value);
        }
    }
}