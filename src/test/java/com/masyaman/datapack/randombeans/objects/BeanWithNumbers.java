package com.masyaman.datapack.randombeans.objects;

import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.serializers.numbers.*;

public class BeanWithNumbers {

    public int primitiveInt;
    public long primitiveLong;
    public float primitiveFloat;
    public double primitiveDouble;

    @SerializeBy(NumberDiffSerializationFactory.class)
    public Integer diffInt;
    @SerializeBy(NumberDiffSerializationFactory.class)
    public Long diffLong;
    @SerializeBy(NumberDiffSerializationFactory.class)
    public Float diffFloat;
    @SerializeBy(NumberDiffSerializationFactory.class)
    public Double diffDouble;

    @SerializeBy(NumberIncrementalSerializationFactory.class)
    public Integer incrementalInt;
    @SerializeBy(NumberIncrementalSerializationFactory.class)
    public Long incrementalLong;
    @SerializeBy(NumberIncrementalSerializationFactory.class)
    public Float incrementalFloat;
    @SerializeBy(NumberIncrementalSerializationFactory.class)
    public Double incrementalDouble;

    @SerializeBy(NumberLinearSerializationFactory.class)
    public Integer linearInt;
    @SerializeBy(NumberLinearSerializationFactory.class)
    public Long linearLong;
    @SerializeBy(NumberLinearSerializationFactory.class)
    public Float linearFloat;
    @SerializeBy(NumberLinearSerializationFactory.class)
    public Double linearDouble;

    @SerializeBy(NumberMedianSerializationFactory.class)
    public Integer medianInt;
    @SerializeBy(NumberMedianSerializationFactory.class)
    public Long medianLong;
    @SerializeBy(NumberMedianSerializationFactory.class)
    public Float medianFloat;
    @SerializeBy(NumberMedianSerializationFactory.class)
    public Double medianDouble;

    @SerializeBy(NumberSerializationFactory.class)
    public Integer numberInt;
    @SerializeBy(NumberSerializationFactory.class)
    public Long numberLong;
    @SerializeBy(NumberSerializationFactory.class)
    public Float numberFloat;
    @SerializeBy(NumberSerializationFactory.class)
    public Double numberDouble;

    @SerializeBy(UnsignedLongSerializationFactory.class)
    public Integer unsignedInt;
    @SerializeBy(UnsignedLongSerializationFactory.class)
    public Long unsignedLong;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanWithNumbers that = (BeanWithNumbers) o;

