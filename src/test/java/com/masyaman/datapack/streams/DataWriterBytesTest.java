package com.masyaman.datapack.streams;

import com.masyaman.datapack.serializers.numbers.NumberDiffSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializationFactory;
import com.masyaman.datapack.utils.ByteStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.masyaman.datapack.utils.ByteStream.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;

public class DataWriterBytesTest {

    private static String DEFAULT_LONG_SERIALIZER = NumberDiffSerializationFactory.INSTANCE.getName();
    private static String DEFAULT_STRING_SERIALIZER = StringSerializationFactory.INSTANCE.getName();

    @Test
    public void testEmptyHeader() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);

        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 0)); // Header: version and settings
    }

    @Test
    public void testWriteBytes() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeByte(0);
        dataWriter.writeByte(10);
        dataWriter.writeByte(100);
        dataWriter.writeByte(255);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 10, 100, 255));
    }

    @Test
    public void testWriteSignedLongs() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeSignedLong(0L);
        dataWriter.writeSignedLong(10L);
        dataWriter.writeSignedLong(63L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 10, 63));

        dataWriter.writeSignedLong(64L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80, 0x40));
        dataWriter.writeSignedLong(255L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80, 0xFF));
        dataWriter.writeSignedLong(256L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x81, 0x00));

        dataWriter.writeSignedLong(-1L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));
        dataWriter.writeSignedLong(-256L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0xBF, 0x00));

        dataWriter.writeSignedLong(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x40));
    }

    @Test
    public void testWriteUnsignedLongs() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeUnsignedLong(0L);
        dataWriter.writeUnsignedLong(10L);
        dataWriter.writeUnsignedLong(63L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 10, 63));

        dataWriter.writeUnsignedLong(64L);
        dataWriter.writeUnsignedLong(126L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x40, 0x7E));

        dataWriter.writeUnsignedLong(127L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80, 0x7F));
        dataWriter.writeUnsignedLong(128L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80, 0x80));
        dataWriter.writeUnsignedLong(255L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x80, 0xFF));
        dataWriter.writeUnsignedLong(256L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x81, 0x00));

        dataWriter.writeUnsignedLong(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));
    }

    @Test
    public void testWriteStrings() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeString(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));

        dataWriter.writeString("AA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, 'A', 'A'));

        dataWriter.writeString("AA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2, "AA"));

        dataWriter.writeString("BBB");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(3, "BBB"));
    }

    @Test
    public void testWriteCachedStrings() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeCachedString(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));

        dataWriter.writeCachedString("AA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 2, 'A', 'A'));

        dataWriter.writeCachedString("AA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1));

        dataWriter.writeCachedString("BBB");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 3, "BBB"));

        dataWriter.writeCachedString("AA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(1));

        dataWriter.writeCachedString("BBB");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(2));

        dataWriter.writeCachedString("C");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0, 1, "C"));
    }

    @Test
    public void testWriteLongsAsObjects() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeObject(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));

        dataWriter.writeObject(0L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, new
                0, DEFAULT_LONG_SERIALIZER.length(), DEFAULT_LONG_SERIALIZER, // save serializer (cached)
                0, 2, "64", // serializer properties: type of signed 64-bits value (cached)
                0, // serializer properties: scale 0
                0 // value itself
        ));

        dataWriter.writeObject(0L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                1, // serializer id, cached
                0 // value itself
        ));

        dataWriter.writeObject(1L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                1, // serializer id, cached
                1 // value itself
        ));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        assertThat(dataReader.readObject()).isNull();
        assertThat(dataReader.readObject()).isEqualTo(0L);
        assertThat(dataReader.readObject()).isEqualTo(0L);
        assertThat(dataReader.readObject()).isEqualTo(1L);
    }

    @Test
    public void testWriteStringsAsObjects() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeObject(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));

        dataWriter.writeObject("AAA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, new
                0, DEFAULT_STRING_SERIALIZER.length(), DEFAULT_STRING_SERIALIZER, // save serializer (cached)
                3, "AAA" // value itself
        ));

        dataWriter.writeObject("BB");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                1, // serializer id, cached
                2, "BB" // value itself
        ));

        dataWriter.writeObject("");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                1, // serializer id, cached
                0 // value itself
        ));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        assertThat(dataReader.readObject()).isNull();
        assertThat(dataReader.readObject()).isEqualTo("AAA");
        assertThat(dataReader.readObject()).isEqualTo("BB");
        assertThat(dataReader.readObject()).isEqualTo("");
    }


    @Test
    public void testWriteMultipleSerializers() throws Exception {
        ByteStream stream = new ByteStream();
        DataWriter dataWriter = new SerialDataWriter(stream);
        stream.getNewBytes();

        dataWriter.writeObject(null);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(0x7F));

        // Write some Longs, serializer id is 1

        dataWriter.writeObject(0L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, new
                0, DEFAULT_LONG_SERIALIZER.length(), DEFAULT_LONG_SERIALIZER, // save serializer (cached)
                0, 2, "64", // serializer properties: type of signed 64-bits value (cached)
                0, // serializer properties: scale 0
                0 // value itself
        ));

        dataWriter.writeObject(0L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                1, // serializer id, cached
                0 // value itself
        ));

        // Write some Strings, serializer id is 2

        dataWriter.writeObject("AAA");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, new
                0, DEFAULT_STRING_SERIALIZER.length(), DEFAULT_STRING_SERIALIZER, // save serializer (cached)
                3, "AAA" // value itself
        ));

        dataWriter.writeObject("BB");
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                2, // serializer id, cached
                2, "BB" // value itself
        ));

        // Again some Longs, serializer id is 1

        dataWriter.writeObject(1L);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                1, // serializer id, cached
                1 // value itself
        ));

        // Write some Ints, serializer id is 3

        dataWriter.writeObject(0);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, new
                1, // save serializer, it's already cached
                0, 2, "32", // serializer properties: type of signed 32-bits value (cached)
                0, // serializer properties: scale 0
                0 // value itself
        ));

        dataWriter.writeObject(0);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                3, // serializer id, cached
                0 // value itself
        ));

        dataWriter.writeObject(1);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                3, // serializer id, cached
                1 // value itself
        ));

        // Write some Doubles, serializer id is 4

        dataWriter.writeObject(0.0);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                0, // serializer id, new
                1, // save serializer, it's already cached
                0, 3, "64f", // serializer properties: type of floating point 64-bits value (cached)
                6, // serializer properties: scale 6 is default for Doubles
                0 // value itself
        ));

        dataWriter.writeObject(0.065535);
        assertThat(stream.getNewBytes()).containsExactly(toByteArray(
                4, // serializer id, cached
                0xC0, 0xFF, 0xFF // value itself
        ));


        // Read objects
        DataReader dataReader = new SerialDataReader(new ByteArrayInputStream(stream.toByteArray()));
        assertThat(dataReader.readObject()).isNull();
        assertThat(dataReader.readObject()).isEqualTo(0L);
        assertThat(dataReader.readObject()).isEqualTo(0L);
        assertThat(dataReader.readObject()).isEqualTo("AAA");
        assertThat(dataReader.readObject()).isEqualTo("BB");
        assertThat(dataReader.readObject()).isEqualTo(1L);
        assertThat(dataReader.readObject()).isEqualTo(0);
        assertThat(dataReader.readObject()).isEqualTo(0);
        assertThat(dataReader.readObject()).isEqualTo(1);
        assertThat(dataReader.readObject()).isEqualTo(0.0);
        assertThat(dataReader.readObject()).isEqualTo(0.065535);
    }
}
