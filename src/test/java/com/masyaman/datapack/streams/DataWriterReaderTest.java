package com.masyaman.datapack.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masyaman.datapack.annotations.deserialization.instances.AsJsonInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.objects.samples.*;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.masyaman.datapack.annotations.deserialization.DeserializationTypes.JSON_TYPE;
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

    @Test
    public void testObjectSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(new LatLonAlt(1.1, 2.2, 3.3), null));
        dw.writeObject(new ArrayFields(new Object[] {1, 1L, 1D}, new String[] {"A", "B"}));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLonAlt(1.1, 2.2, 3.3), null));
        assertThat(dr.readObject()).isEqualTo(new ArrayFields(new Object[] {1, 1L, 1D}, new String[] {"A", "B"}));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testObjectDeserializationAsJson() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new ArrayFields(new Object[] {1, 1L, 1D}, new String[] {"A", "B"}));
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(new LatLonAlt(1.1, 2.2, 3.3), null));
        dw.writeObject(new IgnoredFields("xx", "yy"));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.readValue(dr.readObject(JSON_TYPE), ArrayFields.class))
                .isEqualTo(new ArrayFields(new Object[] {1, 1, 1D}, new String[] {"A", "B"})); // No Long here
        assertThat(mapper.readValue(dr.readObject(JSON_TYPE), LatLonTsTz.class))
                .isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));

        assertThat(dr.readObject(JSON_TYPE)).contains("\"alt\":3.3");
        assertThat(dr.readObject(JSON_TYPE)).isEqualTo("{\"stored\":\"yy\"}");
        assertThat(dr.readObject(JSON_TYPE)).isNull();
    }

    @Test
    public void testObjectDeserializationAsJsonWithType() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new LatLon(1.1, 2.2));
        dw.writeObject(new IgnoredFields("xx", "yy"));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        ObjectMapper mapper = new ObjectMapper();

        // class IgnoredFields has field 'stored', so we can check that real field is not overwritten
        TypeDescriptor<String> jsonType = new TypeDescriptor(String.class, new AsJsonInstance(false, "stored"));

        String latLon = dr.readObject(jsonType);
        assertThat(latLon).contains("\"lat\":1.1");
        assertThat(latLon).contains("\"lon\":2.2");
        assertThat(latLon).contains("\"stored\":\"" + LatLon.class.getTypeName() + "\"");

        // ensure that there is only one field 'stored'
        assertThat(dr.readObject(jsonType)).isEqualTo("{\"stored\":\"yy\"}");
        assertThat(dr.readObject(jsonType)).isNull();
    }

}