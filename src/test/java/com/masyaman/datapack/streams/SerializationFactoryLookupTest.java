package com.masyaman.datapack.streams;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationFactoryLookupTest extends TestCase {

    public void testDifferentNames() throws Exception {
        Map<String, Integer> names = new HashMap<>();
        for (SerializationFactory factory : SerializationFactoryLookup.DEFAULT_FACTORIES) {
            names.put(factory.getName(), names.getOrDefault(factory.getName(), 0) + 1);
        }
        assertThat(names).hasSameSizeAs(SerializationFactoryLookup.DEFAULT_FACTORIES);
    }

    public void testDeserializersWithNullType() throws Exception {
        for (SerializationFactory factory : SerializationFactoryLookup.DEFAULT_FACTORIES) {
            assertThat(factory.isApplicable(factory.getDefaultType()))
                    .as("Factory %s (%s) should be applicable for type %s", factory.getClass().getName(), factory.getName(),
                            factory.getDefaultType().getType().getName())
                    .isTrue();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Serializer serializer = factory.createSerializer(new SerialDataWriter(os), factory.getDefaultType());
            serializer.serialize(null);

            byte[] bytes = os.toByteArray();

            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            Deserializer deserializer = factory.createDeserializer(new SerialDataReader(is), factory.getDefaultType());
            assertThat(deserializer.deserialize()).isNull();
        }
    }
}