package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCachedSerializationFactoryTest extends TestCase {

    public static final SerializationFactory FACTORY = StringCachedSerializationFactory.INSTANCE;
    public static final TypeDescriptor INTEGER_TYPE = new TypeDescriptor(String.class);

    public void testLowest7bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<String> serializer = FACTORY.createSerializer(new SerialDataWriter(os), INTEGER_TYPE);
        byte[] serializerBytes = os.toByteArray();

        serializer.serialize("abcABC123");
        serializer.serialize("1234567890");
        serializer.serialize("abcABC123");
        serializer.serialize("\n\r\t");
        serializer.serialize("abcABC123");

        byte[] bytes = os.toByteArray();
        // 5 bytes for string id, 3 bytes of unique string lengths, 3 strings of lengths 9, 10 and 3
        assertThat(bytes).hasSize(serializerBytes.length + 5 + 3 + 9 + 10 + 3);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<String> deserializer = FACTORY.createDeserializer(new SerialDataReader(is), INTEGER_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo("abcABC123");
        assertThat(deserializer.deserialize()).isEqualTo("1234567890");
        assertThat(deserializer.deserialize()).isEqualTo("abcABC123");
        assertThat(deserializer.deserialize()).isEqualTo("\n\r\t");
        assertThat(deserializer.deserialize()).isEqualTo("abcABC123");
    }
}