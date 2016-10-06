package com.masyaman.datapack.serializers.dates;

public class DatePrecisions {
    public static final int MILLIS = 0;
    public static final int SECOND = 1;
    public static final int MINUTE = 2;
    public static final int HOUR = 3;
    public static final int DAY = 4;

    public static final long[] SCALES = new long[] {1L, 1000L, 1000L * 60, 1000L * 60 * 60, 1000L * 60 * 60 * 24};
}
