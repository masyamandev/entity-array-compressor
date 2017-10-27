package com.masyaman.datapack.docexample;

import com.masyaman.datapack.streams.ObjectWriter;
import com.masyaman.datapack.streams.SerialDataWriter;
import com.masyaman.datapack.utils.ByteStream;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

// This code does not test anything, but it helps to write documentation
public class VehicleStatusBinary {

    @Test
    public void test() throws Exception {
        ByteStream byteStream = new ByteStream();
//      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectWriter serializer = new SerialDataWriter(byteStream);

        printArray(byteStream.getNewBytes());

        serializer.writeObject(new VehicleStatus.GpsPosition(0.000010, 0.000020));

        printArray(byteStream.getNewBytes());

        serializer.writeObject(new VehicleStatus.GpsPosition(0.000012, 0.000025));

        printArray(byteStream.getNewBytes());

        serializer.writeObject(new VehicleStatus.GpsPosition(0.000015, 0.000028));

        printArray(byteStream.getNewBytes());


        serializer.writeObject(new VehicleStatus(
                new VehicleStatus.GpsPosition(0.000015, 0.000020),
                20, 1000000L, "BestFm"));

        printArray(byteStream.getNewBytes());

        serializer.writeObject(new VehicleStatus(
                new VehicleStatus.GpsPosition(0.000018, 0.000025),
                20, 1010000L, "BestFm"));

        printArray(byteStream.getNewBytes());
    }

    private void printArray(byte[] bytes) {
        if (true) return;

        System.out.println("Len: " + bytes.length);
        for (byte b : bytes) {
            System.out.print(String.format(((b & 0xF) == b) ? "0x0%x, " : "0x%x, ", b & 0xFF));
        }
        System.out.println();
        for (byte b : bytes) {
            System.out.print(String.format(" %3d, ", b & 0xFF));
        }
        System.out.println();
        for (byte b : bytes) {
            System.out.print(String.format(((b & 0x1F) == b) ? " ???, " : " '%s', ", (char)b));
        }
        System.out.println();
//        for (byte b : bytes) {
//            System.out.print(String.format("'%s', ", (char)b));
//        }
//        System.out.println();

    }
}
