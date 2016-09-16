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


    private static final long[] longScales;
    static {
        longScales = new long[20];
        longScales[0] = 1;
        for (int i = 1; i < 20; i++) {
            longScales[i] = longScales[i - 1] * 10;
        }
    }

    public static <E extends Number> E scale(E number, int scale) {
        if (number == null) {
            return null;
        } else if (scale == 0) {
            return number;
        } else if (scale > 0) {
            if (number instanceof Integer) {
                return (E) new Integer((int) (number.longValue() * longScales[scale]));
            } else if (number instanceof Long) {
                return (E) new Long(number.longValue() * longScales[scale]);
            } else if (number instanceof Double) {
                return (E) new Double(number.doubleValue() * longScales[scale]);
            } else if (number instanceof Float) {
                return (E) new Float(number.doubleValue() * longScales[scale]);
            }
        } else if (scale < 0) {
            if (number instanceof Integer) {
                return (E) new Integer((int) scaleLong(number.longValue(), scale));
            } else if (number instanceof Long) {
                return (E) new Long(scaleLong(number.longValue(), scale));
            } else if (number instanceof Double) {
                return (E) new Double(number.doubleValue() / longScales[-scale]);
            } else if (number instanceof Float) {
                return (E) new Float(number.doubleValue() / longScales[-scale]);
            }
        }
        return null;
    }

    private static long scaleLong(long value, int scale) {
        long sign = value >= 0 ? 1 : -1;
        return ((value + sign * longScales[-scale] / 2) / longScales[-scale]);
    }

    public static Long round(Number number) {
        if (number == null) {
            return null;
        }

        if (number instanceof Integer) {
            return number.longValue();
        } else if (number instanceof Long) {
            return number.longValue();
        } else if (number instanceof Double) {
            return Math.round(number.doubleValue());
        } else if (number instanceof Float) {
            return Math.round(number.doubleValue());
        }
        return null;
    }
}
