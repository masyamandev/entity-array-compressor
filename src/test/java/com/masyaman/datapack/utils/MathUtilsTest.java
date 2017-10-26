package com.masyaman.datapack.utils;

import org.junit.Test;

import java.math.RoundingMode;

import static com.masyaman.datapack.utils.MathUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MathUtilsTest {

    @Test
    public void testDoubleRoundingHalfUp() throws Exception {
        RoundingMode mode = RoundingMode.HALF_UP;

        assertThat(roundDouble(5.5, mode)).isEqualTo(6);
        assertThat(roundDouble(2.5, mode)).isEqualTo(3);
        assertThat(roundDouble(1.6, mode)).isEqualTo(2);
        assertThat(roundDouble(1.1, mode)).isEqualTo(1);
        assertThat(roundDouble(1.0, mode)).isEqualTo(1);
        assertThat(roundDouble(0.0, mode)).isEqualTo(0);
        assertThat(roundDouble(-1.0, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.1, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.6, mode)).isEqualTo(-2);
        assertThat(roundDouble(-2.5, mode)).isEqualTo(-3);
        assertThat(roundDouble(-5.5, mode)).isEqualTo(-6);
    }

    @Test
    public void testDoubleRoundingHalfDown() throws Exception {
        RoundingMode mode = RoundingMode.HALF_DOWN;

        assertThat(roundDouble(5.5, mode)).isEqualTo(5);
        assertThat(roundDouble(2.5, mode)).isEqualTo(2);
        assertThat(roundDouble(1.6, mode)).isEqualTo(2);
        assertThat(roundDouble(1.1, mode)).isEqualTo(1);
        assertThat(roundDouble(1.0, mode)).isEqualTo(1);
        assertThat(roundDouble(0.0, mode)).isEqualTo(0);
        assertThat(roundDouble(-1.0, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.1, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.6, mode)).isEqualTo(-2);
        assertThat(roundDouble(-2.5, mode)).isEqualTo(-2);
        assertThat(roundDouble(-5.5, mode)).isEqualTo(-5);
    }

    @Test
    public void testDoubleRoundingUp() throws Exception {
        RoundingMode mode = RoundingMode.UP;

        assertThat(roundDouble(5.5, mode)).isEqualTo(6);
        assertThat(roundDouble(2.5, mode)).isEqualTo(3);
        assertThat(roundDouble(1.6, mode)).isEqualTo(2);
        assertThat(roundDouble(1.1, mode)).isEqualTo(2);
        assertThat(roundDouble(1.0, mode)).isEqualTo(1);
        assertThat(roundDouble(0.0, mode)).isEqualTo(0);
        assertThat(roundDouble(-1.0, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.1, mode)).isEqualTo(-2);
        assertThat(roundDouble(-1.6, mode)).isEqualTo(-2);
        assertThat(roundDouble(-2.5, mode)).isEqualTo(-3);
        assertThat(roundDouble(-5.5, mode)).isEqualTo(-6);
    }

    @Test
    public void testDoubleRoundingDown() throws Exception {
        RoundingMode mode = RoundingMode.DOWN;

        assertThat(roundDouble(5.5, mode)).isEqualTo(5);
        assertThat(roundDouble(2.5, mode)).isEqualTo(2);
        assertThat(roundDouble(1.6, mode)).isEqualTo(1);
        assertThat(roundDouble(1.1, mode)).isEqualTo(1);
        assertThat(roundDouble(1.0, mode)).isEqualTo(1);
        assertThat(roundDouble(0.0, mode)).isEqualTo(0);
        assertThat(roundDouble(-1.0, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.1, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.6, mode)).isEqualTo(-1);
        assertThat(roundDouble(-2.5, mode)).isEqualTo(-2);
        assertThat(roundDouble(-5.5, mode)).isEqualTo(-5);
    }

    @Test
    public void testDoubleRoundingFloor() throws Exception {
        RoundingMode mode = RoundingMode.FLOOR;

        assertThat(roundDouble(5.5, mode)).isEqualTo(5);
        assertThat(roundDouble(2.5, mode)).isEqualTo(2);
        assertThat(roundDouble(1.6, mode)).isEqualTo(1);
        assertThat(roundDouble(1.1, mode)).isEqualTo(1);
        assertThat(roundDouble(1.0, mode)).isEqualTo(1);
        assertThat(roundDouble(0.0, mode)).isEqualTo(0);
        assertThat(roundDouble(-1.0, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.1, mode)).isEqualTo(-2);
        assertThat(roundDouble(-1.6, mode)).isEqualTo(-2);
        assertThat(roundDouble(-2.5, mode)).isEqualTo(-3);
        assertThat(roundDouble(-5.5, mode)).isEqualTo(-6);
    }

    @Test
    public void testDoubleRoundingCeiling() throws Exception {
        RoundingMode mode = RoundingMode.CEILING;

        assertThat(roundDouble(5.5, mode)).isEqualTo(6);
        assertThat(roundDouble(2.5, mode)).isEqualTo(3);
        assertThat(roundDouble(1.6, mode)).isEqualTo(2);
        assertThat(roundDouble(1.1, mode)).isEqualTo(2);
        assertThat(roundDouble(1.0, mode)).isEqualTo(1);
        assertThat(roundDouble(0.0, mode)).isEqualTo(0);
        assertThat(roundDouble(-1.0, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.1, mode)).isEqualTo(-1);
        assertThat(roundDouble(-1.6, mode)).isEqualTo(-1);
        assertThat(roundDouble(-2.5, mode)).isEqualTo(-2);
        assertThat(roundDouble(-5.5, mode)).isEqualTo(-5);
    }



    @Test
    public void testLongScaleRoundingHalfUp() throws Exception {
        RoundingMode mode = RoundingMode.HALF_UP;

        assertThat(scale(55L, -1, mode)).isEqualTo(6);
        assertThat(scale(25L, -1, mode)).isEqualTo(3);
        assertThat(scale(16L, -1, mode)).isEqualTo(2);
        assertThat(scale(11L, -1, mode)).isEqualTo(1);
        assertThat(scale(10L, -1, mode)).isEqualTo(1);
        assertThat(scale(0L, -1, mode)).isEqualTo(0);
        assertThat(scale(-10L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-11L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-16L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-25L, -1, mode)).isEqualTo(-3);
        assertThat(scale(-55L, -1, mode)).isEqualTo(-6);
    }

    @Test
    public void testLongScaleRoundingHalfDown() throws Exception {
        RoundingMode mode = RoundingMode.HALF_DOWN;

        assertThat(scale(55L, -1, mode)).isEqualTo(5);
        assertThat(scale(25L, -1, mode)).isEqualTo(2);
        assertThat(scale(16L, -1, mode)).isEqualTo(2);
        assertThat(scale(11L, -1, mode)).isEqualTo(1);
        assertThat(scale(10L, -1, mode)).isEqualTo(1);
        assertThat(scale(0L, -1, mode)).isEqualTo(0);
        assertThat(scale(-10L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-11L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-16L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-25L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-55L, -1, mode)).isEqualTo(-5);
    }

    @Test
    public void testLongScaleRoundingUp() throws Exception {
        RoundingMode mode = RoundingMode.UP;

        assertThat(scale(55L, -1, mode)).isEqualTo(6);
        assertThat(scale(25L, -1, mode)).isEqualTo(3);
        assertThat(scale(16L, -1, mode)).isEqualTo(2);
        assertThat(scale(11L, -1, mode)).isEqualTo(2);
        assertThat(scale(10L, -1, mode)).isEqualTo(1);
        assertThat(scale(0L, -1, mode)).isEqualTo(0);
        assertThat(scale(-10L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-11L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-16L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-25L, -1, mode)).isEqualTo(-3);
        assertThat(scale(-55L, -1, mode)).isEqualTo(-6);
    }

    @Test
    public void testLongScaleRoundingDown() throws Exception {
        RoundingMode mode = RoundingMode.DOWN;

        assertThat(scale(55L, -1, mode)).isEqualTo(5);
        assertThat(scale(25L, -1, mode)).isEqualTo(2);
        assertThat(scale(16L, -1, mode)).isEqualTo(1);
        assertThat(scale(11L, -1, mode)).isEqualTo(1);
        assertThat(scale(10L, -1, mode)).isEqualTo(1);
        assertThat(scale(0L, -1, mode)).isEqualTo(0);
        assertThat(scale(-10L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-11L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-16L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-25L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-55L, -1, mode)).isEqualTo(-5);
    }

    @Test
    public void testLongScaleRoundingFloor() throws Exception {
        RoundingMode mode = RoundingMode.FLOOR;

        assertThat(scale(55L, -1, mode)).isEqualTo(5);
        assertThat(scale(25L, -1, mode)).isEqualTo(2);
        assertThat(scale(16L, -1, mode)).isEqualTo(1);
        assertThat(scale(11L, -1, mode)).isEqualTo(1);
        assertThat(scale(10L, -1, mode)).isEqualTo(1);
        assertThat(scale(0L, -1, mode)).isEqualTo(0);
        assertThat(scale(-10L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-11L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-16L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-25L, -1, mode)).isEqualTo(-3);
        assertThat(scale(-55L, -1, mode)).isEqualTo(-6);
    }

    @Test
    public void testLongScaleRoundingCeiling() throws Exception {
        RoundingMode mode = RoundingMode.CEILING;

        assertThat(scale(55L, -1, mode)).isEqualTo(6);
        assertThat(scale(25L, -1, mode)).isEqualTo(3);
        assertThat(scale(16L, -1, mode)).isEqualTo(2);
        assertThat(scale(11L, -1, mode)).isEqualTo(2);
        assertThat(scale(10L, -1, mode)).isEqualTo(1);
        assertThat(scale(0L, -1, mode)).isEqualTo(0);
        assertThat(scale(-10L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-11L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-16L, -1, mode)).isEqualTo(-1);
        assertThat(scale(-25L, -1, mode)).isEqualTo(-2);
        assertThat(scale(-55L, -1, mode)).isEqualTo(-5);
    }


    @Test
    public void testDivLongsRoundingHalfUp() throws Exception {
        RoundingMode mode = RoundingMode.HALF_UP;

        assertThat(divLongs(4, 4, mode)).isEqualTo(1);
        assertThat(divLongs(5, 4, mode)).isEqualTo(1);
        assertThat(divLongs(6, 4, mode)).isEqualTo(2);
        assertThat(divLongs(7, 4, mode)).isEqualTo(2);
        assertThat(divLongs(8, 4, mode)).isEqualTo(2);
        assertThat(divLongs(-4, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-6, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-7, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-8, 4, mode)).isEqualTo(-2);

        assertThat(divLongs(3, 3, mode)).isEqualTo(1);
        assertThat(divLongs(4, 3, mode)).isEqualTo(1);
        assertThat(divLongs(5, 3, mode)).isEqualTo(2);
        assertThat(divLongs(6, 3, mode)).isEqualTo(2);
        assertThat(divLongs(-3, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-4, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 3, mode)).isEqualTo(-2);
        assertThat(divLongs(-6, 3, mode)).isEqualTo(-2);
    }

    @Test
    public void testDivLongsRoundingHalfDown() throws Exception {
        RoundingMode mode = RoundingMode.HALF_DOWN;

        assertThat(divLongs(4, 4, mode)).isEqualTo(1);
        assertThat(divLongs(5, 4, mode)).isEqualTo(1);
        assertThat(divLongs(6, 4, mode)).isEqualTo(1);
        assertThat(divLongs(7, 4, mode)).isEqualTo(2);
        assertThat(divLongs(8, 4, mode)).isEqualTo(2);
        assertThat(divLongs(-4, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-6, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-7, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-8, 4, mode)).isEqualTo(-2);

        assertThat(divLongs(3, 3, mode)).isEqualTo(1);
        assertThat(divLongs(4, 3, mode)).isEqualTo(1);
        assertThat(divLongs(5, 3, mode)).isEqualTo(2);
        assertThat(divLongs(6, 3, mode)).isEqualTo(2);
        assertThat(divLongs(-3, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-4, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 3, mode)).isEqualTo(-2);
        assertThat(divLongs(-6, 3, mode)).isEqualTo(-2);
    }

    @Test
    public void testDivLongsRoundingUp() throws Exception {
        RoundingMode mode = RoundingMode.UP;

        assertThat(divLongs(4, 4, mode)).isEqualTo(1);
        assertThat(divLongs(5, 4, mode)).isEqualTo(2);
        assertThat(divLongs(6, 4, mode)).isEqualTo(2);
        assertThat(divLongs(7, 4, mode)).isEqualTo(2);
        assertThat(divLongs(8, 4, mode)).isEqualTo(2);
        assertThat(divLongs(-4, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-6, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-7, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-8, 4, mode)).isEqualTo(-2);

        assertThat(divLongs(3, 3, mode)).isEqualTo(1);
        assertThat(divLongs(4, 3, mode)).isEqualTo(2);
        assertThat(divLongs(5, 3, mode)).isEqualTo(2);
        assertThat(divLongs(6, 3, mode)).isEqualTo(2);
        assertThat(divLongs(-3, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-4, 3, mode)).isEqualTo(-2);
        assertThat(divLongs(-5, 3, mode)).isEqualTo(-2);
        assertThat(divLongs(-6, 3, mode)).isEqualTo(-2);
    }

    @Test
    public void testDivLongsRoundingDown() throws Exception {
        RoundingMode mode = RoundingMode.DOWN;

        assertThat(divLongs(4, 4, mode)).isEqualTo(1);
        assertThat(divLongs(5, 4, mode)).isEqualTo(1);
        assertThat(divLongs(6, 4, mode)).isEqualTo(1);
        assertThat(divLongs(7, 4, mode)).isEqualTo(1);
        assertThat(divLongs(8, 4, mode)).isEqualTo(2);
        assertThat(divLongs(-4, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-6, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-7, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-8, 4, mode)).isEqualTo(-2);

        assertThat(divLongs(3, 3, mode)).isEqualTo(1);
        assertThat(divLongs(4, 3, mode)).isEqualTo(1);
        assertThat(divLongs(5, 3, mode)).isEqualTo(1);
        assertThat(divLongs(6, 3, mode)).isEqualTo(2);
        assertThat(divLongs(-3, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-4, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-6, 3, mode)).isEqualTo(-2);
    }

    @Test
    public void testDivLongsRoundingFloor() throws Exception {
        RoundingMode mode = RoundingMode.FLOOR;

        assertThat(divLongs(4, 4, mode)).isEqualTo(1);
        assertThat(divLongs(5, 4, mode)).isEqualTo(1);
        assertThat(divLongs(6, 4, mode)).isEqualTo(1);
        assertThat(divLongs(7, 4, mode)).isEqualTo(1);
        assertThat(divLongs(8, 4, mode)).isEqualTo(2);
        assertThat(divLongs(-4, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-6, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-7, 4, mode)).isEqualTo(-2);
        assertThat(divLongs(-8, 4, mode)).isEqualTo(-2);

        assertThat(divLongs(3, 3, mode)).isEqualTo(1);
        assertThat(divLongs(4, 3, mode)).isEqualTo(1);
        assertThat(divLongs(5, 3, mode)).isEqualTo(1);
        assertThat(divLongs(6, 3, mode)).isEqualTo(2);
        assertThat(divLongs(-3, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-4, 3, mode)).isEqualTo(-2);
        assertThat(divLongs(-5, 3, mode)).isEqualTo(-2);
        assertThat(divLongs(-6, 3, mode)).isEqualTo(-2);
    }

    @Test
    public void testDivLongsRoundingCeiling() throws Exception {
        RoundingMode mode = RoundingMode.CEILING;

        assertThat(divLongs(4, 4, mode)).isEqualTo(1);
        assertThat(divLongs(5, 4, mode)).isEqualTo(2);
        assertThat(divLongs(6, 4, mode)).isEqualTo(2);
        assertThat(divLongs(7, 4, mode)).isEqualTo(2);
        assertThat(divLongs(8, 4, mode)).isEqualTo(2);
        assertThat(divLongs(-4, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-6, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-7, 4, mode)).isEqualTo(-1);
        assertThat(divLongs(-8, 4, mode)).isEqualTo(-2);

        assertThat(divLongs(3, 3, mode)).isEqualTo(1);
        assertThat(divLongs(4, 3, mode)).isEqualTo(2);
        assertThat(divLongs(5, 3, mode)).isEqualTo(2);
        assertThat(divLongs(6, 3, mode)).isEqualTo(2);
        assertThat(divLongs(-3, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-4, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-5, 3, mode)).isEqualTo(-1);
        assertThat(divLongs(-6, 3, mode)).isEqualTo(-2);
    }

}