package com.masyaman.datapack.main;

import com.masyaman.datapack.annotations.deserialization.instances.AsJsonInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.streams.BufferedDataReader;
import com.masyaman.datapack.streams.MultiGzipDataReader;
import com.masyaman.datapack.streams.ObjectReader;
import com.masyaman.datapack.streams.SerialDataReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class ConsoleTool {

    public static void main(String[] args) {
        if (getParam(args, "help") != null || getParam(args, "h") != null || getParam(args, "?") != null) {
            printHelp();
            System.exit(0);
        }

        InputStream is = System.in;
        try {
            if (is.available() == 0) {
                System.err.println("No data in input stream!\n");
                printHelp();
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ObjectReader objectReader = null;
            String deserializer = getParam(args, "input", "serial");
            if ("serial".equals(deserializer)) {
                objectReader = new SerialDataReader(is);
            } else if ("serialgzip".equals(deserializer)) {
                objectReader = new SerialDataReader(new GZIPInputStream(is));
            } else if ("buffered".equals(deserializer)) {
                objectReader = new BufferedDataReader(is);
            } else if ("bufferedgzip".equals(deserializer)) {
                objectReader = new BufferedDataReader(new GZIPInputStream(is));
            } else if ("multigzip".equals(deserializer)) {
                objectReader = new MultiGzipDataReader(is);
            } else {
                System.err.println("No deserializer " + deserializer + " exists.");
                System.exit(1);
            }

            String typeField = getParam(args, "typeField", "");
            TypeDescriptor<String> type = new TypeDescriptor(String.class, new AsJsonInstance(false, typeField));

            while (objectReader.hasObjects()) {
                System.out.println(objectReader.readObject(type));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("Binary Stream to JSON tool.");
        System.out.println("Usage:");
        System.out.println("  java -jar entity-array-compressor.jar {options} <{input_file} >{output_file}");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -help //this screen");
        System.out.println("  -input=[serial, serialgzip, buffered, bufferedgzip, multigzip] //type of parser");
        //System.out.println("  -output=[json] //type of output");
        System.out.println("  -typeField=[field name] //field name in Json for original class name");
    }

    private static String getParam(String[] args, String param) {
        return getParam(args, param, null);
    }

    private static String getParam(String[] args, String param, String def) {
        for (String arg : args) {
            String[] split = arg.trim().replaceAll("^-+", "").split("=", 2);
            if (split[0].equals(param)) {
                return split.length >= 2 ? split[1] : "";
            }
        }
        return def;
    }
}
