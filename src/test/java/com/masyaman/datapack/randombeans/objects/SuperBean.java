package com.masyaman.datapack.randombeans.objects;

import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;

import java.util.*;

public class SuperBean {

    public int primitiveInt;
    public long primitiveLong;
    public float primitiveFloat;
    public double primitiveDouble;

    public Integer objectInt;
    public Long objectLong;
    public Float objectFloat;
    public Double objectDouble;

    public String string;

    public Set<String> stringSet;
    public List<String> stringList;
    public Collection<String> stringCollection;

    @SerializeValueBy(UnknownTypeSerializationFactory.class)
    public Set<Number> numberSet;
    @SerializeValueBy(UnknownTypeSerializationFactory.class)
    public List<Number> numberList;
    @SerializeValueBy(UnknownTypeSerializationFactory.class)
    public Collection<Number> numberCollection;

    public Set<Object> objectSet;
    public List<Object> objectList;
    public Collection<Object> objectCollection;

    public SampleEnum sampleEnum;

    public Set<SampleEnum> sampleEnumSet;
    public List<SampleEnum> sampleEnumList;
    public Collection<SampleEnum> sampleEnumCollection;

    public Date date;
    public Object object;

    public BeanWithStrings beanWithStrings;
    public BeanWithEnums beanWithEnums;

    public BeanWithNumbers beanWithNumbers;
    public BeanWithNumbersAndCollections beanWithNumbersAndCollections;
    public List<BeanWithNumbers> beanWithNumbersList;
    public Set<BeanWithNumbers> beanWithNumbersSet;
    public Map<BeanWithNumbers, String> beanWithNumbersMapKey;
    public Map<String, BeanWithNumbers> beanWithNumbersMapValue;

    public BitSet bitSet;
    public List<BitSet> bitSetList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuperBean superBean = (SuperBean) o;

