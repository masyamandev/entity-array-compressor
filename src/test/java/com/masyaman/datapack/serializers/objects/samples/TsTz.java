package com.masyaman.datapack.serializers.objects.samples;

public class TsTz {
    protected long ts;
    protected int tz;

    public TsTz() {
    }

    public TsTz(long ts, int tz) {
        this.ts = ts;
        this.tz = tz;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getTz() {
        return tz;
    }

    public void setTz(int tz) {
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

    @Override
    public String toString() {
        return "TsTz{" +
                "ts=" + ts +
                ", tz=" + tz +
                '}';
    }
}
