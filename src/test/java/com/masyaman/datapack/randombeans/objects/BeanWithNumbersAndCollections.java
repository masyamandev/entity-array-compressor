package com.masyaman.datapack.randombeans.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanWithNumbersAndCollections extends BeanWithNumbers {

    public List<Integer> integerList;
    public List<Long> longList;
    public List<Float> floatList;
    public List<Double> doubleList;

    public Set<Integer> integerSet;
    public Set<Long> longSet;
    public Set<Float> floatSet;
    public Set<Double> doubleSet;

    public Map<Long, Integer> longToIntMap;
    public Map<Integer, Long> intToLongMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BeanWithNumbersAndCollections that = (BeanWithNumbersAndCollections) o;

        if (integerList != null ? !integerList.equals(that.integerList) : that.integerList != null) return false;
        if (longList != null ? !longList.equals(that.longList) : that.longList != null) return false;
        if (floatList != null ? !floatList.equals(that.floatList) : that.floatList != null) return false;
        if (doubleList != null ? !doubleList.equals(that.doubleList) : that.doubleList != null) return false;
        if (integerSet != null ? !integerSet.equals(that.integerSet) : that.integerSet != null) return false;
        if (longSet != null ? !longSet.equals(that.longSet) : that.longSet != null) return false;
        if (floatSet != null ? !floatSet.equals(that.floatSet) : that.floatSet != null) return false;
        if (doubleSet != null ? !doubleSet.equals(that.doubleSet) : that.doubleSet != null) return false;
        if (longToIntMap != null ? !longToIntMap.equals(that.longToIntMap) : that.longToIntMap != null) return false;
        return intToLongMap != null ? intToLongMap.equals(that.intToLongMap) : that.intToLongMap == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (integerList != null ? integerList.hashCode() : 0);
        result = 31 * result + (longList != null ? longList.hashCode() : 0);
        result = 31 * result + (floatList != null ? floatList.hashCode() : 0);
        result = 31 * result + (doubleList != null ? doubleList.hashCode() : 0);
        result = 31 * result + (integerSet != null ? integerSet.hashCode() : 0);
        result = 31 * result + (longSet != null ? longSet.hashCode() : 0);
        result = 31 * result + (floatSet != null ? floatSet.hashCode() : 0);
        result = 31 * result + (doubleSet != null ? doubleSet.hashCode() : 0);
        result = 31 * result + (longToIntMap != null ? longToIntMap.hashCode() : 0);
        result = 31 * result + (intToLongMap != null ? intToLongMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BeanWithNumbersAndCollections{" +
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
                ", integerList=" + integerList +
                ", longList=" + longList +
                ", floatList=" + floatList +
                ", doubleList=" + doubleList +
                ", integerSet=" + integerSet +
                ", longSet=" + longSet +
                ", floatSet=" + floatSet +
                ", doubleSet=" + doubleSet +
                ", longToIntMap=" + longToIntMap +
                ", intToLongMap=" + intToLongMap +
                '}';
    }
}
