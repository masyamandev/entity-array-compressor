package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.settings.SettingsHandler;
import com.masyaman.datapack.settings.SettingsKeys;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ArraysSerializationFactoryTest {

    public static final SerializationFactory FACTORY = CollectionSerializationFactory.INSTANCE;

    public static final TypeDescriptor<String[]> STRING_ARRAY = new TypeDescriptor(String[].class);

    @Test
    public void testStrings() throws Exception {
        String[] array = new String[30];
        for (int i = 0; i < array.length; i++) {
            array[i] = "val" + (i + 10);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer serializer = FACTORY.createSerializer(new SerialDataWriter(os), STRING_ARRAY);

        serializer.serialize(array);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Object> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        String[] deserialized = deserializer.deserialize(STRING_ARRAY);

        assertThat(deserialized).containsExactly(array);
    }

    @Test
    public void testStringsDeserializeAsObjectConvertedToLinkedList() throws Exception {
        String[] array = new String[30];
        for (int i = 0; i < array.length; i++) {
            array[i] = "val" + (i + 10);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer serializer = FACTORY.createSerializer(new SerialDataWriter(os), STRING_ARRAY);

        serializer.serialize(array);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Object> deserializer = FACTORY.createDeserializer(new SerialDataReader(is, new SettingsHandler()
            .set(SettingsKeys.DEFAULT_COLLECTIONS_DESERIALIZATION_TYPE, new TypeDescriptor(Collection.class))));
        Object deserialized = deserializer.deserialize(new TypeDescriptor(Object.class));

        assertThat(deserialized).isInstanceOf(List.class);

        List deserializedList = (List) deserialized;
        assertThat(deserializedList).containsExactly(array);
    }

    @Test
    public void testStringsDeserializeAsObjectConvertedToArray() throws Exception {
        String[] array = new String[30];
        for (int i = 0; i < array.length; i++) {
            array[i] = "val" + (i + 10);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer serializer = FACTORY.createSerializer(new SerialDataWriter(os), STRING_ARRAY);

        serializer.serialize(array);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<Object> deserializer = FACTORY.createDeserializer(new SerialDataReader(is, new SettingsHandler()
            .set(SettingsKeys.DEFAULT_COLLECTIONS_DESERIALIZATION_TYPE, new TypeDescriptor(Object[].class))));
        Object deserialized = deserializer.deserialize(new TypeDescriptor(Object.class));

        assertThat(deserialized).isInstanceOf(Object[].class);

        Object[] deserializedArray = (Object[]) deserialized;
        assertThat(deserializedArray).containsExactly(array);
    }

}