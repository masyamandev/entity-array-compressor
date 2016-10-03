package com.masyaman.datapack.compare;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeed;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeedDataLoss;
import com.masyaman.datapack.compare.objects.GpsPositionWithSpeedOptimized;
import com.masyaman.datapack.streams.*;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
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
import static org.assertj.core.api.Assertions.assertThat;

public class CompareGpsTrack {

    @Test
    public void test() throws Exception {

        String resourceName = "/samples/gpsTrack1.csv";
        List<GpsPositionWithSpeed> events = parseCsv(GpsPositionWithSpeed.class, resourceName);
        List<GpsPositionWithSpeedOptimized> eventsOptimized = parseCsv(GpsPositionWithSpeedOptimized.class, resourceName);
        List<GpsPositionWithSpeedDataLoss> eventsDataLoss = parseCsv(GpsPositionWithSpeedDataLoss.class, resourceName);

        System.out.println("Got " + events.size() + " objects of type " + events.get(0).getClass().getSimpleName());

        testSerialization("BinaryStream", binarySerialize(true), events);
        testSerialization("BinaryOptimized", binarySerialize(true), eventsOptimized);
        testSerialization("BinaryDataLoss", binarySerialize(false), eventsDataLoss);
        testSerialization("Csv", csvSerialize(), events);
        testSerialization("Json", jsonSerialize(), events);
        testSerialization("Smile", smileSerialize(), events);
        testSerialization("KryoAll", kryoSerializeAll(), events);
        testSerialization("KryoByOne", kryoSerializeByOne(), events);
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

    private static DataSerializer binarySerialize(boolean testDeserialization) throws Exception {
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
            }

            return serialized;
        };
    }


    private static void testSerialization(String name, DataSerializer dataSerializer, List<?> data) throws Exception {
//        System.out.println("\n\n");

        byte[] allBytes = dataSerializer.serialize(data);
        byte[] gzipAllBytes = gzipBytes(allBytes);
        System.out.println(String.format("Serializer %s, serialized size %s, compressed %s",
                name, allBytes.length, gzipAllBytes.length));
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
