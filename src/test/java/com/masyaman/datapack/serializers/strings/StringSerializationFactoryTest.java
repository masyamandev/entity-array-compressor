package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSerializationFactoryTest extends TestCase {

    public static final SerializationFactory FACTORY = StringSerializationFactory.INSTANCE;
    public static final TypeDescriptor STRING_TYPE = new TypeDescriptor(String.class);

    public void testLowest7bits() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<String> serializer = FACTORY.createSerializer(new DataWriter(os), STRING_TYPE);
        byte[] dataWriterBytes = os.toByteArray();

        serializer.serialize("abcABC123");
        serializer.serialize("1234567890");
        serializer.serialize("abcABC123");
        serializer.serialize("\n\r\t");
        serializer.serialize("abcABC123");

        byte[] bytes = os.toByteArray();
        // 5 bytes for length, 3 strings of length 9, 1 string of length 10 and 1 string of length 3
        assertThat(bytes).hasSize(dataWriterBytes.length + 5 + 9 * 3 + 10 + 3);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<String> deserializer = FACTORY.createDeserializer(new DataReader(is), STRING_TYPE);
        assertThat(deserializer.deserialize()).isEqualTo("abcABC123");
        assertThat(deserializer.deserialize()).isEqualTo("1234567890");
        assertThat(deserializer.deserialize()).isEqualTo("abcABC123");
        assertThat(deserializer.deserialize()).isEqualTo("\n\r\t");
        assertThat(deserializer.deserialize()).isEqualTo("abcABC123");
    }
}