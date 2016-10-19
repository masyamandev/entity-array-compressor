package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.serialization.Precision;
import com.masyaman.datapack.annotations.serialization.instances.SerializeValueByInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.numbers.NumberSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializationFactory;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionsSerializationFactoryTest {

    public static final SerializationFactory FACTORY = CollectionSerializationFactory.INSTANCE;

    public static final TypeDescriptor COLLECTION_TYPE = new TypeDescriptor(Collection.class);
    public static final TypeDescriptor LIST_TYPE = new TypeDescriptor(List.class);
    public static final TypeDescriptor ARRAY_LIST_TYPE = new TypeDescriptor(ArrayList.class);
    public static final TypeDescriptor LINKED_LIST_TYPE = new TypeDescriptor(LinkedList.class);
    public static final TypeDescriptor SET_TYPE = new TypeDescriptor(Set.class);
    public static final TypeDescriptor HASH_SET_TYPE = new TypeDescriptor(HashSet.class);
    public static final TypeDescriptor TREE_SET_TYPE = new TypeDescriptor(TreeSet.class);
    public static final TypeDescriptor LINKED_HASH_SET_TYPE = new TypeDescriptor(LinkedHashSet.class);

    public static final TypeDescriptor[] TYPES = new TypeDescriptor[] {
            COLLECTION_TYPE, LIST_TYPE, ARRAY_LIST_TYPE, LINKED_LIST_TYPE,
            SET_TYPE, HASH_SET_TYPE, TREE_SET_TYPE, LINKED_HASH_SET_TYPE
    };

    private static final int HEADER_MAX_SIZE = 20;

    @Test
    public void testStrings() throws Exception {
        Collection<String> collection = new ArrayList<>();
        for (int i = 10; i < 40; i++) {
            collection.add("val" + i);
        }
        int expectedSize = collection.size() * 7; // valueType + valueLen + valueStr(5) = 7 bytes
        checkSerialization(collection, COLLECTION_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testStringsSet() throws Exception {
        Collection<String> collection = new HashSet<>();
        for (int i = 10; i < 40; i++) {
            collection.add("val" + i);
        }
        int expectedSize = collection.size() * 7; // valueType + valueLen + valueStr(5) = 7 bytes
        checkSerialization(collection, COLLECTION_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testNonCachedValuesSpecifiedTypeStrings() throws Exception {
        Collection<String> collection = new ArrayList<>();
        for (int i = 10; i < 40; i++) {
            collection.add("val" + i);
        }
        int expectedSize = collection.size() * 6; // valueLen + valueStr(5) = 7 bytes
        TypeDescriptor td = new TypeDescriptor(List.class,
                new SerializeValueByInstance(StringSerializationFactory.class));
        checkSerialization(collection, td, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testVariousObjects() throws Exception {
        Collection collection = new ArrayList<>();
        for (int i = 10; i < 40; i++) {
            collection.add(Long.valueOf(i));
        }
        for (int i = 10; i < 40; i++) {
            collection.add(Double.valueOf(i) / 10);
        }
        for (int i = 10; i < 40; i++) {
            collection.add("value" + i);
        }
        int expectedSize = collection.size() / 3 * (2 + 4 + 9);
        checkSerialization(collection, LIST_TYPE, COLLECTION_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST_TYPE, LIST_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST_TYPE, ARRAY_LIST_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST_TYPE, SET_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST_TYPE, HASH_SET_TYPE, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
    }

    @Test
    public void testInheritValue() throws Exception {
        Collection collection = new ArrayList<>();
        collection.add(1.111);
        collection.add(2.222);

        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeValueByInstance(NumberSerializationFactory.class, Double.class, Precision1.class));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Collection> serializer = FACTORY.createSerializer(new SerialDataWriter(os), td);

        serializer.serialize(collection);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Collection> deserializer = FACTORY.createDeserializer(new SerialDataReader(is), LIST_TYPE);
        Collection deserialized = deserializer.deserialize();

        Collection expected = new ArrayList<>();
        expected.add(1.1);
        expected.add(2.2);
        assertThat(deserialized).isEqualTo(expected);
    }


    private void checkSerialization(Collection collection, TypeDescriptor td, int minSize, int maxSize) throws IOException {
        for (TypeDescriptor type : TYPES) {
            checkSerialization(collection, td, type, minSize, maxSize);
        }
    }

    private void checkSerialization(Collection collection, TypeDescriptor tdSer, TypeDescriptor tdDeser, int minSize, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Collection> serializer = FACTORY.createSerializer(new SerialDataWriter(os), tdSer);

        serializer.serialize(collection);

        byte[] bytes = os.toByteArray();
        assertThat(bytes.length).isBetween(minSize, maxSize);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Collection> deserializer = FACTORY.createDeserializer(new SerialDataReader(is), tdDeser);
        Collection deserialized = deserializer.deserialize();
        assertThat(tdDeser.getType().isAssignableFrom(deserialized.getClass())).isTrue();
        assertThat(deserialized).containsOnlyElementsOf(collection);
        if (collection instanceof List && deserialized instanceof List) {
            // check order
            assertThat(deserialized).containsExactlyElementsOf(collection);
        }
    }

    @Precision(1)
    private static class Precision1 {}

}