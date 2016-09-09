package com.masyaman.datapack.cache;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(Parameterized.class)
public class ObjectIdCacheTest {

    private Class<? extends ObjectIdCache> clazz;
    private List constructorParams;

    private ObjectIdCache cache;

    public ObjectIdCacheTest(Class<? extends ObjectIdCache> clazz, List constructorParams) {
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
                {ObjectIdCacheTree.class, Arrays.asList(100)},
                {ObjectIdCacheTree.class, Arrays.asList(1000)},
//                {ObjectIdCacheTree.class, Arrays.asList(10000)},
//                {ObjectIdCacheTree.class, Arrays.asList(100000)},
//                {ObjectIdCacheTree.class, Arrays.asList(1000000)},
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
    public void testPreConditions() throws Exception {
        assertThat(cache).isNotNull();
        assertThat(cache.size()).isEqualTo(0);
        assertThat(cache.maxSize()).isGreaterThan(0);
    }

    @Test
    public void testAddDifferentElements() throws Exception {
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead("Element" + i);
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead("NewElement" + i);
            assertThat(cache.size()).isEqualTo(cache.maxSize());
            assertThat(o).isEqualTo("Element" + i);
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.removePosition(cache.size() - 1);
            assertThat(o).isEqualTo("NewElement" + i);
            assertThat(cache.size()).isEqualTo(cache.maxSize() - i - 1);
        }
    }

    @Test
    public void testContains() throws Exception {
        for (int i = 0; i < cache.maxSize(); i++) {
            String element = "Element" + i;
            assertThat(cache.contains(element)).isFalse();
            Object o = cache.addHead(element);
            assertThat(cache.contains(element)).isTrue();
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            String newElement = "NewElement" + i;
            String oldElement = "Element" + i;

            assertThat(cache.contains(oldElement)).isTrue();
            assertThat(cache.contains(newElement)).isFalse();

            Object o = cache.addHead(newElement);
            assertThat(cache.size()).isEqualTo(cache.maxSize());
            assertThat(o).isEqualTo(oldElement);

            assertThat(cache.contains(oldElement)).isFalse();
            assertThat(cache.contains(newElement)).isTrue();
        }
        for (int i = 0; i < cache.maxSize(); i += 2) {
            cache.removeElement("NewElement" + i);
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            assertThat(cache.contains("NewElement" + i)).isEqualTo(i % 2 != 0);
        }
    }

    @Test
    public void testRemoveNotExistedElements() throws Exception {
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead("Element" + i);
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            int pos = cache.removeElement("NewElement" + i);
            assertThat(cache.size()).isEqualTo(cache.maxSize());
            assertThat(pos).isLessThan(0);
        }
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.removePosition(cache.size() - 1);
            assertThat(o).isEqualTo("Element" + i);
            assertThat(cache.size()).isEqualTo(cache.maxSize() - i - 1);
        }
    }

    @Test
    public void testRemoveNotExistedPositions() throws Exception {
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead("Element" + i);
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(cache.removePosition(-1)).isNull();
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(cache.removePosition(cache.size())).isNull();
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
    }

    @Test
    public void testAddAndRemoveByObject() throws Exception {
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead("Element" + i);
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
        for (int i = cache.maxSize() / 3; i < cache.maxSize() / 2; i++) {
            int prevSize = cache.size();
            int pos = cache.removeElement("Element" + i);
            assertThat(pos).isEqualTo(cache.maxSize() - i - 1);
            assertThat(cache.size()).isEqualTo(prevSize - 1);
        }
        for (int i = 0; i < cache.maxSize() / 3; i++) {
            int prevSize = cache.size();
            int pos = cache.removeElement("Element" + i);
            assertThat(pos).isEqualTo(cache.size());
            assertThat(cache.size()).isEqualTo(prevSize - 1);
        }
        for (int i = cache.maxSize() - 1; i >= cache.maxSize() / 2; i--) {
            int prevSize = cache.size();
            int pos = cache.removeElement("Element" + i);
            assertThat(pos).isEqualTo(0);
            assertThat(cache.size()).isEqualTo(prevSize - 1);
        }
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void testAddAndRemoveByPosition() throws Exception {
        for (int i = 0; i < cache.maxSize(); i++) {
            Object o = cache.addHead("Element" + i);
            assertThat(cache.size()).isEqualTo(i + 1);
            assertThat(o).isNull();
        }
        for (int i = cache.maxSize() / 3; i < cache.maxSize() / 2; i++) {
            int prevSize = cache.size();
            Object element = cache.removePosition(cache.maxSize() / 3);
            assertThat(element).isEqualTo("Element" + (cache.maxSize() - i - 1));
            assertThat(cache.size()).isEqualTo(prevSize - 1);
        }
        for (int i = 0; i < cache.maxSize() / 3; i++) {
            int prevSize = cache.size();
            Object element = cache.removePosition(0);
            assertThat(element).isEqualTo("Element" + (cache.maxSize() - i - 1));
            assertThat(cache.size()).isEqualTo(prevSize - 1);
        }
        for (int i = cache.maxSize() / 2; i < cache.maxSize(); i++) {
            int prevSize = cache.size();
            Object element = cache.removePosition(0);
            assertThat(element).isEqualTo("Element" + (cache.maxSize() - i - 1));
            assertThat(cache.size()).isEqualTo(prevSize - 1);
        }
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void testCompareWithReferenceImplementation() throws Exception {
        Random random = new Random(123456890);
        int maxRandom = cache.maxSize() * 10;
        ObjectIdCache referenceCache = new ObjectIdCacheList<>(cache.maxSize());

        for (int i = 0; i < cache.maxSize() * 100; i++) {
            switch (random.nextInt(3)) {
                case 0:
                    String nextVal = "Val" + random.nextInt(random.nextBoolean() ? cache.maxSize() : maxRandom);
                    boolean contains = cache.contains(nextVal);
                    assertThat(contains).isEqualTo(referenceCache.contains(nextVal));
                    if (!contains) {
                        assertThat(cache.addHead(nextVal)).isEqualTo(referenceCache.addHead(nextVal));
                    }
                    break;
                case 1:
                    String val = "Val" + random.nextInt(cache.maxSize());
                    assertThat(cache.removeElement(val)).isEqualTo(referenceCache.removeElement(val));
                    assertThat(cache.addHead(val)).isEqualTo(referenceCache.addHead(val));
                    break;
                case 2:
                    int pos = random.nextInt(cache.maxSize());
                    Object element = cache.removePosition(pos);
                    assertThat(element).isEqualTo(referenceCache.removePosition(pos));
                    if (element != null) {
                        assertThat(cache.addHead(element)).isEqualTo(referenceCache.addHead(element));
                    }
                    break;
            }
            assertThat(cache.size()).isEqualTo(referenceCache.size());
            assertThat(cache.maxSize()).isEqualTo(referenceCache.maxSize());
            if (i % cache.maxSize() == 0) {
                assertThat(cache.toString()).isEqualTo(referenceCache.toString());
            }
        }
        assertThat(cache.toString()).isEqualTo(referenceCache.toString());
    }

    @Test
    @Ignore("Slow test")
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

        for (int i = 0; i < 10; i++) {
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