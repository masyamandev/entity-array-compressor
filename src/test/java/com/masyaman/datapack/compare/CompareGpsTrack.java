package com.masyaman.datapack.compare;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeed;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeedDataLoss;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeedInts;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeedOptimized;
import com.masyaman.datapack.compare.objects.protobuf.GpsPositionWithSpeed.GpsPositionWithSpeedProto;
import com.masyaman.datapack.settings.SettingsHandler;
import com.masyaman.datapack.settings.SettingsKeys;
import com.masyaman.datapack.streams.*;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static com.masyaman.datapack.compare.objects.CsvResourceParser.parseCsv;
import static com.masyaman.datapack.reflection.TypeDescriptor.JSON;
import static com.masyaman.datapack.reflection.TypeDescriptor.JSON_WITH_TYPES;
import static org.assertj.core.api.Assertions.assertThat;

public class CompareGpsTrack {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private List<GpsPositionWithSpeed> events;
    private List<GpsPositionWithSpeedOptimized> eventsOptimized;
    private List<GpsPositionWithSpeedDataLoss> eventsDataLoss;
    private List<GpsPositionWithSpeed> eventsDiff;
    private List<GpsPositionWithSpeedInts> eventsInt;
    private List<GpsPositionWithSpeedInts> eventsIntDiff;
    private int warmUpCycles;
    private int testCycles;

    @Before
    public void setUp() throws Exception {

        String resourceName = "/samples/gpsTrack1.csv";
        events = parseCsv(GpsPositionWithSpeed.class, resourceName);
        eventsOptimized = parseCsv(GpsPositionWithSpeedOptimized.class, resourceName);
        eventsDataLoss = parseCsv(GpsPositionWithSpeedDataLoss.class, resourceName);

        eventsDiff = new ArrayList<>();
        eventsDiff.add(events.get(0));
        for (int i = 1; i < events.size(); i++) {
            GpsPositionWithSpeed prev = events.get(i - 1);
            GpsPositionWithSpeed next = events.get(i);
            GpsPositionWithSpeed event = new GpsPositionWithSpeed();
            event.setLat(next.getLat() - prev.getLat());
            event.setLon(next.getLon() - prev.getLon());
            event.setSpeed(next.getSpeed() - prev.getSpeed());
            event.setTimestamp(next.getTimestamp() - prev.getTimestamp());
            eventsDiff.add(event);
        }

        eventsInt = new ArrayList<>();
        for (GpsPositionWithSpeed event : events) {
            eventsInt.add(new GpsPositionWithSpeedInts(event));
        }

        eventsIntDiff = new ArrayList<>();
        for (GpsPositionWithSpeed event : eventsDiff) {
            eventsIntDiff.add(new GpsPositionWithSpeedInts(event));
        }

        System.out.println("Got " + events.size() + " objects of type " + events.get(0).getClass().getSimpleName());

        warmUpCycles = 0;
        testCycles = 1;
    }

