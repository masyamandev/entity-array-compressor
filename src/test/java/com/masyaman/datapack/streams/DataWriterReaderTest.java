package com.masyaman.datapack.streams;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class DataWriterReaderTest extends TestCase {

    public void testStrings() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter writer = new SerialDataWriter(os);

        writer.writeObject("abcABC123");
        writer.writeObject("1234567890");
        writer.writeObject("\n\r\t");

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader reader = new SerialDataReader(is);
        assertThat(reader.readObject()).isEqualTo("abcABC123");
        assertThat(reader.readObject()).isEqualTo("1234567890");
        assertThat(reader.readObject()).isEqualTo("\n\r\t");
    }

    public void testDefaultsLongAndDouble() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter writer = new SerialDataWriter(os);

        writer.writeObject(new Integer(42));
        writer.writeObject(new Long(540));
        writer.writeObject(new Double(2.2));
        writer.writeObject(new Float(3.3));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader reader = new SerialDataReader(is);
        assertThat(reader.readObject()).isEqualTo(new Integer(42));
        assertThat(reader.readObject()).isEqualTo(new Long(540));
        assertThat(reader.readObject()).isEqualTo(new Double(2.2));
        assertThat(reader.readObject()).isEqualTo(new Float(3.3));
    }
}