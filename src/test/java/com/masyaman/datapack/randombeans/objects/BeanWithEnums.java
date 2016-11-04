package com.masyaman.datapack.randombeans.objects;

import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
import com.masyaman.datapack.serializers.enums.EnumsConstantsSerializationFactory;
import com.masyaman.datapack.serializers.enums.EnumsSerializationFactory;

import java.util.List;
import java.util.Set;

public class BeanWithEnums {

    public SampleEnum sampleEnum;

    @SerializeBy(EnumsConstantsSerializationFactory.class)
    public SampleEnum constantEnum;
    @SerializeBy(EnumsSerializationFactory.class)
    public SampleEnum cachedEnum;

    @SerializeValueBy(EnumsConstantsSerializationFactory.class)
    public List<SampleEnum> constantEnumList;
    @SerializeValueBy(EnumsSerializationFactory.class)
    public List<SampleEnum> cachedEnumList;

    @SerializeValueBy(EnumsConstantsSerializationFactory.class)
    public Set<SampleEnum> constantEnumSet;
    @SerializeValueBy(EnumsSerializationFactory.class)
    public Set<SampleEnum> cachedEnumSet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanWithEnums that = (BeanWithEnums) o;

        if (sampleEnum != that.sampleEnum) return false;
        if (constantEnum != that.constantEnum) return false;
        if (cachedEnum != that.cachedEnum) return false;
        if (constantEnumList != null ? !constantEnumList.equals(that.constantEnumList) : that.constantEnumList != null)
            return false;
        if (cachedEnumList != null ? !cachedEnumList.equals(that.cachedEnumList) : that.cachedEnumList != null)
            return false;
        if (constantEnumSet != null ? !constantEnumSet.equals(that.constantEnumSet) : that.constantEnumSet != null)
            return false;
        return cachedEnumSet != null ? cachedEnumSet.equals(that.cachedEnumSet) : that.cachedEnumSet == null;

    }

    @Override
    public int hashCode() {
        int result = sampleEnum != null ? sampleEnum.hashCode() : 0;
        result = 31 * result + (constantEnum != null ? constantEnum.hashCode() : 0);
        result = 31 * result + (cachedEnum != null ? cachedEnum.hashCode() : 0);
        result = 31 * result + (constantEnumList != null ? constantEnumList.hashCode() : 0);
        result = 31 * result + (cachedEnumList != null ? cachedEnumList.hashCode() : 0);
        result = 31 * result + (constantEnumSet != null ? constantEnumSet.hashCode() : 0);
        result = 31 * result + (cachedEnumSet != null ? cachedEnumSet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BeanWithEnums{" +
                "sampleEnum=" + sampleEnum +
                ", constantEnum=" + constantEnum +
                ", cachedEnum=" + cachedEnum +
                ", constantEnumList=" + constantEnumList +
                ", cachedEnumList=" + cachedEnumList +
                ", constantEnumSet=" + constantEnumSet +
                ", cachedEnumSet=" + cachedEnumSet +
                '}';
    }
}
