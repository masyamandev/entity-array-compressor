package com.masyaman.datapack.randombeans;

import com.masyaman.datapack.randombeans.objects.BeanWithNumbers;
import com.masyaman.datapack.randombeans.objects.BeanWithNumbersAndCollections;
import com.masyaman.datapack.randombeans.objects.BeanWithStrings;
import com.masyaman.datapack.randombeans.objects.SampleEnum;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;

import java.util.*;

public class ObjectsRandomizator {

    public static final float SCALE_FLOAT = 1000000f;
    public static final double SCALE_DOUBLE = 1000000d;

    private Random random;
    private EnhancedRandom enhancedRandom;

    public ObjectsRandomizator() {
        this(new Random(), 50);
    }

    public ObjectsRandomizator(Random random, int collectionsSize) {
        this.random = random;
        this.enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .maxCollectionSize(collectionsSize)
                .randomize(Integer.class, new IntegerRandomizer())
                .randomize(Long.class, new LongRandomizer())
                .randomize(Float.class, new FloatRandomizer())
                .randomize(Double.class, new DoubleRandomizer())
                .randomize(int.class, new NotNullRandomizer(new IntegerRandomizer()))
                .randomize(long.class, new NotNullRandomizer(new LongRandomizer()))
                .randomize(float.class, new NotNullRandomizer(new FloatRandomizer()))
                .randomize(double.class, new NotNullRandomizer(new DoubleRandomizer()))
                .randomize(Number.class, new NumberRandomizer())
                .randomize(String.class, new StringRandomizer())
                .randomize(BitSet.class, new BitSetRandomizer())
//                .randomize(BeanWithNumbers.class, new RandomType<>(BeanWithNumbers.class, BeanWithNumbersAndCollections.class))
                .randomize(Object.class, new RandomType<>(SampleEnum.class,
                        BeanWithStrings.class, BeanWithNumbers.class, BeanWithNumbersAndCollections.class,
                        Integer.class, Long.class, Float.class, Double.class, String.class))
                .build();
    }

    public EnhancedRandom getEnhancedRandom() {
        return enhancedRandom;
    }



    public class IntegerRandomizer implements Randomizer<Integer> {
        private List<Integer> randomInts = new ArrayList<>();

        public IntegerRandomizer() {
            randomize();
        }

        private void randomize() {
            for (int i = 0; i < 32; i++) {
                randomInts.add(null);
                randomInts.add(random.nextInt());
                int l = 1 << i;
                randomInts.add(l);
                randomInts.add(l - 1);
                randomInts.add(l + 1);
                randomInts.add(-l);
                randomInts.add(-l - 1);
                randomInts.add(-l + 1);
            }
            Collections.shuffle(randomInts, random);
        }

        @Override
        public Integer getRandomValue() {
            if (randomInts.isEmpty()) {
                randomize();
            }
            return randomInts.remove(randomInts.size() - 1);
        }
    }

    public class LongRandomizer implements Randomizer<Long> {
        private List<Long> randomLongs = new ArrayList<>();

        public LongRandomizer() {
            randomize();
        }

        private void randomize() {
            for (int i = 0; i < 64; i++) {
                randomLongs.add(null);
                randomLongs.add(random.nextLong());
                long l = 1L << i;
                randomLongs.add(l);
                randomLongs.add(l - 1);
                randomLongs.add(l + 1);
                randomLongs.add(-l);
                randomLongs.add(-l - 1);
                randomLongs.add(-l + 1);
            }
            Collections.shuffle(randomLongs, random);
        }

        @Override
        public Long getRandomValue() {
            if (randomLongs.isEmpty()) {
                randomize();
            }
            return randomLongs.remove(randomLongs.size() - 1);
        }
    }

    public class FloatRandomizer implements Randomizer<Float> {
        @Override
        public Float getRandomValue() {
            return random.nextInt(10) == 0 ? null : (random.nextInt() >> 16) / SCALE_FLOAT;
        }
    }

    public class DoubleRandomizer implements Randomizer<Double> {
        @Override
        public Double getRandomValue() {
            return random.nextInt(10) == 0 ? null : random.nextInt() / SCALE_DOUBLE;
        }
    }

    public class NotNullRandomizer<T> implements Randomizer<T> {
        private Randomizer<T> randomizer;

        public NotNullRandomizer(Randomizer<T> randomizer) {
            this.randomizer = randomizer;
        }

        @Override
        public T getRandomValue() {
            T value = null;
            while (value == null) {
                value = randomizer.getRandomValue();
            }
            return value;
        }
    }

    public class RandomOf<T> implements Randomizer<T> {
        private Randomizer<? extends T>[] randomizers;

        public RandomOf(Randomizer<? extends T>... randomizers) {
            this.randomizers = randomizers;
        }

        @Override
        public T getRandomValue() {
            int r = random.nextInt(randomizers.length + 1);
            return r >= randomizers.length ? null : randomizers[r].getRandomValue();
        }
    }

    public class RandomType<T> implements Randomizer<T> {
        private Class<? extends T>[] classes;

        public RandomType(Class<? extends T>... classes) {
            this.classes = classes;
        }

        @Override
        public T getRandomValue() {
            int r = random.nextInt(classes.length + 1);
            return r >= classes.length ? null : enhancedRandom.nextObject(classes[r]);
        }
    }

    public class NumberRandomizer extends RandomOf<Number> {
        public NumberRandomizer() {
            super(new IntegerRandomizer(), new LongRandomizer(), new FloatRandomizer(), new DoubleRandomizer());
        }
    }

    public class StringRandomizer implements Randomizer<String> {
        @Override
        public String getRandomValue() {
            switch (random.nextInt(3)) {
                case 0: return "String" + random.nextInt(4);
                case 1: return String.format("%h", random.nextInt());
                default: return null;
            }
        }
    }

    public class BitSetRandomizer implements Randomizer<BitSet> {
        @Override
        public BitSet getRandomValue() {
            int size;
            int maxRandom;
            switch (random.nextInt(3)) {
                case 0:
                    size = random.nextInt(5);
                    maxRandom = random.nextInt(100);
                    break;
                case 1:
                    size = random.nextInt(50);
                    maxRandom = random.nextInt(1000);
                    break;
                default:
                    return null;
            }
            BitSet bitSet = new BitSet();
            for (int i = 0; i < size; i++) {
                bitSet.set(random.nextInt(maxRandom + 1));
            }
            return bitSet;
        }
    }

}
