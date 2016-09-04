package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.DecimalPrecision;
import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.annotations.instances.SerializeKeyByInstance;
import com.masyaman.datapack.annotations.instances.SerializeValueByInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.numbers.DoubleFixedSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializationFactory;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class MapSerializationFactoryTest {

    public static final SerializationFactory FACTORY = MapSerializationFactory.INSTANCE;
    public static final TypeDescriptor MAP_TYPE = new TypeDescriptor(Map.class);
    public static final TypeDescriptor HASH_MAP_TYPE = new TypeDescriptor(HashMap.class);
    public static final TypeDescriptor TREE_MAP_TYPE = new TypeDescriptor(TreeMap.class);
    private static final int HEADER_MAX_SIZE = 20;

    @Test
    public void testStrings() throws Exception {
        Map<String, String> map = new HashMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("key" + i, "val" + i);
        }
        checkSerialization(map, MAP_TYPE, map.size() * 10, map.size() * 16 + HEADER_MAX_SIZE);
    }

    @Test
    public void testNonCachedValuesStrings() throws Exception {
        Map<String, String> map = new HashMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("key" + i, "val" + i);
        }
        int expectedSize = map.size() * 14; // keyType + keyLen + keyStr(5) = 7 bytes, same for value
        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeKeyByInstance(UnknownTypeSerializationFactory.class),
                new SerializeValueByInstance(UnknownTypeSerializationFactory.class));
        checkSerialization(map, td, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testNonCachedValuesSpecifiedTypeStrings() throws Exception {
        Map<String, String> map = new HashMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("key" + i, "val" + i);
        }
        int expectedSize = map.size() * 12; // keyLen + keyStr(5) = 6 bytes, same for value
        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeKeyByInstance(StringSerializationFactory.class),
                new SerializeValueByInstance(StringSerializationFactory.class));
        checkSerialization(map, td, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testCachedValuesStrings() throws Exception {
        Map<String, String> map = new HashMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("key" + i, "val");
        }
        int expectedSize = map.size() * 7; // keyLen + keyStr(5) = 6 bytes for key + 1 byte for val
        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeKeyByInstance(StringSerializationFactory.class),
                new SerializeValueByInstance(StringCachedSerializationFactory.class));
        checkSerialization(map, td, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testVariousObjects() throws Exception {
        Map map = new HashMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("keyLong" + i, Long.valueOf(i));
        }
        for (int i = 10; i < 40; i++) {
            map.put("keyDounble" + i, Double.valueOf(i) / 10);
        }
        for (int i = 10; i < 40; i++) {
            map.put(Integer.valueOf(i), "value" + i);
        }
        checkSerialization(map, MAP_TYPE, map.size() * 10, map.size() * 16 + HEADER_MAX_SIZE);
    }

    @Test
    public void testTreeMapToHashMap() throws Exception {
        Map map = new TreeMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("key" + i, "val" + i);
        }
        int minSize = map.size() * 10;
        int maxSize = map.size() * 16 + HEADER_MAX_SIZE;
        checkSerialization(map, MAP_TYPE, MAP_TYPE, minSize, maxSize);
        checkSerialization(map, MAP_TYPE, HASH_MAP_TYPE, minSize, maxSize);
        checkSerialization(map, MAP_TYPE, TREE_MAP_TYPE, minSize, maxSize);
        checkSerialization(map, TREE_MAP_TYPE, MAP_TYPE, minSize, maxSize);
        checkSerialization(map, TREE_MAP_TYPE, HASH_MAP_TYPE, minSize, maxSize);
        checkSerialization(map, TREE_MAP_TYPE, TREE_MAP_TYPE, minSize, maxSize);
    }

    @Test
    public void testHashMapToTreeMap() throws Exception {
        Map map = new HashMap<>();
        for (int i = 10; i < 40; i++) {
            map.put("key" + i, "val" + i);
        }
        int minSize = map.size() * 10;
        int maxSize = map.size() * 16 + HEADER_MAX_SIZE;
        checkSerialization(map, MAP_TYPE, MAP_TYPE, minSize, maxSize);
        checkSerialization(map, MAP_TYPE, HASH_MAP_TYPE, minSize, maxSize);
        checkSerialization(map, MAP_TYPE, TREE_MAP_TYPE, minSize, maxSize);
        checkSerialization(map, HASH_MAP_TYPE, MAP_TYPE, minSize, maxSize);
        checkSerialization(map, HASH_MAP_TYPE, HASH_MAP_TYPE, minSize, maxSize);
        checkSerialization(map, HASH_MAP_TYPE, TREE_MAP_TYPE, minSize, maxSize);
    }

    @Test
    public void testInheritKey() throws Exception {
        Map map = new HashMap<>();
        map.put(1.111, 1.111);
        map.put(2.222, 2.222);

        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeKeyByInstance(DoubleFixedSerializationFactory.class, Double.class, Precision1.class),
                new SerializeValueByInstance(DoubleFixedSerializationFactory.class, Double.class, InheritFromParent.class));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Map> serializer = FACTORY.createSerializer(new DataWriter(os), td);

        serializer.serialize(map);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Map> deserializer = FACTORY.createDeserializer(new DataReader(is), MAP_TYPE);
        Map deserialized = deserializer.deserialize();

        Map expected = new HashMap<>();
        expected.put(1.1, 1.111);
        expected.put(2.2, 2.222);
        assertThat(deserialized).isEqualTo(expected);
    }

    @Test
    public void testInheritValue() throws Exception {
        Map map = new HashMap<>();
        map.put(1.111, 1.111);
        map.put(2.222, 2.222);

        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeKeyByInstance(DoubleFixedSerializationFactory.class, Double.class, InheritFromParent.class),
                new SerializeValueByInstance(DoubleFixedSerializationFactory.class, Double.class, Precision1.class));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Map> serializer = FACTORY.createSerializer(new DataWriter(os), td);

        serializer.serialize(map);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Map> deserializer = FACTORY.createDeserializer(new DataReader(is), MAP_TYPE);
        Map deserialized = deserializer.deserialize();

        Map expected = new HashMap<>();
        expected.put(1.111, 1.1);
        expected.put(2.222, 2.2);
        assertThat(deserialized).isEqualTo(expected);
    }


    private void checkSerialization(Map map, TypeDescriptor td, int minSize, int maxSize) throws IOException {
        checkSerialization(map, td, td, minSize, maxSize);
    }
    private void checkSerialization(Map map, TypeDescriptor tdSer, TypeDescriptor tdDeser, int minSize, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Map> serializer = FACTORY.createSerializer(new DataWriter(os), tdSer);

        serializer.serialize(map);

        byte[] bytes = os.toByteArray();
        assertThat(bytes.length).isBetween(minSize, maxSize);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Map> deserializer = FACTORY.createDeserializer(new DataReader(is), tdDeser);
        Map deserialized = deserializer.deserialize();
        assertThat(tdDeser.getType().isAssignableFrom(deserialized.getClass())).isTrue();
        assertThat(deserialized).isEqualTo(map);
    }

    @DecimalPrecision(1)
    private static class Precision1 {}

}