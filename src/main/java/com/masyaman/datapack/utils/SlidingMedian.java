package com.masyaman.datapack.utils;

import java.util.Arrays;

// Unoptimized code. However for small window side should work fine.
public class SlidingMedian {

    private long[] values;
    private long[] temp;
    private int index = 0;
    private int size = 0;

    public SlidingMedian(int size) {
        if (size <= 0 || (size & 1) == 0) {
            throw new IllegalArgumentException("Median size should be positive and odd");
        }
        values = new long[size];
        temp = new long[size];
    }

    public void pushValue(long value) {
        values[index] = value;
        index = (index + 1) % values.length;
        size = Math.min(size + 1, values.length);
    }

    public long median() {
        // round to odd
        int sizeOdd = (this.size - 1) / 2 * 2 + 1;
        System.arraycopy(values, 0, temp, 0, sizeOdd);
        Arrays.sort(temp, 0, sizeOdd);
        return temp[sizeOdd / 2];
    }
}
