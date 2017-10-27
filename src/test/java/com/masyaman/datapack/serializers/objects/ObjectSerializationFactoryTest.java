package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.Alias;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.samples.*;
import com.masyaman.datapack.settings.ClassManager;
import com.masyaman.datapack.settings.SettingsHandler;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static com.masyaman.datapack.settings.SettingsKeys.CLASS_MANAGER;
import static com.masyaman.datapack.settings.SettingsKeys.IGNORE_UNKNOWN_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;

public class ObjectSerializationFactoryTest {

    public static final ObjectSerializationFactory FACTORY = ObjectSerializationFactory.INSTANCE;

    @Test
    public void testSimpleSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TypeDescriptor type = new TypeDescriptor(TsTz.class);
        Serializer<TsTz> serializer = FACTORY.createSerializer(new SerialDataWriter(os), type);
        serializer.serialize(new TsTz(100000L, 234));
        serializer.serialize(new TsTz(100000L, 234));
        serializer.serialize(new TsTz(100000L, 234));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<TsTz> deserializer = FACTORY.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(type)).isEqualTo(new TsTz(100000L, 234));
        assertThat(deserializer.deserialize(type)).isEqualTo(new TsTz(100000L, 234));
        assertThat(deserializer.deserialize(type)).isEqualTo(new TsTz(100000L, 234));
    }

    @Test
    public void testObjectSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(null, new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), null));
        dw.writeObject(new LatLonTsTz(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(null, new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), null));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testComplexObjectSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new TsTz(100000L, 234));
        dw.writeObject(new TsTz(100000L, 234));
        dw.writeObject(new TsTz(100000L, 234));
        dw.writeObject(new LatLon(1.1, 2.2));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new TsTz(100000L, 234));
        assertThat(dr.readObject()).isEqualTo(new TsTz(100000L, 234));
        assertThat(dr.readObject()).isEqualTo(new TsTz(100000L, 234));
        assertThat(dr.readObject()).isEqualTo(new LatLon(1.1, 2.2));
    }

    @Test
    public void testObjectSerializationWithInheritance() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(null, new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(new LatLonAlt(1.1, 2.2, 3.3), null));
        dw.writeObject(new LatLonTsTz(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(null, new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLonAlt(1.1, 2.2, 3.3), null));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testSerializationWithSpecifiedClass() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new LatLonNoAltTsTz(new LatLonAlt(1.1, 2.2, 3.3), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonNoAltTsTz(null, new TsTz(100000L, 234)));
        dw.writeObject(new LatLonNoAltTsTz(new LatLonAlt(1.1, 2.2, 3.3), null));
        dw.writeObject(new LatLonNoAltTsTz(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        // No Alt here because class was set exactly to LatLon, see annotation in LatLonNoAltTsTz.latLon
        assertThat(dr.readObject()).isEqualTo(new LatLonNoAltTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonNoAltTsTz(null, new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonNoAltTsTz(new LatLon(1.1, 2.2), null));
        assertThat(dr.readObject()).isEqualTo(new LatLonNoAltTsTz(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testSerializationWithSpecifiedClassFieldsObjects() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new LatLonTsTzAsObject(new LatLonAlt(1.1, 2.2, 3.3), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTzAsObject(null, new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTzAsObject(new LatLonAlt(1.1, 2.2, 3.3), null));
        dw.writeObject(new LatLonTsTzAsObject(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        // No Alt here because class was set exactly to LatLon, see annotation in LatLonNoAltTsTz.latLon
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTzAsObject(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTzAsObject(null, new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTzAsObject(new LatLon(1.1, 2.2), null));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTzAsObject(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testSerializationWithArrays() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new ArrayFields(new Object[] {1, 1L, 1D}, new String[] {"A", "B"}));
        dw.writeObject(new ArrayFields(null, new String[] {"A", "B"}));
        dw.writeObject(new ArrayFields(new Object[] {1, 1L, 1D}, null));
        dw.writeObject(new ArrayFields(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new ArrayFields(new Object[] {1, 1L, 1D}, new String[] {"A", "B"}));
        assertThat(dr.readObject()).isEqualTo(new ArrayFields(null, new String[] {"A", "B"}));
        assertThat(dr.readObject()).isEqualTo(new ArrayFields(new Object[] {1, 1L, 1D}, null));
        assertThat(dr.readObject()).isEqualTo(new ArrayFields(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testSerializationWithCollections() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new CollectionFields(Arrays.asList(1, 1L, 1D), Arrays.asList("A", "B")));
        dw.writeObject(new CollectionFields(null, Arrays.asList("A", "B")));
        dw.writeObject(new CollectionFields(Arrays.asList(1, 1L, 1D), null));
        dw.writeObject(new CollectionFields(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new CollectionFields(Arrays.asList(1, 1L, 1D), Arrays.asList("A", "B")));
        assertThat(dr.readObject()).isEqualTo(new CollectionFields(null, Arrays.asList("A", "B")));
        assertThat(dr.readObject()).isEqualTo(new CollectionFields(Arrays.asList(1, 1L, 1D), null));
        assertThat(dr.readObject()).isEqualTo(new CollectionFields(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testSerializationWithSets() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new CollectionSetFields(Arrays.asList(1, 1L, 1D), Arrays.asList("A", "B")));
        dw.writeObject(new CollectionSetFields(null, Arrays.asList("A", "B")));
        dw.writeObject(new CollectionSetFields(Arrays.asList(1, 1L, 1D), null));
        dw.writeObject(new CollectionSetFields(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new CollectionSetFields(Arrays.asList(1, 1L, 1D), Arrays.asList("A", "B")));
        assertThat(dr.readObject()).isEqualTo(new CollectionSetFields(null, Arrays.asList("A", "B")));
        assertThat(dr.readObject()).isEqualTo(new CollectionSetFields(Arrays.asList(1, 1L, 1D), null));
        assertThat(dr.readObject()).isEqualTo(new CollectionSetFields(null, null));
        assertThat(dr.readObject()).isNull();
    }

//    @Test
//    public void testDeserializationWithAnotherType() throws Exception {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        DataWriter dw = new SerialDataWriter(os);
//        dw.writeObject(new CollectionFields(Arrays.asList(1, 1L, 1D), Arrays.asList("A", "B")));
//        dw.writeObject(new CollectionFields(null, Arrays.asList("A", "B")));
//        dw.writeObject(new CollectionFields(Arrays.asList(1, 1L, 1D), null));
//        dw.writeObject(new CollectionFields(null, null));
//        dw.writeObject(null);
//
//        byte[] bytes = os.toByteArray();
//
//        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
//        DataReader dr = new SerialDataReader(is);
//        TypeDescriptor td = new TypeDescriptor(CollectionSetFields.class);
//        assertThat(dr.readObject(td)).isEqualTo(new CollectionSetFields(Arrays.asList(1, 1L, 1D), Arrays.asList("A", "B")));
//        assertThat(dr.readObject(td)).isEqualTo(new CollectionSetFields(null, Arrays.asList("A", "B")));
//        assertThat(dr.readObject(td)).isEqualTo(new CollectionSetFields(Arrays.asList(1, 1L, 1D), null));
//        assertThat(dr.readObject(td)).isEqualTo(new CollectionSetFields(null, null));
//        assertThat(dr.readObject(td)).isNull();
//    }

    @Test
    public void testSerializationWithIgnoredFields() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new IgnoredFields("A", "B"));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new IgnoredFields(null, "B"));
    }

    @Test
    public void testSerializationWithDates() throws Exception {
        Date today = new Date();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os);
        dw.writeObject(new ObjectWithDate(today));
        dw.writeObject(null);
        dw.writeObject(new ObjectWithDate(null));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is);
        assertThat(dr.readObject()).isEqualTo(new ObjectWithDate(today));
        assertThat(dr.readObject()).isNull();
        assertThat(dr.readObject()).isEqualTo(new ObjectWithDate(null));
    }

    @Test
    public void testDeserializeToAnotherType() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler()
                .set(CLASS_MANAGER, new ClassManager().addMixIn(LatLon.class, ObjectRenameMixIn.class)));
        dw.writeObject(new LatLon(1.1, 2.2));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler()
                .set(CLASS_MANAGER, new ClassManager().addMixIn(LatLonAlt.class, ObjectRenameMixIn.class)));
        assertThat(dr.readObject()).isEqualTo(new LatLonAlt(1.1, 2.2, 0));
    }

    @Test(expected = IOException.class)
    public void testDeserializeFailOnUnknownField() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler()
                .set(CLASS_MANAGER, new ClassManager().addMixIn(LatLonAlt.class, ObjectRenameMixIn.class)));
        dw.writeObject(new LatLonAlt(1.1, 2.2, 3.3));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler()
                .set(CLASS_MANAGER, new ClassManager().addMixIn(LatLon.class, ObjectRenameMixIn.class)));
        assertThat(dr.readObject()).isEqualTo(new LatLon(1.1, 2.2));
    }

    @Test
    public void testDeserializeIgnoreUnknownField() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler()
                .set(CLASS_MANAGER, new ClassManager().addMixIn(LatLonAlt.class, ObjectRenameMixIn.class)));
        dw.writeObject(new LatLonAlt(1.1, 2.2, 3.3));

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler()
                .set(CLASS_MANAGER, new ClassManager().addMixIn(LatLon.class, ObjectRenameMixIn.class))
                .set(IGNORE_UNKNOWN_FIELDS, true));
        assertThat(dr.readObject()).isEqualTo(new LatLon(1.1, 2.2));
    }


    @Alias("MyObject")
    private static class ObjectRenameMixIn {}
}