    @Test
    public void compareSerialization() throws Exception {
        warmUp();

        testSerialization("BinaryStream", binarySerialize(false, false), events);
        testSerialization("BinaryOptimized", binarySerialize(false, false), eventsOptimized);
        testSerialization("BinaryDataLoss", binarySerialize(false, false), eventsDataLoss);
        //testSerialization("BinaryInt", binarySerialize(false, false), eventsInt);
        //testSerialization("BinaryIntDiff", binarySerialize(false, false), eventsIntDiff);
        testSerialization("BufferedStream32k", bufferedSerialize(32000, false, false), events);
        testSerialization("BufferedOptimized32k", bufferedSerialize(32000, false, false), eventsOptimized);
        testSerialization("BufferedDataLoss32k", bufferedSerialize(32000, false, false), eventsDataLoss);
        //testSerialization("BufferedInt32k", bufferedSerialize(32000, false, false), eventsInt);
        //testSerialization("BufferedIntDiff32k", bufferedSerialize(32000, false, false), eventsIntDiff);
        testSerialization("BufferedStream1k", bufferedSerialize(1000, false, false), events);
        testSerialization("BufferedOptimized1k", bufferedSerialize(1000, false, false), eventsOptimized);
        testSerialization("BufferedDataLoss1k", bufferedSerialize(1000, false, false), eventsDataLoss);
        //testSerialization("BufferedInt1k", bufferedSerialize(1000, false, false), eventsInt);
        //testSerialization("BufferedIntDiff1k", bufferedSerialize(1000, false, false), eventsIntDiff);
        testSerialization("MultiGzipExperimentalStream", multiGzipSerialize(false, false), events);
        testSerialization("MultiGzipExperimentalOptimized", multiGzipSerialize(false, false), eventsOptimized);
        testSerialization("MultiGzipExperimentalDataLoss", multiGzipSerialize(false, false), eventsDataLoss);
        //testSerialization("MultiGzipExperimentalInt", multiGzipSerialize(false, false), eventsInt);
        //testSerialization("MultiGzipExperimentalIntDiff", multiGzipSerialize(false, false), eventsIntDiff);
        testSerialization("Csv", csvSerialize(), events);
        testSerialization("Json", jsonSerialize(), events);
        testSerialization("Smile", smileSerialize(), events);
        testSerialization("KryoAll", kryoSerializeAll(), events);
        testSerialization("KryoByOne", kryoSerializeByOne(), events);
        //testSerialization("Protobuf", protobufSerialize(), events);
        testSerialization("CsvDiff", csvSerialize(), eventsDiff);
        testSerialization("JsonDiff", jsonSerialize(), eventsDiff);
        testSerialization("SmileDiff", smileSerialize(), eventsDiff);
        testSerialization("KryoAllDiff", kryoSerializeAll(), eventsDiff);
        testSerialization("KryoByOneDiff", kryoSerializeByOne(), eventsDiff);
        //testSerialization("ProtobufDiff", protobufSerialize(), eventsDiff);
        testSerialization("CsvInt", csvSerialize(), eventsInt);
        testSerialization("JsonInt", jsonSerialize(), eventsInt);
        testSerialization("SmileInt", smileSerialize(), eventsInt);
        testSerialization("KryoAllInt", kryoSerializeAll(), eventsInt);
        testSerialization("KryoByOneInt", kryoSerializeByOne(), eventsInt);
        testSerialization("ProtobufInt", protobufSerialize(), eventsInt);
        testSerialization("CsvIntDiff", csvSerialize(), eventsIntDiff);
        testSerialization("JsonIntDiff", jsonSerialize(), eventsIntDiff);
        testSerialization("SmileIntDiff", smileSerialize(), eventsIntDiff);
        testSerialization("KryoAllIntDiff", kryoSerializeAll(), eventsIntDiff);
        testSerialization("KryoByOneIntDiff", kryoSerializeByOne(), eventsIntDiff);
        testSerialization("ProtobufIntDiff", protobufSerialize(), eventsIntDiff);
    }

    @Test
    public void testDeserialization() throws Exception {
        testSerialization("BinaryStream", binarySerialize(true, true), events);
        testSerialization("BinaryOptimized", binarySerialize(true, true), eventsOptimized);
        testSerialization("BufferedStream32k", bufferedSerialize(32000, true, true), events);
        testSerialization("BufferedOptimized32k", bufferedSerialize(32000, true, true), eventsOptimized);
        testSerialization("BufferedStream1k", bufferedSerialize(1000, true, true), events);
        testSerialization("BufferedOptimized1k", bufferedSerialize(1000, true, true), eventsOptimized);
        testSerialization("MultiGzipExperimentalStream", multiGzipSerialize(true, true), events);
        testSerialization("MultiGzipExperimentalOptimized", multiGzipSerialize(true, true), eventsOptimized);
    }

