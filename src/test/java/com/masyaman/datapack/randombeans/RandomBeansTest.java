package com.masyaman.datapack.randombeans;

import com.masyaman.datapack.randombeans.objects.SuperBean;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.streams.SerialDataReader;
import com.masyaman.datapack.streams.SerialDataWriter;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RandomBeansTest {

    private int seed;
    private int length;
    private int collectionsSize;

    public RandomBeansTest(int seed, int length, int collectionsSize) {
        this.seed = seed;
        this.length = length;
        this.collectionsSize = collectionsSize;
    }

    @Parameterized.Parameters(name = "Seed: {0}, length: {1}, collections size: {2}")
    public static Collection parameters() {
        List params = new ArrayList<>();
        Random random = new Random();
        for (int i = 10; i <= 100; i+=10) {
            params.add(new Object[] {random.nextInt(), i, i / 2});
        }
//        return params;
        return Arrays.asList(new Object[][] {{-2077069663, 50, 20}});
    }

    @Test
    public void testRandomBeans() throws Exception {
        Random random = new Random(seed);
        EnhancedRandom enhancedRandom = new ObjectsRandomizator(random, collectionsSize).getEnhancedRandom();

        List<SuperBean> beans = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            beans.add(enhancedRandom.nextObject(SuperBean.class));
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataWriter writer = new SerialDataWriter(os);

        for (SuperBean bean : beans) {
            writer.writeObject(bean);
        }

        byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DataReader reader = new SerialDataReader(is);

        for (int i = 0; i < length; i++) {
            assertThat(reader.hasObjects()).isTrue();
            assertThat(reader.readObject()).describedAs("Failed on step %d", i).isEqualTo(beans.get(i));
        }
        assertThat(reader.hasObjects()).isFalse();
    }


}
