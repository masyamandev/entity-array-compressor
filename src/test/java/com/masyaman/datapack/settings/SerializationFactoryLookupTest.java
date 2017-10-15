package com.masyaman.datapack.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masyaman.datapack.annotations.deserialization.DeserializationTypes;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.samples.LatLon;
import com.masyaman.datapack.serializers.objects.samples.ObjectWithDate;
import com.masyaman.datapack.settings.SerializationFactoryLookup;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SerializationFactoryLookupTest extends TestCase {

    private SerializationFactory factory;

    public SerializationFactoryLookupTest(String name, SerializationFactory factory) {
        super(name);
        this.factory = factory;
    }

    @Parameterized.Parameters(name = "{0} {1}")
    public static Collection parameters() {
        return SerializationFactoryLookup.DEFAULT_FACTORIES.stream()
                .map(sf -> new Object[] {sf.getClass().getSimpleName() + " \"" + sf.getName() + "\"", sf})
                .collect(Collectors.toList());
    }

    @BeforeClass
    public static void testDifferentNames() throws Exception {
        Map<String, Integer> names = new HashMap<>();
        for (SerializationFactory factory : SerializationFactoryLookup.DEFAULT_FACTORIES) {
            names.put(factory.getName(), names.getOrDefault(factory.getName(), 0) + 1);
        }
        assertThat(names).hasSameSizeAs(SerializationFactoryLookup.DEFAULT_FACTORIES);
    }

    @Test
    public void testDeserializersWithNullType() throws Exception {
        assertThat(factory.isApplicable(factory.getDefaultType()))
                .as("Factory %s (%s) should be applicable for type %s", factory.getClass().getName(), factory.getName(),
                        factory.getDefaultType().getType().getName())
                .isTrue();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer serializer = factory.createSerializer(new SerialDataWriter(os), factory.getDefaultType());
        serializer.serialize(null);

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer deserializer = factory.createDeserializer(new SerialDataReader(is));
        assertThat(deserializer.deserialize(factory.getDefaultType())).isNull();
    }

    @Test
    public void testDeserializersWithNotNullType() throws Exception {
        assertThat(factory.isApplicable(factory.getDefaultType()))
                .as("Factory %s (%s) should be applicable for type %s", factory.getClass().getName(), factory.getName(),
                        factory.getDefaultType().getType().getName())
                .isTrue();

        List<Object> objects = createObjectsOfType(factory.getDefaultType());

        for (Object object : objects) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Serializer serializer = factory.createSerializer(new SerialDataWriter(os), new TypeDescriptor(object.getClass()));
            serializer.serialize(object);

            byte[] bytes = os.toByteArray();

            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            Deserializer deserializer = factory.createDeserializer(new SerialDataReader(is));
            Object deserialized = deserializer.deserialize(new TypeDescriptor(object.getClass()));
            assertThat(deserialized).isEqualTo(object);
        }
    }

    @Test
    public void testDeserializersAsJson() throws Exception {
        assertThat(factory.isApplicable(factory.getDefaultType()))
                .as("Factory %s (%s) should be applicable for type %s", factory.getClass().getName(), factory.getName(),
                        factory.getDefaultType().getType().getName())
                .isTrue();

        List<Object> objects = createObjectsOfType(factory.getDefaultType());

        for (Object object : objects) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Serializer serializer = factory.createSerializer(new SerialDataWriter(os), new TypeDescriptor(object.getClass()));
            serializer.serialize(object);

            byte[] bytes = os.toByteArray();

            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            Deserializer<String> deserializer = factory.createDeserializer(new SerialDataReader(is));
            String deserialized = deserializer.deserialize(DeserializationTypes.JSON_TYPE);
            assertThat(deserialized).isNotNull();
            assertThat(deserialized).isNotEmpty();

            new ObjectMapper().readValue(deserialized, Object.class);
        }
    }

    private List<Object> createObjectsOfType(TypeDescriptor typeDescriptor) {
        List<Object> objects = new ArrayList<>();

        // Default constructor
        objects.add(create(typeDescriptor.getType()));

        // Add Integers
        objects.add(create(typeDescriptor.getType(), 0));
        objects.add(create(typeDescriptor.getType(), 1));
        objects.add(create(typeDescriptor.getType(), 2));
        objects.add(create(typeDescriptor.getType(), 10));
        objects.add(create(typeDescriptor.getType(), 100));
        objects.add(create(typeDescriptor.getType(), 1000));
        objects.add(create(typeDescriptor.getType(), -1));
        objects.add(create(typeDescriptor.getType(), -1000));
        // Add Longs
        objects.add(create(typeDescriptor.getType(), 0L));
        objects.add(create(typeDescriptor.getType(), 1L));
        objects.add(create(typeDescriptor.getType(), 2L));
        objects.add(create(typeDescriptor.getType(), 10L));
        objects.add(create(typeDescriptor.getType(), 100L));
        objects.add(create(typeDescriptor.getType(), 1000L));
        objects.add(create(typeDescriptor.getType(), -1L));
        objects.add(create(typeDescriptor.getType(), -1000L));
        // Add Doubles
        objects.add(create(typeDescriptor.getType(), 0.0));
        objects.add(create(typeDescriptor.getType(), 0.1));
        objects.add(create(typeDescriptor.getType(), 1000.1));

        // Add Strings
        objects.add(create(typeDescriptor.getType(), "A"));
        objects.add(create(typeDescriptor.getType(), "BB"));
        objects.add(create(typeDescriptor.getType(), "CCC"));
        objects.add(create(typeDescriptor.getType(), "`~!@#$%^&*()-_=+[]{};:\'\",./\\<>?"));

        // Add Enums
        objects.add(create(typeDescriptor.getType(), RoundingMode.UP));
        objects.add(create(typeDescriptor.getType(), RoundingMode.DOWN));

        // Add Collections
        objects.add(create(typeDescriptor.getType(), new ArrayList(Arrays.asList())));
        objects.add(create(typeDescriptor.getType(), new ArrayList(Arrays.asList(0, 1, 2))));
        objects.add(create(typeDescriptor.getType(), new ArrayList(Arrays.asList("X", "YY", "ZZZ"))));
        // Add Maps
        objects.add(create(typeDescriptor.getType(), new HashMap(Collections.emptyMap())));
        objects.add(create(typeDescriptor.getType(), new HashMap(Collections.singletonMap("A", "BB"))));
        objects.add(create(typeDescriptor.getType(), new HashMap(Collections.singletonMap(1, 33))));
        // Add Bitset
        objects.add(create(typeDescriptor.getType(), new BitSet()));
        objects.add(create(typeDescriptor.getType(), BitSet.valueOf(new long[] {1234567L})));

        // Add Objects
        objects.add(create(typeDescriptor.getType(), new LatLon(1.2, 3.4)));
        objects.add(create(typeDescriptor.getType(), new ObjectWithDate(new Date(123456890L))));


        // Filter objects
        objects = objects.stream().filter(o -> o != null).collect(Collectors.toList());

        if (typeDescriptor.getType() == Object.class) {
            objects = objects.stream().filter(o -> {
                try {
                    return !o.getClass().getPackage().getName().startsWith("java") && o.getClass().getConstructor() != null;
                } catch (NoSuchMethodException e) {
                    return false;
                }
            }).collect(Collectors.toList());
        }

        System.out.println(typeDescriptor.getType() + " " + objects.size());
        assertThat(objects.size()).as("No suitable object is created").isGreaterThan(0);

        return objects;
    }

    private Object create(Class clazz, Object... parameters) {
        if (parameters.length == 1 && clazz.isAssignableFrom(parameters[0].getClass())) {
            return parameters[0];
        }
        Class[] parameterTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramClass = parameters[i].getClass();
            if (paramClass == Integer.class) {
                paramClass = int.class;
            } else if (paramClass == Long.class) {
                paramClass = long.class;
            } else if (paramClass == Double.class) {
                paramClass = double.class;
            }
            parameterTypes[i] = paramClass;
        }
        return create(clazz, parameterTypes, parameters);
    }

    private Object create(Class clazz, Class[] parameterTypes, Object... parameters) {
        try {
            return clazz.getConstructor(parameterTypes).newInstance(parameters);
        } catch (Exception e) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].getSuperclass() != null) {
                    Class[] newParameterTypes = parameterTypes.clone();
                    newParameterTypes[i] = newParameterTypes[i].getSuperclass();
                    Object o = create(clazz, newParameterTypes, parameters);
                    if (o != null) {
                        return o;
                    }
                }
            }
            return null;
        }
    }
}