        if (primitiveInt != superBean.primitiveInt) return false;
        if (primitiveLong != superBean.primitiveLong) return false;
        if (Float.compare(superBean.primitiveFloat, primitiveFloat) != 0) return false;
        if (Double.compare(superBean.primitiveDouble, primitiveDouble) != 0) return false;
        if (objectInt != null ? !objectInt.equals(superBean.objectInt) : superBean.objectInt != null) return false;
        if (objectLong != null ? !objectLong.equals(superBean.objectLong) : superBean.objectLong != null) return false;
        if (objectFloat != null ? !objectFloat.equals(superBean.objectFloat) : superBean.objectFloat != null)
            return false;
        if (objectDouble != null ? !objectDouble.equals(superBean.objectDouble) : superBean.objectDouble != null)
            return false;
        if (string != null ? !string.equals(superBean.string) : superBean.string != null) return false;
        if (stringSet != null ? !stringSet.equals(superBean.stringSet) : superBean.stringSet != null) return false;
        if (stringList != null ? !stringList.equals(superBean.stringList) : superBean.stringList != null) return false;
        if (stringCollection != null ? !stringCollection.equals(superBean.stringCollection) : superBean.stringCollection != null)
            return false;
        if (numberSet != null ? !numberSet.equals(superBean.numberSet) : superBean.numberSet != null) return false;
        if (numberList != null ? !numberList.equals(superBean.numberList) : superBean.numberList != null) return false;
        if (numberCollection != null ? !numberCollection.equals(superBean.numberCollection) : superBean.numberCollection != null)
            return false;
        if (objectSet != null ? !objectSet.equals(superBean.objectSet) : superBean.objectSet != null) return false;
        if (objectList != null ? !objectList.equals(superBean.objectList) : superBean.objectList != null) return false;
        if (objectCollection != null ? !objectCollection.equals(superBean.objectCollection) : superBean.objectCollection != null)
            return false;
        if (sampleEnum != superBean.sampleEnum) return false;
        if (sampleEnumSet != null ? !sampleEnumSet.equals(superBean.sampleEnumSet) : superBean.sampleEnumSet != null)
            return false;
        if (sampleEnumList != null ? !sampleEnumList.equals(superBean.sampleEnumList) : superBean.sampleEnumList != null)
            return false;
        if (sampleEnumCollection != null ? !sampleEnumCollection.equals(superBean.sampleEnumCollection) : superBean.sampleEnumCollection != null)
            return false;
        if (date != null ? !date.equals(superBean.date) : superBean.date != null) return false;
        if (object != null ? !object.equals(superBean.object) : superBean.object != null) return false;
        if (beanWithStrings != null ? !beanWithStrings.equals(superBean.beanWithStrings) : superBean.beanWithStrings != null)
            return false;
        if (beanWithEnums != null ? !beanWithEnums.equals(superBean.beanWithEnums) : superBean.beanWithEnums != null)
            return false;
        if (beanWithNumbers != null ? !beanWithNumbers.equals(superBean.beanWithNumbers) : superBean.beanWithNumbers != null)
            return false;
        if (beanWithNumbersAndCollections != null ? !beanWithNumbersAndCollections.equals(superBean.beanWithNumbersAndCollections) : superBean.beanWithNumbersAndCollections != null)
            return false;
        if (beanWithNumbersList != null ? !beanWithNumbersList.equals(superBean.beanWithNumbersList) : superBean.beanWithNumbersList != null)
            return false;
        if (beanWithNumbersSet != null ? !beanWithNumbersSet.equals(superBean.beanWithNumbersSet) : superBean.beanWithNumbersSet != null)
            return false;
        if (beanWithNumbersMapKey != null ? !beanWithNumbersMapKey.equals(superBean.beanWithNumbersMapKey) : superBean.beanWithNumbersMapKey != null)
            return false;
        if (beanWithNumbersMapValue != null ? !beanWithNumbersMapValue.equals(superBean.beanWithNumbersMapValue) : superBean.beanWithNumbersMapValue != null)
            return false;
        if (bitSet != null ? !bitSet.equals(superBean.bitSet) : superBean.bitSet != null) return false;
        return bitSetList != null ? bitSetList.equals(superBean.bitSetList) : superBean.bitSetList == null;

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
        result = 31 * result + (objectInt != null ? objectInt.hashCode() : 0);
        result = 31 * result + (objectLong != null ? objectLong.hashCode() : 0);
        result = 31 * result + (objectFloat != null ? objectFloat.hashCode() : 0);
        result = 31 * result + (objectDouble != null ? objectDouble.hashCode() : 0);
        result = 31 * result + (string != null ? string.hashCode() : 0);
        result = 31 * result + (stringSet != null ? stringSet.hashCode() : 0);
        result = 31 * result + (stringList != null ? stringList.hashCode() : 0);
        result = 31 * result + (stringCollection != null ? stringCollection.hashCode() : 0);
        result = 31 * result + (numberSet != null ? numberSet.hashCode() : 0);
        result = 31 * result + (numberList != null ? numberList.hashCode() : 0);
        result = 31 * result + (numberCollection != null ? numberCollection.hashCode() : 0);
        result = 31 * result + (objectSet != null ? objectSet.hashCode() : 0);
        result = 31 * result + (objectList != null ? objectList.hashCode() : 0);
        result = 31 * result + (objectCollection != null ? objectCollection.hashCode() : 0);
        result = 31 * result + (sampleEnum != null ? sampleEnum.hashCode() : 0);
        result = 31 * result + (sampleEnumSet != null ? sampleEnumSet.hashCode() : 0);
        result = 31 * result + (sampleEnumList != null ? sampleEnumList.hashCode() : 0);
        result = 31 * result + (sampleEnumCollection != null ? sampleEnumCollection.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (beanWithStrings != null ? beanWithStrings.hashCode() : 0);
        result = 31 * result + (beanWithEnums != null ? beanWithEnums.hashCode() : 0);
        result = 31 * result + (beanWithNumbers != null ? beanWithNumbers.hashCode() : 0);
        result = 31 * result + (beanWithNumbersAndCollections != null ? beanWithNumbersAndCollections.hashCode() : 0);
        result = 31 * result + (beanWithNumbersList != null ? beanWithNumbersList.hashCode() : 0);
        result = 31 * result + (beanWithNumbersSet != null ? beanWithNumbersSet.hashCode() : 0);
        result = 31 * result + (beanWithNumbersMapKey != null ? beanWithNumbersMapKey.hashCode() : 0);
        result = 31 * result + (beanWithNumbersMapValue != null ? beanWithNumbersMapValue.hashCode() : 0);
        result = 31 * result + (bitSet != null ? bitSet.hashCode() : 0);
        result = 31 * result + (bitSetList != null ? bitSetList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SuperBean{" +
                "primitiveInt=" + primitiveInt +
                ", primitiveLong=" + primitiveLong +
                ", primitiveFloat=" + primitiveFloat +
                ", primitiveDouble=" + primitiveDouble +
                ", objectInt=" + objectInt +
                ", objectLong=" + objectLong +
                ", objectFloat=" + objectFloat +
                ", objectDouble=" + objectDouble +
                ", string='" + string + '\'' +
                ", stringSet=" + stringSet +
                ", stringList=" + stringList +
                ", stringCollection=" + stringCollection +
                ", numberSet=" + numberSet +
                ", numberList=" + numberList +
                ", numberCollection=" + numberCollection +
                ", objectSet=" + objectSet +
                ", objectList=" + objectList +
                ", objectCollection=" + objectCollection +
                ", sampleEnum=" + sampleEnum +
                ", sampleEnumSet=" + sampleEnumSet +
                ", sampleEnumList=" + sampleEnumList +
                ", sampleEnumCollection=" + sampleEnumCollection +
                ", date=" + date +
                ", object=" + object +
                ", beanWithStrings=" + beanWithStrings +
                ", beanWithEnums=" + beanWithEnums +
                ", beanWithNumbers=" + beanWithNumbers +
                ", beanWithNumbersAndCollections=" + beanWithNumbersAndCollections +
                ", beanWithNumbersList=" + beanWithNumbersList +
                ", beanWithNumbersSet=" + beanWithNumbersSet +
                ", beanWithNumbersMapKey=" + beanWithNumbersMapKey +
                ", beanWithNumbersMapValue=" + beanWithNumbersMapValue +
                ", bitSet=" + bitSet +
                ", bitSetList=" + bitSetList +
                '}';
    }
}
