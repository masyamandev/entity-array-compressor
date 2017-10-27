package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.annotations.serialization.Precision;
import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
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

import static com.masyaman.datapack.reflection.TypeDescriptor.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CollectionsSerializationFactoryTest {

    public static final SerializationFactory FACTORY = CollectionSerializationFactory.INSTANCE;

    public static final TypeDescriptor[] TYPES = new TypeDescriptor[] {
            COLLECTION, LIST, ARRAY_LIST, LINKED_LIST,
            SET, HASH_SET, TREE_SET, LINKED_HASH_SET,
            OBJECT, OBJECT_ARRAY
    };

    private static final int HEADER_MAX_SIZE = 20;

    @Test
    public void testStrings() throws Exception {
        Collection<String> collection = new ArrayList<>();
        for (int i = 10; i < 40; i++) {
            collection.add("val" + i);
        }
        int expectedSize = collection.size() * 7; // valueType + valueLen + valueStr(5) = 7 bytes
        checkSerialization(collection, COLLECTION, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testStringsSet() throws Exception {
        Collection<String> collection = new HashSet<>();
        for (int i = 10; i < 40; i++) {
            collection.add("val" + i);
        }
        int expectedSize = collection.size() * 7; // valueType + valueLen + valueStr(5) = 7 bytes
        checkSerialization(collection, COLLECTION, expectedSize, expectedSize + HEADER_MAX_SIZE);
    }

    @Test
    public void testNonCachedValuesSpecifiedTypeStrings() throws Exception {
        Collection<String> collection = new ArrayList<>();
        for (int i = 10; i < 40; i++) {
            collection.add("val" + i);
        }
        int expectedSize = collection.size() * 6; // valueLen + valueStr(5) = 7 bytes
        TypeDescriptor td = new TypeDescriptor(List.class,
                new SerializeValueBy.Instance(StringSerializationFactory.class));
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
        checkSerialization(collection, LIST, COLLECTION, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST, LIST, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST, ARRAY_LIST, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST, SET, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
        checkSerialization(collection, LIST, HASH_SET, expectedSize, expectedSize + HEADER_MAX_SIZE * 2);
    }

    @Test
    public void testInheritValue() throws Exception {
        Collection collection = new ArrayList<>();
        collection.add(1.111);
        collection.add(2.222);

        TypeDescriptor td = new TypeDescriptor(Map.class,
                new SerializeValueBy.Instance(NumberSerializationFactory.class, Double.class, Precision1.class));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<Collection> serializer = FACTORY.createSerializer(new SerialDataWriter(os), td);

        serializer.serialize(collection);
        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Collection> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        Collection deserialized = deserializer.deserialize(LIST);

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
        Deserializer<Collection> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        Object deserializedObject = deserializer.deserialize(tdDeser);
        Collection deserialized;
        if (tdDeser.getType().isArray()) {
            deserialized = Arrays.asList((Object[]) deserializedObject);
        } else {
            deserialized = (Collection) deserializedObject;
            assertThat(tdDeser.getType().isAssignableFrom(deserialized.getClass())).isTrue();
        }
        assertThat(deserialized).containsOnlyElementsOf(collection);
        if (collection instanceof List && deserialized instanceof List) {
            // check order
            assertThat(deserialized).containsExactlyElementsOf(collection);
        }
    }

    @Precision(1)
    private static class Precision1 {}

}