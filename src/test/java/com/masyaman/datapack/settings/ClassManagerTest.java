package com.masyaman.datapack.settings;

import com.masyaman.datapack.annotations.Alias;
import com.masyaman.datapack.serializers.objects.samples.LatLon;
import com.masyaman.datapack.serializers.objects.samples.LatLonAlt;
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

import static com.masyaman.datapack.annotations.deserialization.DeserializationTypes.JSON_WITH_TYPES_TYPE;
import static com.masyaman.datapack.settings.SettingsKeys.CLASS_MANAGER;
import static org.assertj.core.api.Assertions.assertThat;

public class ClassManagerTest {

    @Test
    public void testClassAlias() throws Exception {
        ClassManager classManager = new ClassManager();
        classManager.addMixIn(LatLon.class, LatLonMixIn.class);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler().set(CLASS_MANAGER, classManager));
        dw.writeObject(new LatLon(1.1, 2.2));

        byte[] bytes = os.toByteArray();
        String bytesAsString = new String(bytes);
        assertThat(bytesAsString).doesNotContain("LatLon");
        assertThat(bytesAsString).contains("LLClass");

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler().set(CLASS_MANAGER, classManager));

        assertThat(dr.readObject()).isEqualTo(new LatLon(1.1, 2.2));
    }

    @Test
    public void testFieldsAliasJson() throws Exception {
        ClassManager classManager = new ClassManager();
        classManager.addMixIn(LatLon.class, LatLonMixIn.class);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler().set(CLASS_MANAGER, classManager));
        dw.writeObject(new LatLon(1.1, 2.2));

        byte[] bytes = os.toByteArray();
        String bytesAsString = new String(bytes);
        assertThat(bytesAsString).doesNotContain("LatLon");
        assertThat(bytesAsString).contains("LLClass");
        assertThat(bytesAsString).doesNotContain("lat");
        assertThat(bytesAsString).doesNotContain("lon");
        assertThat(bytesAsString).contains("Latitude");
        assertThat(bytesAsString).contains("Longitude");

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler().set(CLASS_MANAGER, classManager));

        String json = dr.readObject(JSON_WITH_TYPES_TYPE);
        assertThat(json).contains("\"type\":\"LLClass\"");
        assertThat(json).contains("\"Latitude\":1.1");
        assertThat(json).contains("\"Longitude\":2.2");
    }

    @Test
    public void testExtendsAlias() throws Exception {
        ClassManager classManager = new ClassManager();
        classManager.addMixIn(LatLon.class, LatLonMixIn.class);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler().set(CLASS_MANAGER, classManager));
        dw.writeObject(new LatLonAlt(1.1, 2.2, 3.3));

        byte[] bytes = os.toByteArray();
        String bytesAsString = new String(bytes);
        assertThat(bytesAsString).contains("LatLonAlt");
        assertThat(bytesAsString).doesNotContain("LLClass");
        assertThat(bytesAsString).doesNotContain("lat");
        assertThat(bytesAsString).doesNotContain("lon");
        assertThat(bytesAsString).contains("Latitude");
        assertThat(bytesAsString).contains("Longitude");
        assertThat(bytesAsString).contains("alt");

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler().set(CLASS_MANAGER, classManager));

        assertThat(dr.readObject()).isEqualTo(new LatLonAlt(1.1, 2.2, 3.3));
    }

    @Test
    public void testExtendsAliasJson() throws Exception {
        ClassManager classManager = new ClassManager();
        classManager.addMixIn(LatLon.class, LatLonMixIn.class);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler().set(CLASS_MANAGER, classManager));
        dw.writeObject(new LatLonAlt(1.1, 2.2, 3.3));

        byte[] bytes = os.toByteArray();
        String bytesAsString = new String(bytes);
        assertThat(bytesAsString).contains("LatLonAlt");
        assertThat(bytesAsString).doesNotContain("LLClass");
        assertThat(bytesAsString).doesNotContain("lat");
        assertThat(bytesAsString).doesNotContain("lon");
        assertThat(bytesAsString).contains("Latitude");
        assertThat(bytesAsString).contains("Longitude");
        assertThat(bytesAsString).contains("alt");

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new SerialDataReader(is, new SettingsHandler().set(CLASS_MANAGER, classManager));

        String json = dr.readObject(JSON_WITH_TYPES_TYPE);
        assertThat(json).contains("\"type\":\"" + LatLonAlt.class.getName() + "\"");
        assertThat(json).contains("\"Latitude\":1.1");
        assertThat(json).contains("\"Longitude\":2.2");
        assertThat(json).contains("\"alt\":3.3");
    }

    @Test(expected = IOException.class)
    public void testThrowExceptionNoAlias() throws Exception {
        ClassManager classManager = new ClassManager();
        classManager.addMixIn(LatLon.class, LatLonMixIn.class);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new SerialDataWriter(os, new SettingsHandler().set(CLASS_MANAGER, classManager));
        dw.writeObject(new LatLon(1.1, 2.2));

        byte[] bytes = os.toByteArray();
        String bytesAsString = new String(bytes);
        assertThat(bytesAsString).doesNotContain("LatLon");
        assertThat(bytesAsString).contains("LLClass");

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);

        // No ClassManager with alias specified, exception should be thrown
        DataReader dr = new SerialDataReader(is);
        dr.readObject();
    }


    @Alias("LLClass")
    public static class LatLonMixIn {
        @Alias("Latitude")
        private double lat;
        @Alias("Longitude")
        private double lon;
    }
}