        if (primitiveInt != that.primitiveInt) return false;
        if (primitiveLong != that.primitiveLong) return false;
        if (Float.compare(that.primitiveFloat, primitiveFloat) != 0) return false;
        if (Double.compare(that.primitiveDouble, primitiveDouble) != 0) return false;
        if (diffInt != null ? !diffInt.equals(that.diffInt) : that.diffInt != null) return false;
        if (diffLong != null ? !diffLong.equals(that.diffLong) : that.diffLong != null) return false;
        if (diffFloat != null ? !diffFloat.equals(that.diffFloat) : that.diffFloat != null) return false;
        if (diffDouble != null ? !diffDouble.equals(that.diffDouble) : that.diffDouble != null) return false;
        if (incrementalInt != null ? !incrementalInt.equals(that.incrementalInt) : that.incrementalInt != null)
            return false;
        if (incrementalLong != null ? !incrementalLong.equals(that.incrementalLong) : that.incrementalLong != null)
            return false;
        if (incrementalFloat != null ? !incrementalFloat.equals(that.incrementalFloat) : that.incrementalFloat != null)
            return false;
        if (incrementalDouble != null ? !incrementalDouble.equals(that.incrementalDouble) : that.incrementalDouble != null)
            return false;
        if (linearInt != null ? !linearInt.equals(that.linearInt) : that.linearInt != null) return false;
        if (linearLong != null ? !linearLong.equals(that.linearLong) : that.linearLong != null) return false;
        if (linearFloat != null ? !linearFloat.equals(that.linearFloat) : that.linearFloat != null) return false;
        if (linearDouble != null ? !linearDouble.equals(that.linearDouble) : that.linearDouble != null) return false;
        if (medianInt != null ? !medianInt.equals(that.medianInt) : that.medianInt != null) return false;
        if (medianLong != null ? !medianLong.equals(that.medianLong) : that.medianLong != null) return false;
        if (medianFloat != null ? !medianFloat.equals(that.medianFloat) : that.medianFloat != null) return false;
        if (medianDouble != null ? !medianDouble.equals(that.medianDouble) : that.medianDouble != null) return false;
        if (numberInt != null ? !numberInt.equals(that.numberInt) : that.numberInt != null) return false;
        if (numberLong != null ? !numberLong.equals(that.numberLong) : that.numberLong != null) return false;
        if (numberFloat != null ? !numberFloat.equals(that.numberFloat) : that.numberFloat != null) return false;
        if (numberDouble != null ? !numberDouble.equals(that.numberDouble) : that.numberDouble != null) return false;
        if (unsignedInt != null ? !unsignedInt.equals(that.unsignedInt) : that.unsignedInt != null) return false;
        return unsignedLong != null ? unsignedLong.equals(that.unsignedLong) : that.unsignedLong == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = primitiveInt;
        result = 31 * result + (int) (primitiveLong ^ (primitiveLong >>> 32));
        result = 31 * result + (primitiveFloat != +0.0f ? Float.floatToIntBits(primitiveFloat) : 0);
        temp = Double.doubleToLongBits(primitiveDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (diffInt != null ? diffInt.hashCode() : 0);
        result = 31 * result + (diffLong != null ? diffLong.hashCode() : 0);
        result = 31 * result + (diffFloat != null ? diffFloat.hashCode() : 0);
        result = 31 * result + (diffDouble != null ? diffDouble.hashCode() : 0);
        result = 31 * result + (incrementalInt != null ? incrementalInt.hashCode() : 0);
        result = 31 * result + (incrementalLong != null ? incrementalLong.hashCode() : 0);
        result = 31 * result + (incrementalFloat != null ? incrementalFloat.hashCode() : 0);
        result = 31 * result + (incrementalDouble != null ? incrementalDouble.hashCode() : 0);
        result = 31 * result + (linearInt != null ? linearInt.hashCode() : 0);
        result = 31 * result + (linearLong != null ? linearLong.hashCode() : 0);
        result = 31 * result + (linearFloat != null ? linearFloat.hashCode() : 0);
        result = 31 * result + (linearDouble != null ? linearDouble.hashCode() : 0);
        result = 31 * result + (medianInt != null ? medianInt.hashCode() : 0);
        result = 31 * result + (medianLong != null ? medianLong.hashCode() : 0);
        result = 31 * result + (medianFloat != null ? medianFloat.hashCode() : 0);
        result = 31 * result + (medianDouble != null ? medianDouble.hashCode() : 0);
        result = 31 * result + (numberInt != null ? numberInt.hashCode() : 0);
        result = 31 * result + (numberLong != null ? numberLong.hashCode() : 0);
        result = 31 * result + (numberFloat != null ? numberFloat.hashCode() : 0);
        result = 31 * result + (numberDouble != null ? numberDouble.hashCode() : 0);
        result = 31 * result + (unsignedInt != null ? unsignedInt.hashCode() : 0);
        result = 31 * result + (unsignedLong != null ? unsignedLong.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BeanWithNumbers{" +
                "primitiveInt=" + primitiveInt +
                ", primitiveLong=" + primitiveLong +
                ", primitiveFloat=" + primitiveFloat +
                ", primitiveDouble=" + primitiveDouble +
                ", diffInt=" + diffInt +
                ", diffLong=" + diffLong +
                ", diffFloat=" + diffFloat +
                ", diffDouble=" + diffDouble +
                ", incrementalInt=" + incrementalInt +
                ", incrementalLong=" + incrementalLong +
                ", incrementalFloat=" + incrementalFloat +
                ", incrementalDouble=" + incrementalDouble +
                ", linearInt=" + linearInt +
                ", linearLong=" + linearLong +
                ", linearFloat=" + linearFloat +
                ", linearDouble=" + linearDouble +
                ", medianInt=" + medianInt +
                ", medianLong=" + medianLong +
                ", medianFloat=" + medianFloat +
                ", medianDouble=" + medianDouble +
                ", numberInt=" + numberInt +
                ", numberLong=" + numberLong +
                ", numberFloat=" + numberFloat +
                ", numberDouble=" + numberDouble +
                ", unsignedInt=" + unsignedInt +
                ", unsignedLong=" + unsignedLong +
                '}';
    }
}