    @Test
    public void timeSimpleDeserialization() throws Exception {
        warmUp();

        testSerialization("Json", jsonSerializeDeserialize(new TypeReference<List<GpsPositionWithSpeed>>() {}), events);
        testSerialization("JsonInt", jsonSerializeDeserialize(new TypeReference<List<GpsPositionWithSpeedInts>>() {}), eventsInt);
        testSerialization("JsonIntDiff", jsonSerializeDeserialize(new TypeReference<List<GpsPositionWithSpeedInts>>() {}), eventsIntDiff);
        testSerialization("BinaryStream", binarySerialize(true, false), events);
        //testSerialization("BinaryOptimized", binarySerialize(true, false), eventsOptimized);
        testSerialization("BinaryInt", binarySerialize(true, false), eventsInt);
        //testSerialization("BinaryIntDiff", binarySerialize(true, false), eventsIntDiff);
        testSerialization("BufferedStream32k", bufferedSerialize(32000, true, false), events);
        //testSerialization("BufferedOptimized32k", bufferedSerialize(32000, true, false), eventsOptimized);
        testSerialization("BufferedStream32kInt", bufferedSerialize(32000, true, false), eventsInt);
        //testSerialization("BufferedStream32kIntDiff", bufferedSerialize(32000, true, false), eventsIntDiff);
        testSerialization("BufferedStream1k", bufferedSerialize(1000, true, false), events);
        //testSerialization("BufferedOptimized1k", bufferedSerialize(1000, true, false), eventsOptimized);
        testSerialization("BufferedStream1kInt", bufferedSerialize(1000, true, false), eventsInt);
        //testSerialization("BufferedStream1kIntDiff", bufferedSerialize(1000, true, false), eventsIntDiff);
        testSerialization("MultiGzipExperimentalStream", multiGzipSerialize(true, false), events);
        //testSerialization("MultiGzipExperimentalOptimized", multiGzipSerialize(true, false), eventsOptimized);
        testSerialization("MultiGzipExperimentalStreamInt", multiGzipSerialize(true, false), eventsInt);
        //testSerialization("MultiGzipExperimentalStreamIntDiff", multiGzipSerialize(true, false), eventsIntDiff);
    }

    private void warmUp() {
//        warmUpCycles = 50;
//        testCycles = 20;
    }

    private static DataSerializer csvSerialize() throws Exception {
        return e -> {
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {

                Class<?> clazz = e.get(0).getClass();
                List<String> headers = new ArrayList<>();
                for (Field field : clazz.getDeclaredFields()) {
                    headers.add(field.getName());
                }

                CsvWriterSettings settings = new CsvWriterSettings();
                settings.setNullValue("?");
                settings.setRowWriterProcessor(new BeanWriterProcessor<>(clazz));
                settings.setHeaders(headers.toArray(new String[0]));

                CsvWriter writer = new CsvWriter(byteStream, settings);
                writer.writeHeaders();
                for (Object o : e) {
                    writer.processRecord(o);
                }

                return byteStream.toByteArray();
            }
        };
    }

    private static DataSerializer jsonSerialize() throws Exception {
        return e -> new ObjectMapper().writeValueAsBytes(e);
    }

    private static DataSerializer jsonSerializeDeserialize(TypeReference typeReference) throws Exception {
        return e -> {
            byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(e);
            assertThat((Iterable<?>) OBJECT_MAPPER.readValue(bytes, typeReference)).isEqualTo(e);
            return bytes;
        };
    }

    private static DataSerializer smileSerialize() throws Exception {
        return e -> new ObjectMapper(new SmileFactory()).writeValueAsBytes(e);
    }

