package com.masyaman.datapack.utils;

import java.math.RoundingMode;

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

    public static <E extends Number> E scale(E number, int scale, RoundingMode roundingMode) {
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
                return (E) new Integer((int) scaleLong(number.longValue(), scale, roundingMode));
            } else if (number instanceof Long) {
                return (E) new Long(scaleLong(number.longValue(), scale, roundingMode));
            } else if (number instanceof Double) {
                return (E) new Double(number.doubleValue() / longScales[-scale]);
            } else if (number instanceof Float) {
                return (E) new Float(number.doubleValue() / longScales[-scale]);
            }
        }
        return null;
    }

    private static long scaleLong(long value, int scale, RoundingMode roundingMode) {
        long sign = value >= 0 ? 1 : -1;
        switch (roundingMode) {
            case HALF_UP:
                return (value + sign * longScales[-scale] / 2) / longScales[-scale];
            case HALF_DOWN:
                return (value + sign * (longScales[-scale] / 2 - 1)) / longScales[-scale];
            case UP:
                return (value + sign * (longScales[-scale] - 1)) / longScales[-scale];
            case DOWN:
                return (value) / longScales[-scale];
            case FLOOR:
                return (value - (sign < 0 ? (longScales[-scale] - 1) : 0)) / longScales[-scale];
            case CEILING:
                return (value + (sign > 0 ? (longScales[-scale] - 1) : 0)) / longScales[-scale];
            default:
                throw new UnsupportedOperationException("Rounding mode " + roundingMode + " is not supported");
        }
    }

    public static Long round(Number number, RoundingMode roundingMode) {
        if (number == null) {
            return null;
        }

        if (number instanceof Integer) {
            return number.longValue();
        } else if (number instanceof Long) {
            return number.longValue();
        } else if (number instanceof Double) {
            return roundDouble(number.doubleValue(), roundingMode);
        } else if (number instanceof Float) {
            return roundDouble(number.doubleValue(), roundingMode);
        }
        return null;
    }

    public static long roundDouble(double number, RoundingMode roundingMode) {
        switch (roundingMode) {
            case HALF_UP:
                return number >= 0.0 ? Math.round(number) : -Math.round(-number);
            case HALF_DOWN:
                return number >= 0.0 ? -Math.round(-number) : Math.round(number);
            case UP:
                return number >= 0.0 ? Math.round(Math.ceil(number)) : -Math.round(Math.ceil(-number));
            case DOWN:
                return (long) number;
            case FLOOR:
                return Math.round(Math.floor(number));
            case CEILING:
                return Math.round(Math.ceil(number));
            default:
                throw new UnsupportedOperationException("Rounding mode " + roundingMode + " is not supported");
        }
    }
}
