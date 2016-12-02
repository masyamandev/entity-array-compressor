package com.masyaman.datapack.docexample;

import com.masyaman.datapack.annotations.Alias;
import com.masyaman.datapack.annotations.serialization.CacheSize;
import com.masyaman.datapack.annotations.serialization.Precision;
import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.serializers.numbers.NumberLinearSerializationFactory;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;

import java.math.RoundingMode;

@Alias("VehicleStatus")
public class VehicleStatus {

    // Use serializer for specified type. This field will be serialized strictly as GpsPosition object, inheritance
    // will not be allowed. If @SerializeBy annotation is omit, then serializer UnknownTypeSerializationFactory
    // will be used which could increase space, but allows to store inherited objects (e.g. GpsPositionWithAltitude).
    @SerializeBy(ObjectSerializationFactory.class)
    private GpsPosition gpsPosition;

    // By default NumberDiffSerializationFactory is used, difference to previous value will be stored.
    // Store 1 decimal digit in fraction. Default rounding mode is HALF_UP.
    @Precision(1)
    private double speed;

    // By default NumberDiffSerializationFactory is used, difference to previous value will be stored.
    // Precision can be negative, in this case timestamp will be serialized as seconds instead of millis.
    @Precision(value = -3, roundingMode = RoundingMode.FLOOR)
    private long timestamp;

    // Assuming radioStation will not be switched too often, so we can cache recent values and write only ids from cache.
    @CacheSize(20)
    @SerializeBy(StringCachedSerializationFactory.class)
    private String radioStation;

    // Constructors and getters

    public VehicleStatus() {}

    public VehicleStatus(GpsPosition gpsPosition, double speed, long timestamp, String radioStation) {
        this.gpsPosition = gpsPosition;
        this.speed = speed;
        this.timestamp = timestamp;
        this.radioStation = radioStation;
    }

    @Alias("GpsPosition")
    public static class GpsPosition {

        // Use linear prediction based on 2 previous values. Default precision is 6 decimal digits.
        @SerializeBy(NumberLinearSerializationFactory.class)
        private double lat;

        // Use linear prediction based on 2 previous values. Default precision is 6 decimal digits.
        @SerializeBy(NumberLinearSerializationFactory.class)
        private double lon;

        // Constructors and getters

        public GpsPosition() {}

        public GpsPosition(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