    private static DataSerializer kryoSerializeAll() throws Exception {
        return e -> {
            Kryo kryo = new Kryo();
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                 Output output = new Output(byteStream)) {
                kryo.writeObject(output, e);
                return byteStream.toByteArray();
            }
        };
    }

    private static DataSerializer kryoSerializeByOne() throws Exception {
        return e -> {
            Kryo kryo = new Kryo();
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                 Output output = new Output(byteStream)) {
                for (Object o : e) {
                    kryo.writeObject(output, o);
                }
                return byteStream.toByteArray();
            }
        };
    }

    private static DataSerializer protobufSerialize() throws Exception {
        return e -> {
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                for (Object o : e) {
                    GpsPositionWithSpeedInts pos = (GpsPositionWithSpeedInts) o;
                    GpsPositionWithSpeedProto proto = GpsPositionWithSpeedProto.newBuilder()
//                                    .setLat((int) Math.round(pos.getLat() * 1000000))
//                                    .setLon((int) Math.round(pos.getLon() * 1000000))
//                                    .setSpeed((int) Math.round(pos.getSpeed() * 10))
                                    .setLat(pos.getLat())
                                    .setLon(pos.getLon())
                                    .setSpeed(pos.getSpeed())
                                    .setTs(pos.getTimestamp())
                                    .build();
                    proto.writeTo(byteStream);
                }
                return byteStream.toByteArray();
            }
        };
    }

    private static DataSerializer binarySerialize(boolean testDeserialization, boolean testToJson) throws Exception {
        return e -> {
            byte[] serialized;
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                try (ObjectWriter serializer = new SerialDataWriter(byteStream)) {
                    for (Object event : e) {
                        serializer.writeObject(event);
                    }
                }
                serialized = byteStream.toByteArray();
            }

            if (testDeserialization) {
                try (ObjectReader objectReader = new SerialDataReader(new ByteArrayInputStream(serialized))) {
                    for (Object event : e) {
                        assertThat(objectReader.hasObjects()).isTrue();
                        Object deserialized = objectReader.readObject();
                        assertThat(deserialized).isEqualTo(event);
                    }
                    assertThat(objectReader.hasObjects()).isFalse();
                }
                if (testToJson) {
                    try (ObjectReader objectReader = new SerialDataReader(new ByteArrayInputStream(serialized))) {
                        for (Object event : e) {
                            assertThat(objectReader.hasObjects()).isTrue();
                            String deserialized = objectReader.readObject(JSON);
                            assertThat(deserialized).isNotEmpty();
                            assertThat(OBJECT_MAPPER.readValue(deserialized, Object.class)).isNotNull();
                        }
                        assertThat(objectReader.hasObjects()).isFalse();
                    }
                    try (ObjectReader objectReader = new SerialDataReader(new ByteArrayInputStream(serialized))) {
                        for (Object event : e) {
                            assertThat(objectReader.hasObjects()).isTrue();
                            String deserialized = objectReader.readObject(JSON_WITH_TYPES);
                            assertThat(deserialized).isNotEmpty();
                            assertThat(OBJECT_MAPPER.readValue(deserialized, Object.class)).isNotNull();
                        }
                        assertThat(objectReader.hasObjects()).isFalse();
                    }
                }
            }

            return serialized;
        };
    }

    private static DataSerializer bufferedSerialize(int bufferSize, boolean testDeserialization, boolean testToJson) throws Exception {
        return e -> {
            SettingsHandler settings = new SettingsHandler()
                    .set(SettingsKeys.BYTE_BUFFER_SIZE, bufferSize);
            byte[] serialized;
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                try (ObjectWriter serializer = new BufferedDataWriter(byteStream, settings)) {
                    for (Object event : e) {
                        serializer.writeObject(event);
                    }
                }
                serialized = byteStream.toByteArray();
            }

            if (testDeserialization) {
                try (ObjectReader objectReader = new BufferedDataReader(new ByteArrayInputStream(serialized))) {
                    for (Object event : e) {
                        assertThat(objectReader.hasObjects()).isTrue();
                        Object deserialized = objectReader.readObject();
                        assertThat(deserialized).isEqualTo(event);
                    }
                    assertThat(objectReader.hasObjects()).isFalse();
                }
                if (testToJson) {
                    try (ObjectReader objectReader = new BufferedDataReader(new ByteArrayInputStream(serialized))) {
                        for (Object event : e) {
                            assertThat(objectReader.hasObjects()).isTrue();
                            String deserialized = objectReader.readObject(JSON);
                            assertThat(deserialized).isNotEmpty();
                            assertThat(OBJECT_MAPPER.readValue(deserialized, Object.class)).isNotNull();
                        }
                        assertThat(objectReader.hasObjects()).isFalse();
                    }
                    try (ObjectReader objectReader = new BufferedDataReader(new ByteArrayInputStream(serialized))) {
                        for (Object event : e) {
                            assertThat(objectReader.hasObjects()).isTrue();
                            String deserialized = objectReader.readObject(JSON_WITH_TYPES);
                            assertThat(deserialized).isNotEmpty();
                            assertThat(OBJECT_MAPPER.readValue(deserialized, Object.class)).isNotNull();
                        }
                        assertThat(objectReader.hasObjects()).isFalse();
                    }
                }
            }

            return serialized;
        };
    }

    private static DataSerializer multiGzipSerialize(boolean testDeserialization, boolean testToJson) throws Exception {
        return e -> {
            byte[] serialized;
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                try (ObjectWriter serializer = new MultiGzipDataWriter(byteStream)) {
                    for (Object event : e) {
                        serializer.writeObject(event);
                    }
                }
                serialized = byteStream.toByteArray();
            }

            if (testDeserialization) {
                try (ObjectReader objectReader = new MultiGzipDataReader(new ByteArrayInputStream(serialized))) {
                    for (Object event : e) {
                        assertThat(objectReader.hasObjects()).isTrue();
                        Object deserialized = objectReader.readObject();
                        assertThat(deserialized).isEqualTo(event);
                    }
                    assertThat(objectReader.hasObjects()).isFalse();
                }
                if (testToJson) {
                    try (ObjectReader objectReader = new MultiGzipDataReader(new ByteArrayInputStream(serialized))) {
                        for (Object event : e) {
                            assertThat(objectReader.hasObjects()).isTrue();
                            String deserialized = objectReader.readObject(JSON);
                            assertThat(deserialized).isNotEmpty();
                            assertThat(OBJECT_MAPPER.readValue(deserialized, Object.class)).isNotNull();
                        }
                        assertThat(objectReader.hasObjects()).isFalse();
                    }
                    try (ObjectReader objectReader = new MultiGzipDataReader(new ByteArrayInputStream(serialized))) {
                        for (Object event : e) {
                            assertThat(objectReader.hasObjects()).isTrue();
                            String deserialized = objectReader.readObject(JSON_WITH_TYPES);
                            assertThat(deserialized).isNotEmpty();
                            assertThat(OBJECT_MAPPER.readValue(deserialized, Object.class)).isNotNull();
                        }
                        assertThat(objectReader.hasObjects()).isFalse();
                    }
                }
            }

            return serialized;
        };
    }


    private void testSerialization(String name, DataSerializer dataSerializer, List<?> data) throws Exception {
//        System.out.println("\n\n");

        for (int i = 0; i < warmUpCycles; i++) {
            dataSerializer.serialize(data);
        }

        byte[] allBytes = null;
        long start = System.nanoTime();
        for (int i = 0; i < testCycles; i++) {
            allBytes = dataSerializer.serialize(data);
        }
        long finish = System.nanoTime();
        long time = (finish - start) / testCycles;

        byte[] gzipAllBytes = gzipBytes(allBytes);
        System.out.println(String.format("Serializer %s, time %s ms, serialized size %s, compressed %s",
                name, time / 1000000 + "." + time % 1000000, allBytes.length, gzipAllBytes.length));
        String allBytesAsString = new String(allBytes);

        if (false) {
            Map<Class, List<Object>> eventsByType = data.stream()
                    .collect(Collectors.groupingBy(e -> ((Object) e).getClass()));

            int values = 0, size = 0, compressed = 0;
            for (Map.Entry<Class, List<Object>> entry : eventsByType.entrySet()) {
                byte[] bytes = dataSerializer.serialize(entry.getValue());
                byte[] gzipBytes = gzipBytes(bytes);
                String bytesAsString = new String(bytes);
                System.out.println(String.format("Class %s, values %s, size %s, compressed %s",
                        entry.getKey().getSimpleName(), entry.getValue().size(), bytes.length, gzipBytes.length));
                values += entry.getValue().size();
                size += bytes.length;
                compressed += gzipBytes.length;
            }

            System.out.println(String.format("Total values %s, size %s, compressed %s",
                    values, size, compressed));
        }
    }

    private static byte[] gzipBytes(byte[] data) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(data.length);
             GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
            zipStream.write(data);
            zipStream.finish();
            return byteStream.toByteArray();
        }
    }

    @FunctionalInterface
    private interface DataSerializer {
        byte[] serialize(List<?> data) throws Exception;
    }
}
