package com.masyaman.datapack.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(Parameterized.class)
public class ObjectIdCachePerformanceTest {

    private Class<? extends ObjectIdCache> clazz;
    private List constructorParams;

    private ObjectIdCache cache;

    public ObjectIdCachePerformanceTest(Class<? extends ObjectIdCache> clazz, List constructorParams) {
        this.clazz = clazz;
        this.constructorParams = constructorParams;
    }

    @Before
    public void init() throws Exception {
        Constructor<? extends ObjectIdCache> constructor = clazz.getDeclaredConstructor(int.class);// TODO
        this.cache = constructor.newInstance(constructorParams.toArray());
    }

    @Parameterized.Parameters(name = "{0} {1}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                {ObjectIdCacheTree.class, Arrays.asList(2)},
                {ObjectIdCacheTree.class, Arrays.asList(4)},
                {ObjectIdCacheTree.class, Arrays.asList(6)},
                {ObjectIdCacheTree.class, Arrays.asList(8)},
                {ObjectIdCacheTree.class, Arrays.asList(10)},
                {ObjectIdCacheTree.class, Arrays.asList(20)},
                {ObjectIdCacheTree.class, Arrays.asList(30)},
                {ObjectIdCacheTree.class, Arrays.asList(40)},
                {ObjectIdCacheTree.class, Arrays.asList(50)},
                {ObjectIdCacheTree.class, Arrays.asList(60)},
                {ObjectIdCacheTree.class, Arrays.asList(70)},
                {ObjectIdCacheTree.class, Arrays.asList(80)},
                {ObjectIdCacheTree.class, Arrays.asList(90)},
                {ObjectIdCacheTree.class, Arrays.asList(100)},
                {ObjectIdCacheTree.class, Arrays.asList(1000)},
                {ObjectIdCacheTree.class, Arrays.asList(10000)},
                {ObjectIdCacheTree.class, Arrays.asList(100000)},
                {ObjectIdCacheTree.class, Arrays.asList(1000000)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(2)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(4)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(6)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(8)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(10)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(20)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(30)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(40)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(50)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(60)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(70)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(80)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(90)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(100)},
                {ObjectIdCacheRingBuffer.class, Arrays.asList(1000)},
//                {ObjectIdCacheRingBuffer.class, Arrays.asList(10000)},
                {ObjectIdCacheList.class, Arrays.asList(2)},
                {ObjectIdCacheList.class, Arrays.asList(4)},
                {ObjectIdCacheList.class, Arrays.asList(6)},
                {ObjectIdCacheList.class, Arrays.asList(8)},
                {ObjectIdCacheList.class, Arrays.asList(10)},
                {ObjectIdCacheList.class, Arrays.asList(100)},
                {ObjectIdCacheList.class, Arrays.asList(1000)},
//                {ObjectIdCacheList.class, Arrays.asList(10000)},
        });
    }

    @Test
    public void testPerformance() throws Exception {

        long startInitTime = System.nanoTime();
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < cache.maxSize() * 10; i++) {
            values.add(i);
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead(values.get(i));
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
        long initTime = System.nanoTime() - startInitTime;
        System.out.println(String.format("Init time %.3f", initTime / 1000000000.0));

        for (int i = 0; i < 20; i++) {
            long startTime = System.nanoTime();
            runPerformance(values, 100000);
            long time = System.nanoTime() - startTime;
            System.out.println(String.format("Run time %.3f", time / 1000000000.0));
        }
    }

    private void runPerformance(List values, int operations) {
        Random random = new Random(123456890);
        int maxRandom = values.size();

        for (int i = 0; i < operations; i++) {
            switch (random.nextInt(3)) {
                case 0:
                    String nextVal = "Val" + random.nextInt(random.nextBoolean() ? cache.maxSize() : maxRandom);
                    if (!cache.contains(nextVal)) {
                        cache.addHead(nextVal);
                    }
                    break;
                case 1:
                    Object val = values.get(random.nextInt(cache.maxSize()));
                    cache.removeElement(val);
                    cache.addHead(val);
                    break;
                case 2:
                    int pos = random.nextInt(cache.size());
                    Object element = cache.removePosition(pos);
                    assertThat(element).isNotNull();
                    cache.addHead(element);
                    break;
            }
        }
    }
}