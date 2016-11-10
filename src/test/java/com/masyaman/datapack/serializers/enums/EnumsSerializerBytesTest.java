package com.masyaman.datapack.serializers.enums;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import com.masyaman.datapack.utils.ByteStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.masyaman.datapack.utils.ByteStream.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;

public class EnumsSerializerBytesTest {

    public static final TypeDescriptor<Digits> ENUM_TYPE = new TypeDescriptor<>(Digits.class);

    @Test
    public void testEnumSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = EnumsSerializationFactory.INSTANCE;
        Serializer<Digits> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, ENUM_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0x7F, // serializer id, null means id-less serializer
                serializationFactory.getName().length(), serializationFactory.getName(), // save serializer
                // serializer properties
                0 // Cache size
        ));

        serializer.serialize(Digits.TWO);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 3, "TWO"));
        serializer.serialize(Digits.TWO);
        serializer.serialize(Digits.TWO);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1, 1));

        serializer.serialize(Digits.THREE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 5, "THREE"));
        serializer.serialize(Digits.THREE);
        serializer.serialize(Digits.THREE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1, 1));

        serializer.serialize(Digits.TWO);
        serializer.serialize(Digits.TWO);
        serializer.serialize(Digits.TWO);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, 1, 1));

        serializer.serialize(Digits.THREE);
        serializer.serialize(Digits.THREE);
        serializer.serialize(Digits.THREE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, 1, 1));

        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Digits> deserializer = dataReader.createAndRegisterDeserializer();
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isNull();
    }


    @Test
    public void testEnumConstantSerializer() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        SerializationFactory serializationFactory = EnumsConstantsSerializationFactory.INSTANCE;
        Serializer<Digits> serializer = dataWriter.createAndRegisterSerializer(serializationFactory, ENUM_TYPE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, 0 is required for globally-defined
                serializationFactory.getName().length(), serializationFactory.getName() // save serializer
                // serializer properties
        ));

        serializer.serialize(Digits.TWO);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 3, "TWO"));
        serializer.serialize(Digits.TWO);
        serializer.serialize(Digits.TWO);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1, 1));

        serializer.serialize(Digits.THREE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 5, "THREE"));
        serializer.serialize(Digits.THREE);
        serializer.serialize(Digits.THREE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, 2));

        serializer.serialize(Digits.TWO);
        serializer.serialize(Digits.TWO);
        serializer.serialize(Digits.TWO);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1, 1, 1));

        serializer.serialize(Digits.THREE);
        serializer.serialize(Digits.THREE);
        serializer.serialize(Digits.THREE);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, 2, 2));

        serializer.serialize(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        Deserializer<Digits> deserializer = dataReader.createAndRegisterDeserializer();
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.TWO);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);
        assertThat(deserializer.deserialize(ENUM_TYPE)).isEqualTo(Digits.THREE);

        assertThat(deserializer.deserialize(ENUM_TYPE)).isNull();
    }


    public enum Digits {
        ZERO, ONE, TWO, THREE
    }
}