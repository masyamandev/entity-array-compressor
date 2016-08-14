package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectSerializationFactoryTest {

    public static final ObjectSerializationFactory FACTORY = ObjectSerializationFactory.INSTANCE;


    @Test
    public void testSimpleSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Serializer<TsTz> serializer = FACTORY.createSerializer(new DataWriter(os), new TypeDescriptor(TsTz.class));
        serializer.serialize(new TsTz(100000L, 234));
        serializer.serialize(new TsTz(100000L, 234));
        serializer.serialize(new TsTz(100000L, 234));

        byte[] bytes = os.toByteArray();
        System.out.println(bytes.length);
        System.out.println(new String(bytes));

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Deserializer<TsTz> deserializer = FACTORY.createDeserializer(new DataReader(is), new TypeDescriptor(TsTz.class));
        assertThat(deserializer.deserialize()).isEqualTo(new TsTz(100000L, 234));
        assertThat(deserializer.deserialize()).isEqualTo(new TsTz(100000L, 234));
        assertThat(deserializer.deserialize()).isEqualTo(new TsTz(100000L, 234));
    }

    @Test
    public void testObjectSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new DataWriter(os);
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(null, new TsTz(100000L, 234)));
        dw.writeObject(new LatLonTsTz(new LatLon(1.1, 2.2), null));
        dw.writeObject(new LatLonTsTz(null, null));
        dw.writeObject(null);

        byte[] bytes = os.toByteArray();
        System.out.println(bytes.length);
        System.out.println(new String(bytes));

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new DataReader(is);
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(null, new TsTz(100000L, 234)));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(new LatLon(1.1, 2.2), null));
        assertThat(dr.readObject()).isEqualTo(new LatLonTsTz(null, null));
        assertThat(dr.readObject()).isNull();
    }

    @Test
    public void testComplexObjectSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter dw = new DataWriter(os);
        dw.writeObject(new TsTz(100000L, 234));
        dw.writeObject(new TsTz(100000L, 234));
        dw.writeObject(new TsTz(100000L, 234));
        dw.writeObject(new LatLon(1.1, 2.2));

        byte[] bytes = os.toByteArray();
        System.out.println(bytes.length);
        System.out.println(new String(bytes));

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader dr = new DataReader(is);
        assertThat(dr.readObject()).isEqualTo(new TsTz(100000L, 234));
        assertThat(dr.readObject()).isEqualTo(new TsTz(100000L, 234));
        assertThat(dr.readObject()).isEqualTo(new TsTz(100000L, 234));
        assertThat(dr.readObject()).isEqualTo(new LatLon(1.1, 2.2));
    }


    private static class LatLonTsTz {
        private LatLon latLon;
        private TsTz tsTz;

        public LatLonTsTz() {
        }
        public LatLonTsTz(LatLon latLon, TsTz tsTz) {
            this.latLon = latLon;
            this.tsTz = tsTz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LatLonTsTz that = (LatLonTsTz) o;

            if (latLon != null ? !latLon.equals(that.latLon) : that.latLon != null) return false;
            return tsTz != null ? tsTz.equals(that.tsTz) : that.tsTz == null;

        }

        @Override
        public int hashCode() {
            int result = latLon != null ? latLon.hashCode() : 0;
            result = 31 * result + (tsTz != null ? tsTz.hashCode() : 0);
            return result;
        }
    }

    private static class LatLon {
        private double lat;
        private double lon;

        public LatLon() {
        }
        public LatLon(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LatLon latLon = (LatLon) o;

            if (Double.compare(latLon.lat, lat) != 0) return false;
            return Double.compare(latLon.lon, lon) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(lat);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(lon);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    private static class TsTz {
        private long ts;
        private long tz;

        public TsTz() {
        }
        public TsTz(long ts, long tz) {
            this.ts = ts;
            this.tz = tz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TsTz tsTz = (TsTz) o;

            if (ts != tsTz.ts) return false;
            return tz == tsTz.tz;

        }

        @Override
        public int hashCode() {
            int result = (int) (ts ^ (ts >>> 32));
            result = 31 * result + (int) (tz ^ (tz >>> 32));
            return result;
        }
    }


}