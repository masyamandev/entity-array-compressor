package com.masyaman.datapack.utils;

public class MathUtils {

    // TODO: works for length=3 only
    public static long median(long... values) {
        int max = 0, min = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] > values[max]) {
                max = i;
            }
            if (values[i] < values[min]) {
                min = i;
            }
        }
        for (int i = 0; i < values.length; i++) {
            if (i != max && i != min) {
                return values[i];
            }
        }
        return 0;
    }
}
